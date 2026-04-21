package com.pazzioliweb.facturacionmodule.service.dian;

import com.pazzioliweb.facturacionmodule.config.DianConfig;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Firma digitalmente el XML UBL 2.1 con XAdES-BES usando el certificado .p12/.pfx configurado.
 * La firma se inserta en la segunda UBLExtension del documento.
 */
@Component
public class XmlDigitalSigner {

    private final DianConfig dianConfig;

    public XmlDigitalSigner(DianConfig dianConfig) {
        this.dianConfig = dianConfig;
    }

    /**
     * Firma el XML y retorna el XML firmado como String.
     * Si no hay certificado configurado, retorna el XML sin firmar.
     */
    public String firmarXml(String xmlSinFirmar) {
        String rutaCert = dianConfig.getCertificado().getRuta();
        String passCert = dianConfig.getCertificado().getPassword();

        if (rutaCert == null || rutaCert.isBlank()) {
            // Sin certificado configurado, retornar sin firmar
            return xmlSinFirmar;
        }

        try {
            // 1. Cargar certificado
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(rutaCert)) {
                keyStore.load(fis, passCert.toCharArray());
            }

            String alias = null;
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                alias = aliases.nextElement();
                if (keyStore.isKeyEntry(alias)) break;
            }
            if (alias == null) {
                throw new RuntimeException("No se encontró clave privada en el certificado");
            }

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, passCert.toCharArray());
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);

            // 2. Parsear XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlSinFirmar)));

            // 3. Buscar la segunda UBLExtension > ExtensionContent para insertar la firma
            NodeList extensionContents = doc.getElementsByTagNameNS(
                    "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2",
                    "ExtensionContent");

            Element signatureParent;
            if (extensionContents.getLength() >= 2) {
                signatureParent = (Element) extensionContents.item(1);
            } else {
                signatureParent = doc.getDocumentElement();
            }

            // 4. Crear contexto de firma
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            DOMSignContext dsc = new DOMSignContext(privateKey, signatureParent);

            // 5. Crear referencia con transforms enveloped
            List<Transform> transforms = new ArrayList<>();
            transforms.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
            transforms.add(fac.newTransform(CanonicalizationMethod.INCLUSIVE,
                    (TransformParameterSpec) null));

            Reference ref = fac.newReference("",
                    fac.newDigestMethod(DigestMethod.SHA256, null),
                    transforms, null, null);

            // 6. Crear SignedInfo
            SignedInfo si = fac.newSignedInfo(
                    fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                            (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                    Collections.singletonList(ref));

            // 7. Crear KeyInfo con X509Data
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            List<Object> x509Content = new ArrayList<>();
            x509Content.add(cert);
            X509Data x509Data = kif.newX509Data(x509Content);
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509Data));

            // 8. Crear y firmar
            XMLSignature signature = fac.newXMLSignature(si, ki);
            signature.sign(dsc);

            // 9. Serializar documento firmado
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StringWriter writer = new StringWriter();
            trans.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error firmando XML digitalmente: " + e.getMessage(), e);
        }
    }
}

