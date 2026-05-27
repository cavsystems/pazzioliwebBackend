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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Genera XML UBL 2.1 para Nota Crédito y Nota Débito Electrónicas (DIAN Colombia).
 *
 * Diferencias clave vs Factura:
 *  - Root element: CreditNote / DebitNote (no Invoice)
 *  - Namespace específico: CreditNote-2 / DebitNote-2
 *  - cac:BillingReference apunta a la factura original (CUFE)
 *  - cbc:DiscrepancyResponse describe el motivo (concepto DIAN)
 *  - Tipo documento: 91 = NC, 92 = ND
 *
 * Las notas usan la misma estructura de líneas, totales, emisor/receptor que la factura,
 * por lo que reutilizamos UblXmlGenerator como helper para las partes comunes.
 */
@Component
public class UblNotaGenerator {

    private static final String NS_CBC = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private static final String NS_CAC = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
    private static final String NS_CN  = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2";
    private static final String NS_DN  = "urn:oasis:names:specification:ubl:schema:xsd:DebitNote-2";
    private static final String NS_EXT = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
    private static final String NS_STS = "dian:gov:co:facturaelectronica:Structures-2-1";
    private static final String NS_DS  = "http://www.w3.org/2000/09/xmldsig#";

    private final DianConfig dianConfig;

    public UblNotaGenerator(DianConfig dianConfig) {
        this.dianConfig = dianConfig;
    }

    /** Genera el XML UBL 2.1 para Nota Crédito (tipoDocumento=91). */
    public String generarXmlNotaCredito(DianDocumentoRequestDTO request, String cude) {
        return generarXmlNota(request, cude, true);
    }

    /** Genera el XML UBL 2.1 para Nota Débito (tipoDocumento=92). */
    public String generarXmlNotaDebito(DianDocumentoRequestDTO request, String cude) {
        return generarXmlNota(request, cude, false);
    }

    private String generarXmlNota(DianDocumentoRequestDTO request, String cude, boolean esCredito) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            String nsRaiz = esCredito ? NS_CN : NS_DN;
            String tagRaiz = esCredito ? "CreditNote" : "DebitNote";
            String tagLine = esCredito ? "CreditNoteLine" : "DebitNoteLine";
            String tagTypeCode = esCredito ? "CreditNoteTypeCode" : "DebitNoteTypeCode";

            // Root
            Element root = doc.createElementNS(nsRaiz, tagRaiz);
            root.setAttribute("xmlns:cbc", NS_CBC);
            root.setAttribute("xmlns:cac", NS_CAC);
            root.setAttribute("xmlns:ext", NS_EXT);
            root.setAttribute("xmlns:sts", NS_STS);
            root.setAttribute("xmlns:ds", NS_DS);
            doc.appendChild(root);

            // UBLExtensions (DIAN)
            Element ublExtensions = doc.createElementNS(NS_EXT, "ext:UBLExtensions");
            Element ublExtension = doc.createElementNS(NS_EXT, "ext:UBLExtension");
            Element extensionContent = doc.createElementNS(NS_EXT, "ext:ExtensionContent");

            Element dianExtensions = doc.createElementNS(NS_STS, "sts:DianExtensions");
            Element invoiceControl = doc.createElementNS(NS_STS, "sts:InvoiceControl");
            addTextElement(doc, invoiceControl, "sts:InvoiceAuthorization",
                    dianConfig.getResolucion().getNumero() != null ? dianConfig.getResolucion().getNumero() : "");

            Element authPeriod = doc.createElementNS(NS_STS, "sts:AuthorizationPeriod");
            addCbcElement(doc, authPeriod, "cbc:StartDate",
                    dianConfig.getResolucion().getFechaDesde() != null ? dianConfig.getResolucion().getFechaDesde() : "");
            addCbcElement(doc, authPeriod, "cbc:EndDate",
                    dianConfig.getResolucion().getFechaHasta() != null ? dianConfig.getResolucion().getFechaHasta() : "");
            invoiceControl.appendChild(authPeriod);

