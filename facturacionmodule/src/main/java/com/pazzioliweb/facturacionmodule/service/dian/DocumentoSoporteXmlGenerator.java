package com.pazzioliweb.facturacionmodule.service.dian;

import com.pazzioliweb.facturacionmodule.config.DianConfig;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoRequestDTO;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Genera el XML UBL 2.1 del **Documento Soporte en Adquisiciones a No Obligados a Facturar**
 * conforme a la DIAN Colombia (Anexo Técnico 1.9, Resolución 167 de 2021).
 *
 * Diferencias frente a una factura electrónica (UblXmlGenerator):
 *  - Root element: cac:Invoice (UBL Invoice-2) pero CustomizationID = "05".
 *  - InvoiceTypeCode = "05".
 *  - El EMISOR del XML es el COMPRADOR (la empresa) — porque es ella la que
 *    expide el soporte. El RECEPTOR es el proveedor no obligado a facturar.
 *  - Es obligatorio el bloque WithholdingTaxTotal (retenciones aplicadas).
 *  - CUDS (Código Único de Documento Soporte) en lugar de CUFE.
 */
@Component
public class DocumentoSoporteXmlGenerator {

    private static final String NS_CBC = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private static final String NS_CAC = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
    private static final String NS_INV = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2";
    private static final String NS_EXT = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
    private static final String NS_STS = "dian:gov:co:facturaelectronica:Structures-2-1";
    private static final String NS_DS  = "http://www.w3.org/2000/09/xmldsig#";

    private final DianConfig dianConfig;

    public DocumentoSoporteXmlGenerator(DianConfig dianConfig) {
        this.dianConfig = dianConfig;
    }

    public String generarXml(DianDocumentoRequestDTO request, String cuds,
                              BigDecimal retefuente, BigDecimal reteiva, BigDecimal reteica) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element invoice = doc.createElementNS(NS_INV, "Invoice");
            invoice.setAttribute("xmlns:cbc", NS_CBC);
            invoice.setAttribute("xmlns:cac", NS_CAC);
            invoice.setAttribute("xmlns:ext", NS_EXT);
            invoice.setAttribute("xmlns:sts", NS_STS);
            invoice.setAttribute("xmlns:ds", NS_DS);
            doc.appendChild(invoice);

            // UBLExtensions y bloque DIAN (numeración, CUDS, etc.)
            Element ublExtensions = doc.createElementNS(NS_EXT, "ext:UBLExtensions");
            Element ext1 = doc.createElementNS(NS_EXT, "ext:UBLExtension");
            Element extContent1 = doc.createElementNS(NS_EXT, "ext:ExtensionContent");
            Element dianExt = doc.createElementNS(NS_STS, "sts:DianExtensions");
            Element invCtrl = doc.createElementNS(NS_STS, "sts:InvoiceControl");
            addText(doc, invCtrl, "sts:InvoiceAuthorization",
                    dianConfig.getResolucion().getNumero() != null ? dianConfig.getResolucion().getNumero() : "");
            Element period = doc.createElementNS(NS_STS, "sts:AuthorizationPeriod");
            addCbc(doc, period, "cbc:StartDate",
                    dianConfig.getResolucion().getFechaDesde() != null ? dianConfig.getResolucion().getFechaDesde() : "");
            addCbc(doc, period, "cbc:EndDate",
                    dianConfig.getResolucion().getFechaHasta() != null ? dianConfig.getResolucion().getFechaHasta() : "");
            invCtrl.appendChild(period);
            Element authRange = doc.createElementNS(NS_STS, "sts:AuthorizedInvoices");
            addText(doc, authRange, "sts:Prefix", request.getPrefijo() != null ? request.getPrefijo() : "");
            addText(doc, authRange, "sts:From", String.valueOf(dianConfig.getResolucion().getRangoDesde()));
            addText(doc, authRange, "sts:To",   String.valueOf(dianConfig.getResolucion().getRangoHasta()));
            invCtrl.appendChild(authRange);
            dianExt.appendChild(invCtrl);
            extContent1.appendChild(dianExt);
            ext1.appendChild(extContent1);
            ublExtensions.appendChild(ext1);

