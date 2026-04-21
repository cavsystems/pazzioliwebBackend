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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Genera el XML UBL 2.1 conforme a la especificación de la DIAN Colombia.
 * Documento tipo Invoice para facturas electrónicas.
 */
@Component
public class UblXmlGenerator {

    private static final String NS_CBC = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private static final String NS_CAC = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
    private static final String NS_INV = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2";
    private static final String NS_EXT = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
    private static final String NS_STS = "dian:gov:co:facturaelectronica:Structures-2-1";
    private static final String NS_DS = "http://www.w3.org/2000/09/xmldsig#";

    private final DianConfig dianConfig;

    public UblXmlGenerator(DianConfig dianConfig) {
        this.dianConfig = dianConfig;
    }

    /**
     * Genera el XML UBL 2.1 completo para enviar a la DIAN.
     *
     * @param request datos del documento
     * @param cufe    CUFE ya calculado
     * @return XML como String
     */
    public String generarXml(DianDocumentoRequestDTO request, String cufe) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Root: Invoice
            Element invoice = doc.createElementNS(NS_INV, "Invoice");
            invoice.setAttribute("xmlns:cbc", NS_CBC);
            invoice.setAttribute("xmlns:cac", NS_CAC);
            invoice.setAttribute("xmlns:ext", NS_EXT);
            invoice.setAttribute("xmlns:sts", NS_STS);
            invoice.setAttribute("xmlns:ds", NS_DS);
            doc.appendChild(invoice);

            // UBLExtensions (placeholder para firma digital)
            Element ublExtensions = doc.createElementNS(NS_EXT, "ext:UBLExtensions");
            Element ublExtension = doc.createElementNS(NS_EXT, "ext:UBLExtension");
            Element extensionContent = doc.createElementNS(NS_EXT, "ext:ExtensionContent");

            // DIAN Extensions
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

            // InvoiceSource
            Element invoiceSource = doc.createElementNS(NS_STS, "sts:InvoiceSource");
            addCbcElement(doc, invoiceSource, "cbc:IdentificationCode", "CO");
            dianExtensions.appendChild(invoiceSource);

            // SoftwareProvider
            Element softwareProvider = doc.createElementNS(NS_STS, "sts:SoftwareProvider");
            addTextElement(doc, softwareProvider, "sts:ProviderID",
                    request.getEmisor() != null ? request.getEmisor().getNumeroIdentificacion() : "");
            addTextElement(doc, softwareProvider, "sts:SoftwareID",
                    dianConfig.getSoftware().getId() != null ? dianConfig.getSoftware().getId() : "");
            dianExtensions.appendChild(softwareProvider);

            // SoftwareSecurityCode
            addTextElement(doc, dianExtensions, "sts:SoftwareSecurityCode", generarSoftwareSecurityCode(request));

            extensionContent.appendChild(dianExtensions);
            ublExtension.appendChild(extensionContent);
            ublExtensions.appendChild(ublExtension);

            // Extension para firma digital (vacía, se llena al firmar)
            Element ublExtension2 = doc.createElementNS(NS_EXT, "ext:UBLExtension");
            Element extensionContent2 = doc.createElementNS(NS_EXT, "ext:ExtensionContent");
            ublExtension2.appendChild(extensionContent2);
            ublExtensions.appendChild(ublExtension2);

            invoice.appendChild(ublExtensions);

            // UBLVersionID
            addCbcElement(doc, invoice, "cbc:UBLVersionID", "UBL 2.1");
            addCbcElement(doc, invoice, "cbc:CustomizationID", "10");
            addCbcElement(doc, invoice, "cbc:ProfileID", "DIAN 2.1");
            addCbcElement(doc, invoice, "cbc:ProfileExecutionID", String.valueOf(dianConfig.getAmbiente()));

            // ID (número de factura)
            addCbcElement(doc, invoice, "cbc:ID", request.getPrefijo() + request.getConsecutivo());

            // UUID (CUFE)
            Element uuidEl = doc.createElementNS(NS_CBC, "cbc:UUID");
            uuidEl.setAttribute("schemeID", String.valueOf(dianConfig.getAmbiente()));
            uuidEl.setAttribute("schemeName", "CUFE-SHA384");
            uuidEl.setTextContent(cufe);
            invoice.appendChild(uuidEl);