            Element authInvoices = doc.createElementNS(NS_STS, "sts:AuthorizedInvoices");
            addTextElement(doc, authInvoices, "sts:Prefix", request.getPrefijo());
            addTextElement(doc, authInvoices, "sts:From", String.valueOf(dianConfig.getResolucion().getRangoDesde()));
            addTextElement(doc, authInvoices, "sts:To", String.valueOf(dianConfig.getResolucion().getRangoHasta()));
            invoiceControl.appendChild(authInvoices);

            dianExtensions.appendChild(invoiceControl);

            Element invoiceSource = doc.createElementNS(NS_STS, "sts:InvoiceSource");
            addCbcElement(doc, invoiceSource, "cbc:IdentificationCode", "CO");
            dianExtensions.appendChild(invoiceSource);

            Element softwareProvider = doc.createElementNS(NS_STS, "sts:SoftwareProvider");
            addTextElement(doc, softwareProvider, "sts:ProviderID",
                    request.getEmisor() != null ? request.getEmisor().getNumeroIdentificacion() : "");
            addTextElement(doc, softwareProvider, "sts:SoftwareID",
                    dianConfig.getSoftware().getId() != null ? dianConfig.getSoftware().getId() : "");
            dianExtensions.appendChild(softwareProvider);

            addTextElement(doc, dianExtensions, "sts:SoftwareSecurityCode", cude.substring(0, Math.min(40, cude.length())));

            extensionContent.appendChild(dianExtensions);
            ublExtension.appendChild(extensionContent);
            ublExtensions.appendChild(ublExtension);

            // Extension placeholder para firma
            Element ublExtension2 = doc.createElementNS(NS_EXT, "ext:UBLExtension");
            Element extensionContent2 = doc.createElementNS(NS_EXT, "ext:ExtensionContent");
            ublExtension2.appendChild(extensionContent2);
            ublExtensions.appendChild(ublExtension2);
            root.appendChild(ublExtensions);

            // Cabecera UBL
            addCbcElement(doc, root, "cbc:UBLVersionID", "UBL 2.1");
            addCbcElement(doc, root, "cbc:CustomizationID", esCredito ? "20" : "30");
            addCbcElement(doc, root, "cbc:ProfileID", "DIAN 2.1");
            addCbcElement(doc, root, "cbc:ProfileExecutionID", String.valueOf(dianConfig.getAmbiente()));

            // ID
            addCbcElement(doc, root, "cbc:ID", request.getPrefijo() + request.getConsecutivo());

            // UUID (CUDE — código único de las notas)
            Element uuidEl = doc.createElementNS(NS_CBC, "cbc:UUID");
            uuidEl.setAttribute("schemeID", String.valueOf(dianConfig.getAmbiente()));
            uuidEl.setAttribute("schemeName", "CUDE-SHA384");
            uuidEl.setTextContent(cude);
            root.appendChild(uuidEl);

            // Fechas
            addCbcElement(doc, root, "cbc:IssueDate",
                    request.getFechaEmision().format(DateTimeFormatter.ISO_LOCAL_DATE));
            addCbcElement(doc, root, "cbc:IssueTime",
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "-05:00");

            // Tipo de documento (91=NC, 92=ND)
            Element tc = doc.createElementNS(NS_CBC, "cbc:" + tagTypeCode);
            tc.setTextContent(esCredito ? "91" : "92");
            root.appendChild(tc);

            // Note (motivo)
            String motivo = request.getRazonConcepto() != null ? request.getRazonConcepto()
                    : (esCredito ? "Nota Crédito de venta" : "Nota Débito de venta");
            addCbcElement(doc, root, "cbc:Note", motivo);

            addCbcElement(doc, root, "cbc:DocumentCurrencyCode", "COP");
            addCbcElement(doc, root, "cbc:LineCountNumeric",
                    String.valueOf(request.getLineas() != null ? request.getLineas().size() : 0));