            // Extension para firma digital (vacía, se llena al firmar XAdES-BES)
            Element ext2 = doc.createElementNS(NS_EXT, "ext:UBLExtension");
            Element extContent2 = doc.createElementNS(NS_EXT, "ext:ExtensionContent");
            ext2.appendChild(extContent2);
            ublExtensions.appendChild(ext2);
            invoice.appendChild(ublExtensions);

            // Cabecera UBL — CustomizationID "05" = Documento Soporte
            addCbc(doc, invoice, "cbc:UBLVersionID", "UBL 2.1");
            addCbc(doc, invoice, "cbc:CustomizationID", "05");
            addCbc(doc, invoice, "cbc:ProfileID", "DIAN 2.1: documento soporte");
            addCbc(doc, invoice, "cbc:ProfileExecutionID", String.valueOf(dianConfig.getAmbiente()));
            addCbc(doc, invoice, "cbc:ID", (request.getPrefijo() != null ? request.getPrefijo() : "") + request.getConsecutivo());

            Element uuidEl = doc.createElementNS(NS_CBC, "cbc:UUID");
            uuidEl.setAttribute("schemeID", String.valueOf(dianConfig.getAmbiente()));
            uuidEl.setAttribute("schemeName", "CUDS-SHA384");
            uuidEl.setTextContent(cuds);
            invoice.appendChild(uuidEl);

            addCbc(doc, invoice, "cbc:IssueDate",
                    request.getFechaEmision().format(DateTimeFormatter.ISO_LOCAL_DATE));
            addCbc(doc, invoice, "cbc:IssueTime",
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "-05:00");

            Element typeCode = doc.createElementNS(NS_CBC, "cbc:InvoiceTypeCode");
            typeCode.setTextContent("05");
            invoice.appendChild(typeCode);
            addCbc(doc, invoice, "cbc:Note", "Documento Soporte en Adquisiciones a No Obligados a Facturar");
            addCbc(doc, invoice, "cbc:DocumentCurrencyCode", "COP");
            addCbc(doc, invoice, "cbc:LineCountNumeric",
                    String.valueOf(request.getLineas() != null ? request.getLineas().size() : 0));

            // En DS, el "Supplier" del XML es la EMPRESA COMPRADORA (que es quien emite el soporte).
            Element supplier = doc.createElementNS(NS_CAC, "cac:AccountingSupplierParty");
            addCbc(doc, supplier, "cbc:AdditionalAccountID", "1");
            String emRespFiscal = request.getEmisor() != null && request.getEmisor().getResponsabilidadFiscal() != null
                    && !request.getEmisor().getResponsabilidadFiscal().isBlank()
                    ? request.getEmisor().getResponsabilidadFiscal() : "R-99-PN";
            Element supParty = crearParty(doc, request.getEmisor().getRazonSocial(),
                    request.getEmisor().getNumeroIdentificacion(),
                    request.getEmisor().getTipoIdentificacion(),
                    emRespFiscal);
            supplier.appendChild(supParty);
            invoice.appendChild(supplier);

            // En DS, el "Customer" del XML es el PROVEEDOR no obligado a facturar.
            // Por definición es R-99-PN (no obligado), pero respetar si viene seteado.
            if (request.getReceptor() == null) {
                throw new IllegalStateException(
                    "Documento Soporte: el receptor (proveedor no obligado) es obligatorio. " +
                    "DianDocumentoRequestDTO.receptor está null."
                );
            }
            Element customer = doc.createElementNS(NS_CAC, "cac:AccountingCustomerParty");
            addCbc(doc, customer, "cbc:AdditionalAccountID",
                    "31".equals(request.getReceptor().getTipoIdentificacion()) ? "1" : "2");
            String rcRespFiscal = request.getReceptor() != null && request.getReceptor().getResponsabilidadFiscal() != null
                    && !request.getReceptor().getResponsabilidadFiscal().isBlank()
                    ? request.getReceptor().getResponsabilidadFiscal() : "R-99-PN";
            Element custParty = crearParty(doc, request.getReceptor().getNombre(),
                    request.getReceptor().getNumeroIdentificacion(),
                    request.getReceptor().getTipoIdentificacion(),
                    rcRespFiscal);
            customer.appendChild(custParty);
            invoice.appendChild(customer);

