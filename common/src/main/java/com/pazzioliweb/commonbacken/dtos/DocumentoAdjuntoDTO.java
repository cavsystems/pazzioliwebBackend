package com.pazzioliweb.commonbacken.dtos;

/**
 * Documento ya renderizado y listo para enviar por cualquier canal (WhatsApp hoy, lo que venga
 * después). Lo construyen los EmailXService de cada módulo, que son los que ya tienen la plantilla
 * HTML del documento; así el PDF de WhatsApp es EXACTAMENTE el mismo que se adjunta al correo y no
 * hay dos plantillas que mantener.
 *
 * @param nombreArchivo nombre visible del archivo, sin extensión (ej. "Factura-FC-1")
 * @param caption       texto corto que acompaña al documento en el chat
 * @param pdf           PDF ya renderizado; null si el render falló
 */
public record DocumentoAdjuntoDTO(String nombreArchivo, String caption, byte[] pdf) {

    /** true si el PDF se pudo renderizar. */
    public boolean tienePdf() {
        return pdf != null && pdf.length > 0;
    }
}