            // ── DiscrepancyResponse (concepto DIAN) ──
            // Concepto NC: 1=Devolución, 2=Anulación, 3=Rebaja, 4=Descuento, 5=Otro
            // Concepto ND: 1=Intereses, 2=Gastos por cobrar, 3=Cambio del valor, 4=Otros
            Element discrepancy = doc.createElementNS(NS_CAC, "cac:DiscrepancyResponse");
            if (request.getDocumentoReferencia() != null) {
                addCbcElement(doc, discrepancy, "cbc:ReferenceID",
                        request.getDocumentoReferencia().getNumeroDocumento() != null
                            ? request.getDocumentoReferencia().getNumeroDocumento() : "");
            }
            int concepto = request.getCodigoConcepto() != null ? request.getCodigoConcepto() : (esCredito ? 5 : 4);
            addCbcElement(doc, discrepancy, "cbc:ResponseCode", String.valueOf(concepto));
            addCbcElement(doc, discrepancy, "cbc:Description", motivo);
            root.appendChild(discrepancy);

            // ── BillingReference (referencia a la factura original) ──
            if (request.getDocumentoReferencia() != null) {
                Element billRef = doc.createElementNS(NS_CAC, "cac:BillingReference");
                Element invoiceDocRef = doc.createElementNS(NS_CAC, "cac:InvoiceDocumentReference");
                addCbcElement(doc, invoiceDocRef, "cbc:ID",
                        request.getDocumentoReferencia().getNumeroDocumento() != null
                            ? request.getDocumentoReferencia().getNumeroDocumento() : "");
                if (request.getDocumentoReferencia().getCufeOriginal() != null) {
                    Element uuidOrig = doc.createElementNS(NS_CBC, "cbc:UUID");
                    uuidOrig.setAttribute("schemeName", "CUFE-SHA384");
                    uuidOrig.setTextContent(request.getDocumentoReferencia().getCufeOriginal());
                    invoiceDocRef.appendChild(uuidOrig);
                }
                if (request.getDocumentoReferencia().getFechaEmisionOriginal() != null) {
                    addCbcElement(doc, invoiceDocRef, "cbc:IssueDate",
                            request.getDocumentoReferencia().getFechaEmisionOriginal().format(DateTimeFormatter.ISO_LOCAL_DATE));
                }
                billRef.appendChild(invoiceDocRef);
                root.appendChild(billRef);
            }

            // ── Emisor / Receptor ──
            Element supplierParty = doc.createElementNS(NS_CAC, "cac:AccountingSupplierParty");
            addCbcElement(doc, supplierParty, "cbc:AdditionalAccountID", "1");
            supplierParty.appendChild(crearPartyEmisor(doc, request.getEmisor()));
            root.appendChild(supplierParty);

            Element customerParty = doc.createElementNS(NS_CAC, "cac:AccountingCustomerParty");
            addCbcElement(doc, customerParty, "cbc:AdditionalAccountID",
                    request.getReceptor() != null && "31".equals(request.getReceptor().getTipoIdentificacion()) ? "1" : "2");
            customerParty.appendChild(crearPartyReceptor(doc, request.getReceptor()));
            root.appendChild(customerParty);