            // ── WithholdingTaxTotal (obligatorio en DS si hay retención) ──
            if (retefuente != null && retefuente.compareTo(BigDecimal.ZERO) > 0) {
                invoice.appendChild(crearRetencion(doc, "06", "ReteFuente", request.getBaseGravable(), retefuente));
            }
            if (reteiva != null && reteiva.compareTo(BigDecimal.ZERO) > 0) {
                invoice.appendChild(crearRetencion(doc, "05", "ReteIVA", request.getTotalIva(), reteiva));
            }
            if (reteica != null && reteica.compareTo(BigDecimal.ZERO) > 0) {
                invoice.appendChild(crearRetencion(doc, "07", "ReteICA", request.getBaseGravable(), reteica));
            }

            // TaxTotal (IVA general, si aplica)
            if (request.getTotalIva() != null && request.getTotalIva().compareTo(BigDecimal.ZERO) > 0) {
                Element taxTotal = doc.createElementNS(NS_CAC, "cac:TaxTotal");
                addAmount(doc, taxTotal, "cbc:TaxAmount", request.getTotalIva(), "COP");
                Element taxSub = doc.createElementNS(NS_CAC, "cac:TaxSubtotal");
                addAmount(doc, taxSub, "cbc:TaxableAmount", request.getBaseGravable(), "COP");
                addAmount(doc, taxSub, "cbc:TaxAmount", request.getTotalIva(), "COP");
                Element cat = doc.createElementNS(NS_CAC, "cac:TaxCategory");
                BigDecimal base = request.getBaseGravable() != null ? request.getBaseGravable() : BigDecimal.ZERO;
                BigDecimal iva = request.getTotalIva();
                String tasaIva = "0.00";
                if (base.compareTo(BigDecimal.ZERO) > 0) {
                    tasaIva = iva.multiply(BigDecimal.valueOf(100))
                            .divide(base, 2, RoundingMode.HALF_UP).toPlainString();
                }
                addCbc(doc, cat, "cbc:Percent", tasaIva);
                Element scheme = doc.createElementNS(NS_CAC, "cac:TaxScheme");
                addCbc(doc, scheme, "cbc:ID", "01");
                addCbc(doc, scheme, "cbc:Name", "IVA");
                cat.appendChild(scheme);
                taxSub.appendChild(cat);
                taxTotal.appendChild(taxSub);
                invoice.appendChild(taxTotal);
            }

            // Totales monetarios
            Element legalMonetary = doc.createElementNS(NS_CAC, "cac:LegalMonetaryTotal");
            addAmount(doc, legalMonetary, "cbc:LineExtensionAmount", request.getBaseGravable(), "COP");
            addAmount(doc, legalMonetary, "cbc:TaxExclusiveAmount", request.getBaseGravable(), "COP");
            addAmount(doc, legalMonetary, "cbc:TaxInclusiveAmount", request.getTotalFactura(), "COP");
            BigDecimal totalRet = (retefuente != null ? retefuente : BigDecimal.ZERO)
                    .add(reteiva != null ? reteiva : BigDecimal.ZERO)
                    .add(reteica != null ? reteica : BigDecimal.ZERO);
            BigDecimal payable = request.getTotalFactura() != null
                    ? request.getTotalFactura().subtract(totalRet) : BigDecimal.ZERO;
            addAmount(doc, legalMonetary, "cbc:PayableAmount", payable, "COP");
            invoice.appendChild(legalMonetary);

            // InvoiceLines
            if (request.getLineas() != null) {
                int i = 1;
                for (DianDocumentoRequestDTO.LineaDTO l : request.getLineas()) {
                    invoice.appendChild(crearLinea(doc, i++, l));
                }
            }