            // IssueDate / IssueTime
            addCbcElement(doc, invoice, "cbc:IssueDate",
                    request.getFechaEmision().format(DateTimeFormatter.ISO_LOCAL_DATE));
            addCbcElement(doc, invoice, "cbc:IssueTime",
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "-05:00");

            // InvoiceTypeCode
            Element invoiceTypeCode = doc.createElementNS(NS_CBC, "cbc:InvoiceTypeCode");
            invoiceTypeCode.setTextContent(request.getTipoDocumento());
            invoice.appendChild(invoiceTypeCode);

            // Note
            addCbcElement(doc, invoice, "cbc:Note", "Factura electrónica de venta");

            // DocumentCurrencyCode
            addCbcElement(doc, invoice, "cbc:DocumentCurrencyCode", "COP");

            // LineCountNumeric
            addCbcElement(doc, invoice, "cbc:LineCountNumeric",
                    String.valueOf(request.getLineas() != null ? request.getLineas().size() : 0));

            // ── AccountingSupplierParty (Emisor) ──
            Element supplierParty = doc.createElementNS(NS_CAC, "cac:AccountingSupplierParty");
            addCbcElement(doc, supplierParty, "cbc:AdditionalAccountID", "1"); // Persona jurídica
            Element party = crearParty(doc, request.getEmisor());
            supplierParty.appendChild(party);
            invoice.appendChild(supplierParty);

            // ── AccountingCustomerParty (Receptor/Cliente) ──
            Element customerParty = doc.createElementNS(NS_CAC, "cac:AccountingCustomerParty");
            addCbcElement(doc, customerParty, "cbc:AdditionalAccountID",
                    "31".equals(request.getReceptor().getTipoIdentificacion()) ? "1" : "2");
            Element custParty = crearPartyReceptor(doc, request.getReceptor());
            customerParty.appendChild(custParty);
            invoice.appendChild(customerParty);

            // ── PaymentMeans (Forma de pago) ──
            Element paymentMeans = doc.createElementNS(NS_CAC, "cac:PaymentMeans");
            addCbcElement(doc, paymentMeans, "cbc:ID", request.getFormaPago()); // 1=Contado, 2=Crédito
            addCbcElement(doc, paymentMeans, "cbc:PaymentMeansCode",
                    request.getMetodosPago() != null && !request.getMetodosPago().isEmpty()
                            ? request.getMetodosPago().get(0).getMedioPago() : "10");
            if ("2".equals(request.getFormaPago()) && request.getFechaVencimiento() != null) {
                addCbcElement(doc, paymentMeans, "cbc:PaymentDueDate",
                        request.getFechaVencimiento().format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            invoice.appendChild(paymentMeans);

            // ── TaxTotal (IVA) ──
            Element taxTotal = doc.createElementNS(NS_CAC, "cac:TaxTotal");
            addAmountElement(doc, taxTotal, "cbc:TaxAmount", request.getTotalIva());

            Element taxSubtotal = doc.createElementNS(NS_CAC, "cac:TaxSubtotal");
            addAmountElement(doc, taxSubtotal, "cbc:TaxableAmount", request.getBaseGravable());
            addAmountElement(doc, taxSubtotal, "cbc:TaxAmount", request.getTotalIva());

            Element taxCategory = doc.createElementNS(NS_CAC, "cac:TaxCategory");
            Element percent = doc.createElementNS(NS_CBC, "cbc:Percent");
            percent.setTextContent("19.00");
            taxCategory.appendChild(percent);

            Element taxScheme = doc.createElementNS(NS_CAC, "cac:TaxScheme");
            addCbcElement(doc, taxScheme, "cbc:ID", "01");
            addCbcElement(doc, taxScheme, "cbc:Name", "IVA");
            taxCategory.appendChild(taxScheme);
            taxSubtotal.appendChild(taxCategory);
            taxTotal.appendChild(taxSubtotal);
            invoice.appendChild(taxTotal);

            // ── LegalMonetaryTotal ──
            Element legalTotal = doc.createElementNS(NS_CAC, "cac:LegalMonetaryTotal");
            addAmountElement(doc, legalTotal, "cbc:LineExtensionAmount", request.getBaseGravable());
            addAmountElement(doc, legalTotal, "cbc:TaxExclusiveAmount", request.getBaseGravable());
            addAmountElement(doc, legalTotal, "cbc:TaxInclusiveAmount", request.getTotalFactura());
            addAmountElement(doc, legalTotal, "cbc:AllowanceTotalAmount",
                    request.getTotalDescuento() != null ? request.getTotalDescuento() : BigDecimal.ZERO);
            addAmountElement(doc, legalTotal, "cbc:PayableAmount", request.getTotalFactura());
            invoice.appendChild(legalTotal);

            // ── InvoiceLines ──
            if (request.getLineas() != null) {
                for (DianDocumentoRequestDTO.LineaDTO linea : request.getLineas()) {
                    invoice.appendChild(crearInvoiceLine(doc, linea));
                }
            }

            // Serializar a String
            return documentToString(doc);

        } catch (Exception e) {
            throw new RuntimeException("Error generando XML UBL 2.1: " + e.getMessage(), e);
        }
    }