            // ── TaxTotal: tasa IVA dinámica (no hardcoded 19%) ──
            Element taxTotal = doc.createElementNS(NS_CAC, "cac:TaxTotal");
            addAmountElement(doc, taxTotal, "cbc:TaxAmount", request.getTotalIva());
            Element taxSubtotal = doc.createElementNS(NS_CAC, "cac:TaxSubtotal");
            addAmountElement(doc, taxSubtotal, "cbc:TaxableAmount", request.getBaseGravable());
            addAmountElement(doc, taxSubtotal, "cbc:TaxAmount", request.getTotalIva());
            Element taxCategory = doc.createElementNS(NS_CAC, "cac:TaxCategory");
            BigDecimal baseIva = request.getBaseGravable() != null ? request.getBaseGravable() : BigDecimal.ZERO;
            BigDecimal montoIva = request.getTotalIva() != null ? request.getTotalIva() : BigDecimal.ZERO;
            String tasaIvaNota = "0.00";
            if (baseIva.compareTo(BigDecimal.ZERO) > 0) {
                tasaIvaNota = montoIva.multiply(BigDecimal.valueOf(100))
                        .divide(baseIva, 2, java.math.RoundingMode.HALF_UP).toPlainString();
            }
            addCbcElement(doc, taxCategory, "cbc:Percent", tasaIvaNota);
            Element taxScheme = doc.createElementNS(NS_CAC, "cac:TaxScheme");
            // Régimen: si la empresa no es responsable de IVA → schemeID "ZZ".
            boolean respIva = request.getEmisor() == null
                    || request.getEmisor().getResponsableIva() == null
                    || Boolean.TRUE.equals(request.getEmisor().getResponsableIva());
            addCbcElement(doc, taxScheme, "cbc:ID", respIva ? "01" : "ZZ");
            addCbcElement(doc, taxScheme, "cbc:Name", respIva ? "IVA" : "No aplica");
            taxCategory.appendChild(taxScheme);
            taxSubtotal.appendChild(taxCategory);
            taxTotal.appendChild(taxSubtotal);
            root.appendChild(taxTotal);

            // ── LegalMonetaryTotal ──
            Element legalTotal = doc.createElementNS(NS_CAC, "cac:LegalMonetaryTotal");
            addAmountElement(doc, legalTotal, "cbc:LineExtensionAmount", request.getBaseGravable());
            addAmountElement(doc, legalTotal, "cbc:TaxExclusiveAmount", request.getBaseGravable());
            addAmountElement(doc, legalTotal, "cbc:TaxInclusiveAmount", request.getTotalFactura());
            addAmountElement(doc, legalTotal, "cbc:PayableAmount", request.getTotalFactura());
            root.appendChild(legalTotal);

            // ── Líneas (CreditNoteLine / DebitNoteLine) ──
            if (request.getLineas() != null) {
                for (DianDocumentoRequestDTO.LineaDTO linea : request.getLineas()) {
                    root.appendChild(crearLineaNota(doc, linea, tagLine));
                }
            }