            return serializar(doc);
        } catch (Exception e) {
            throw new RuntimeException("Error generando XML Documento Soporte: " + e.getMessage(), e);
        }
    }

    private Element crearRetencion(Document doc, String taxSchemeId, String taxSchemeName,
                                    BigDecimal base, BigDecimal monto) {
        Element wt = doc.createElementNS(NS_CAC, "cac:WithholdingTaxTotal");
        addAmount(doc, wt, "cbc:TaxAmount", monto, "COP");
        Element sub = doc.createElementNS(NS_CAC, "cac:TaxSubtotal");
        addAmount(doc, sub, "cbc:TaxableAmount", base != null ? base : BigDecimal.ZERO, "COP");
        addAmount(doc, sub, "cbc:TaxAmount", monto, "COP");
        BigDecimal b = base != null ? base : BigDecimal.ZERO;
        String pct = "0.00";
        if (b.compareTo(BigDecimal.ZERO) > 0) {
            pct = monto.multiply(BigDecimal.valueOf(100)).divide(b, 2, RoundingMode.HALF_UP).toPlainString();
        }
        Element cat = doc.createElementNS(NS_CAC, "cac:TaxCategory");
        addCbc(doc, cat, "cbc:Percent", pct);
        Element scheme = doc.createElementNS(NS_CAC, "cac:TaxScheme");
        addCbc(doc, scheme, "cbc:ID", taxSchemeId);
        addCbc(doc, scheme, "cbc:Name", taxSchemeName);
        cat.appendChild(scheme);
        sub.appendChild(cat);
        wt.appendChild(sub);
        return wt;
    }

    private Element crearParty(Document doc, String nombre, String identificacion, String tipoId) {
        return crearParty(doc, nombre, identificacion, tipoId, "R-99-PN");
    }

    private Element crearParty(Document doc, String nombre, String identificacion, String tipoId, String responsabilidadFiscal) {
        Element party = doc.createElementNS(NS_CAC, "cac:Party");
        Element partyId = doc.createElementNS(NS_CAC, "cac:PartyIdentification");
        Element idEl = doc.createElementNS(NS_CBC, "cbc:ID");
        idEl.setAttribute("schemeID", "0");
        idEl.setAttribute("schemeName", tipoId != null ? tipoId : "31");
        idEl.setTextContent(identificacion != null ? identificacion : "");
        partyId.appendChild(idEl);
        party.appendChild(partyId);
        Element partyName = doc.createElementNS(NS_CAC, "cac:PartyName");
        addCbc(doc, partyName, "cbc:Name", nombre != null ? nombre : "");
        party.appendChild(partyName);
        // PartyLegalEntity con TaxLevelCode obligatorio en DS.
        Element pls = doc.createElementNS(NS_CAC, "cac:PartyLegalEntity");
        addCbc(doc, pls, "cbc:RegistrationName", nombre != null ? nombre : "");
        addCbc(doc, pls, "cbc:CompanyID", identificacion != null ? identificacion : "");
        addCbc(doc, pls, "cbc:TaxLevelCode", responsabilidadFiscal != null ? responsabilidadFiscal : "R-99-PN");
        party.appendChild(pls);
        return party;
    }

    private Element crearLinea(Document doc, int numero, DianDocumentoRequestDTO.LineaDTO l) {
        Element linea = doc.createElementNS(NS_CAC, "cac:InvoiceLine");
        addCbc(doc, linea, "cbc:ID", String.valueOf(numero));
        Element quantity = doc.createElementNS(NS_CBC, "cbc:InvoicedQuantity");
        quantity.setAttribute("unitCode", "94");
        quantity.setTextContent(l.getCantidad() != null ? String.valueOf(l.getCantidad()) : "0");
        linea.appendChild(quantity);
        addAmount(doc, linea, "cbc:LineExtensionAmount", l.getTotalLinea(), "COP");
        Element item = doc.createElementNS(NS_CAC, "cac:Item");
        addCbc(doc, item, "cbc:Description", l.getDescripcion() != null ? l.getDescripcion() : "");
        linea.appendChild(item);
        Element price = doc.createElementNS(NS_CAC, "cac:Price");
        addAmount(doc, price, "cbc:PriceAmount", l.getPrecioUnitario(), "COP");
        linea.appendChild(price);
        return linea;
    }

    private String serializar(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        StringWriter w = new StringWriter();
        t.transform(new DOMSource(doc), new StreamResult(w));
        return w.toString();
    }

    private void addCbc(Document doc, Element parent, String name, String text) {
        Element el = doc.createElementNS(NS_CBC, name);
        el.setTextContent(text != null ? text : "");
        parent.appendChild(el);
    }

    private void addText(Document doc, Element parent, String name, String text) {
        Element el = doc.createElementNS(NS_STS, name);
        el.setTextContent(text != null ? text : "");
        parent.appendChild(el);
    }

    private void addAmount(Document doc, Element parent, String name, BigDecimal value, String currency) {
        Element el = doc.createElementNS(NS_CBC, name);
        el.setAttribute("currencyID", currency);
        el.setTextContent(value != null ? value.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00");
        parent.appendChild(el);
    }
}