    private Element crearParty(Document doc, DianDocumentoRequestDTO.EmisorDTO emisor) {
        Element party = doc.createElementNS(NS_CAC, "cac:Party");

        // PartyName
        Element partyName = doc.createElementNS(NS_CAC, "cac:PartyName");
        addCbcElement(doc, partyName, "cbc:Name",
                emisor != null ? emisor.getRazonSocial() : "");
        party.appendChild(partyName);

        // PartyTaxScheme
        Element partyTaxScheme = doc.createElementNS(NS_CAC, "cac:PartyTaxScheme");
        addCbcElement(doc, partyTaxScheme, "cbc:RegistrationName",
                emisor != null ? emisor.getRazonSocial() : "");
        addCbcElement(doc, partyTaxScheme, "cbc:CompanyID",
                emisor != null ? emisor.getNumeroIdentificacion() : "");

        Element taxScheme = doc.createElementNS(NS_CAC, "cac:TaxScheme");
        addCbcElement(doc, taxScheme, "cbc:ID", "01");
        addCbcElement(doc, taxScheme, "cbc:Name", "IVA");
        partyTaxScheme.appendChild(taxScheme);
        party.appendChild(partyTaxScheme);

        // PartyLegalEntity
        Element partyLegal = doc.createElementNS(NS_CAC, "cac:PartyLegalEntity");
        addCbcElement(doc, partyLegal, "cbc:RegistrationName",
                emisor != null ? emisor.getRazonSocial() : "");
        addCbcElement(doc, partyLegal, "cbc:CompanyID",
                emisor != null ? emisor.getNumeroIdentificacion() : "");

        // Address
        if (emisor != null) {
            Element address = doc.createElementNS(NS_CAC, "cac:RegistrationAddress");
            addCbcElement(doc, address, "cbc:CityName",
                    emisor.getMunicipio() != null ? emisor.getMunicipio() : "");
            addCbcElement(doc, address, "cbc:CountrySubentity",
                    emisor.getDepartamento() != null ? emisor.getDepartamento() : "");

            Element addressLine = doc.createElementNS(NS_CAC, "cac:AddressLine");
            addCbcElement(doc, addressLine, "cbc:Line",
                    emisor.getDireccion() != null ? emisor.getDireccion() : "");
            address.appendChild(addressLine);

            Element country = doc.createElementNS(NS_CAC, "cac:Country");
            addCbcElement(doc, country, "cbc:IdentificationCode", "CO");
            addCbcElement(doc, country, "cbc:Name", "Colombia");
            address.appendChild(country);
            partyLegal.appendChild(address);
        }
        party.appendChild(partyLegal);

        // Contact
        if (emisor != null) {
            Element contact = doc.createElementNS(NS_CAC, "cac:Contact");
            addCbcElement(doc, contact, "cbc:Telephone",
                    emisor.getTelefono() != null ? emisor.getTelefono() : "");
            addCbcElement(doc, contact, "cbc:ElectronicMail",
                    emisor.getCorreo() != null ? emisor.getCorreo() : "");
            party.appendChild(contact);
        }

        return party;
    }

