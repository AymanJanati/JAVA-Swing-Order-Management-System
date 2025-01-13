package com.gestion.commandes.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class PDFExporter {
    public static void exportToPDF(String filePath, String content) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph(content));
            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}