            return documentToString(doc);
        } catch (Exception e) {
            throw new RuntimeException("Error generando XML UBL Nota: " + e.getMessage(), e);
        }
    }

    private Element crearLineaNota(Document doc, DianDocumentoRequestDTO.LineaDTO linea, String tagLine) {
        Element l = doc.createElementNS(NS_CAC, "cac:" + tagLine);
        addCbcElement(doc, l, "cbc:ID", String.valueOf(linea.getNumero()));
        String qtyTag = "CreditNoteLine".equals(tagLine) ? "cbc:CreditedQuantity" : "cbc:DebitedQuantity";
        Element qty = doc.createElementNS(NS_CBC, qtyTag);
        qty.setAttribute("unitCode", "94");
        qty.setTextContent(String.valueOf(linea.getCantidad()));
        l.appendChild(qty);
        addAmountElement(doc, l, "cbc:LineExtensionAmount",
                linea.getTotalLinea() != null ? linea.getTotalLinea() : BigDecimal.ZERO);

        Element item = doc.createElementNS(NS_CAC, "cac:Item");
        addCbcElement(doc, item, "cbc:Description",
                linea.getDescripcion() != null ? linea.getDescripcion() : "Producto");
        Element sellerId = doc.createElementNS(NS_CAC, "cac:SellersItemIdentification");
        addCbcElement(doc, sellerId, "cbc:ID",
                linea.getCodigoProducto() != null ? linea.getCodigoProducto() : "");
        item.appendChild(sellerId);
        l.appendChild(item);

        Element price = doc.createElementNS(NS_CAC, "cac:Price");
        addAmountElement(doc, price, "cbc:PriceAmount",
                linea.getPrecioUnitario() != null ? linea.getPrecioUnitario() : BigDecimal.ZERO);
        Element baseQty = doc.createElementNS(NS_CBC, "cbc:BaseQuantity");
        baseQty.setAttribute("unitCode", "94");
        baseQty.setTextContent("1");
        price.appendChild(baseQty);
        l.appendChild(price);

        return l;
    }

    private Element crearPartyEmisor(Document doc, DianDocumentoRequestDTO.EmisorDTO em) {
        Element party = doc.createElementNS(NS_CAC, "cac:Party");
        Element partyName = doc.createElementNS(NS_CAC, "cac:PartyName");
        addCbcElement(doc, partyName, "cbc:Name", em != null ? safe(em.getRazonSocial()) : "");
        party.appendChild(partyName);

        Element pls = doc.createElementNS(NS_CAC, "cac:PartyLegalEntity");
        addCbcElement(doc, pls, "cbc:RegistrationName", em != null ? safe(em.getRazonSocial()) : "");
        addCbcElement(doc, pls, "cbc:CompanyID", em != null ? safe(em.getNumeroIdentificacion()) : "");
        // cbc:TaxLevelCode obligatorio DIAN.
        String emRespFiscal = em != null && em.getResponsabilidadFiscal() != null && !em.getResponsabilidadFiscal().isBlank()
                ? em.getResponsabilidadFiscal() : "R-99-PN";
        addCbcElement(doc, pls, "cbc:TaxLevelCode", emRespFiscal);
        party.appendChild(pls);
        return party;
    }

    private Element crearPartyReceptor(Document doc, DianDocumentoRequestDTO.ReceptorDTO rc) {
        Element party = doc.createElementNS(NS_CAC, "cac:Party");
        Element partyName = doc.createElementNS(NS_CAC, "cac:PartyName");
        addCbcElement(doc, partyName, "cbc:Name", rc != null ? safe(rc.getNombre()) : "");
        party.appendChild(partyName);

        Element pls = doc.createElementNS(NS_CAC, "cac:PartyLegalEntity");
        addCbcElement(doc, pls, "cbc:RegistrationName", rc != null ? safe(rc.getNombre()) : "");
        addCbcElement(doc, pls, "cbc:CompanyID", rc != null ? safe(rc.getNumeroIdentificacion()) : "");
        String rcRespFiscal = rc != null && rc.getResponsabilidadFiscal() != null && !rc.getResponsabilidadFiscal().isBlank()
                ? rc.getResponsabilidadFiscal() : "R-99-PN";
        addCbcElement(doc, pls, "cbc:TaxLevelCode", rcRespFiscal);
        party.appendChild(pls);
        return party;
    }

    // ── Helpers ──
    private void addCbcElement(Document doc, Element parent, String name, String value) {
        Element el = doc.createElementNS(NS_CBC, name);
        el.setTextContent(value != null ? value : "");
        parent.appendChild(el);
    }
    private void addTextElement(Document doc, Element parent, String name, String value) {
        String ns = name.startsWith("cbc:") ? NS_CBC : (name.startsWith("cac:") ? NS_CAC : NS_STS);
        Element el = doc.createElementNS(ns, name);
        el.setTextContent(value != null ? value : "");
        parent.appendChild(el);
    }
    private void addAmountElement(Document doc, Element parent, String name, BigDecimal value) {
        Element el = doc.createElementNS(NS_CBC, name);
        el.setAttribute("currencyID", "COP");
        el.setTextContent(value != null ? value.toPlainString() : "0.00");
        parent.appendChild(el);
    }
    private String safe(String s) { return s != null ? s : ""; }

    private String documentToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        t.setOutputProperty(OutputKeys.INDENT, "no");
        StringWriter w = new StringWriter();
        t.transform(new DOMSource(doc), new StreamResult(w));
        return w.toString();
    }
}