    private Element crearPartyReceptor(Document doc, DianDocumentoRequestDTO.ReceptorDTO receptor) {
        Element party = doc.createElementNS(NS_CAC, "cac:Party");

        Element partyIdentification = doc.createElementNS(NS_CAC, "cac:PartyIdentification");
        Element idEl = doc.createElementNS(NS_CBC, "cbc:ID");
        idEl.setAttribute("schemeID",
                receptor.getDigitoVerificacion() != null ? receptor.getDigitoVerificacion() : "");
        idEl.setAttribute("schemeName",
                receptor.getTipoIdentificacion() != null ? receptor.getTipoIdentificacion() : "13");
        idEl.setTextContent(receptor.getNumeroIdentificacion() != null ? receptor.getNumeroIdentificacion() : "");
        partyIdentification.appendChild(idEl);
        party.appendChild(partyIdentification);

        Element partyName = doc.createElementNS(NS_CAC, "cac:PartyName");
        addCbcElement(doc, partyName, "cbc:Name",
                receptor.getNombre() != null ? receptor.getNombre() : "");
        party.appendChild(partyName);

        Element partyTaxScheme = doc.createElementNS(NS_CAC, "cac:PartyTaxScheme");
        addCbcElement(doc, partyTaxScheme, "cbc:RegistrationName",
                receptor.getNombre() != null ? receptor.getNombre() : "");
        addCbcElement(doc, partyTaxScheme, "cbc:CompanyID",
                receptor.getNumeroIdentificacion() != null ? receptor.getNumeroIdentificacion() : "");
        Element taxScheme = doc.createElementNS(NS_CAC, "cac:TaxScheme");
        addCbcElement(doc, taxScheme, "cbc:ID", "01");
        addCbcElement(doc, taxScheme, "cbc:Name", "IVA");
        partyTaxScheme.appendChild(taxScheme);
        party.appendChild(partyTaxScheme);

        Element partyLegal = doc.createElementNS(NS_CAC, "cac:PartyLegalEntity");
        addCbcElement(doc, partyLegal, "cbc:RegistrationName",
                receptor.getNombre() != null ? receptor.getNombre() : "");
        addCbcElement(doc, partyLegal, "cbc:CompanyID",
                receptor.getNumeroIdentificacion() != null ? receptor.getNumeroIdentificacion() : "");
        party.appendChild(partyLegal);

        if (receptor.getCorreo() != null) {
            Element contact = doc.createElementNS(NS_CAC, "cac:Contact");
            addCbcElement(doc, contact, "cbc:ElectronicMail", receptor.getCorreo());
            if (receptor.getTelefono() != null) {
                addCbcElement(doc, contact, "cbc:Telephone", receptor.getTelefono());
            }
            party.appendChild(contact);
        }

        return party;
    }

    private Element crearInvoiceLine(Document doc, DianDocumentoRequestDTO.LineaDTO linea) {
        Element invoiceLine = doc.createElementNS(NS_CAC, "cac:InvoiceLine");

        addCbcElement(doc, invoiceLine, "cbc:ID", String.valueOf(linea.getNumero()));

        Element quantity = doc.createElementNS(NS_CBC, "cbc:InvoicedQuantity");
        quantity.setAttribute("unitCode", "EA");
        quantity.setTextContent(String.valueOf(linea.getCantidad()));
        invoiceLine.appendChild(quantity);

        addAmountElement(doc, invoiceLine, "cbc:LineExtensionAmount",
                linea.getTotalLinea() != null ? linea.getTotalLinea() : BigDecimal.ZERO);

        // AllowanceCharge (descuento)
        if (linea.getDescuento() != null && linea.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
            Element allowance = doc.createElementNS(NS_CAC, "cac:AllowanceCharge");
            addCbcElement(doc, allowance, "cbc:ChargeIndicator", "false");
            addAmountElement(doc, allowance, "cbc:Amount", linea.getDescuento());
            invoiceLine.appendChild(allowance);
        }

        // TaxTotal
        Element taxTotal = doc.createElementNS(NS_CAC, "cac:TaxTotal");
        addAmountElement(doc, taxTotal, "cbc:TaxAmount",
                linea.getValorIva() != null ? linea.getValorIva() : BigDecimal.ZERO);

        Element taxSubtotal = doc.createElementNS(NS_CAC, "cac:TaxSubtotal");
        BigDecimal baseLinea = linea.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(linea.getCantidad()))
                .subtract(linea.getDescuento() != null ? linea.getDescuento() : BigDecimal.ZERO);
        addAmountElement(doc, taxSubtotal, "cbc:TaxableAmount", baseLinea);
        addAmountElement(doc, taxSubtotal, "cbc:TaxAmount",
                linea.getValorIva() != null ? linea.getValorIva() : BigDecimal.ZERO);

        Element taxCategory = doc.createElementNS(NS_CAC, "cac:TaxCategory");
        Element percent = doc.createElementNS(NS_CBC, "cbc:Percent");
        percent.setTextContent(linea.getPorcentajeIva() != null ? linea.getPorcentajeIva().toPlainString() : "0.00");
        taxCategory.appendChild(percent);

        Element taxScheme = doc.createElementNS(NS_CAC, "cac:TaxScheme");
        addCbcElement(doc, taxScheme, "cbc:ID", "01");
        addCbcElement(doc, taxScheme, "cbc:Name", "IVA");
        taxCategory.appendChild(taxScheme);
        taxSubtotal.appendChild(taxCategory);
        taxTotal.appendChild(taxSubtotal);
        invoiceLine.appendChild(taxTotal);

        // Item
        Element item = doc.createElementNS(NS_CAC, "cac:Item");
        addCbcElement(doc, item, "cbc:Description",
                linea.getDescripcion() != null ? linea.getDescripcion() : "");

        Element sellersItemId = doc.createElementNS(NS_CAC, "cac:SellersItemIdentification");
        addCbcElement(doc, sellersItemId, "cbc:ID",
                linea.getCodigoProducto() != null ? linea.getCodigoProducto() : "");
        item.appendChild(sellersItemId);
        invoiceLine.appendChild(item);

        // Price
        Element price = doc.createElementNS(NS_CAC, "cac:Price");
        addAmountElement(doc, price, "cbc:PriceAmount",
                linea.getPrecioUnitario() != null ? linea.getPrecioUnitario() : BigDecimal.ZERO);
        Element baseQty = doc.createElementNS(NS_CBC, "cbc:BaseQuantity");
        baseQty.setAttribute("unitCode", "EA");
        baseQty.setTextContent("1.00");
        price.appendChild(baseQty);
        invoiceLine.appendChild(price);

        return invoiceLine;
    }

    // ── Helpers ──

    private void addCbcElement(Document doc, Element parent, String name, String value) {
        Element el = doc.createElementNS(NS_CBC, name);
        el.setTextContent(value != null ? value : "");
        parent.appendChild(el);
    }

    private void addTextElement(Document doc, Element parent, String name, String value) {
        Element el = doc.createElement(name);
        el.setTextContent(value != null ? value : "");
        parent.appendChild(el);
    }

    private void addAmountElement(Document doc, Element parent, String name, BigDecimal amount) {
        Element el = doc.createElementNS(NS_CBC, name);
        el.setAttribute("currencyID", "COP");
        el.setTextContent(amount != null ? amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() : "0.00");
        parent.appendChild(el);
    }

    private String generarSoftwareSecurityCode(DianDocumentoRequestDTO request) {
        // SHA-384(SoftwareID + PIN + NroFactura)
        String softwareId = dianConfig.getSoftware().getId() != null ? dianConfig.getSoftware().getId() : "";
        String pin = dianConfig.getSoftware().getPin() != null ? dianConfig.getSoftware().getPin() : "";
        String nroFactura = request.getPrefijo() + request.getConsecutivo();
        String input = softwareId + pin + nroFactura;
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-384");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String documentToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}

