package com.example.test.service;

import com.example.test.model.CV;
import com.example.test.model.Utilisateur;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PDFExportService {

    // Colors
    private static final BaseColor COLOR_PRIMARY   = new BaseColor(83, 74, 183);  // purple
    private static final BaseColor COLOR_LIGHT     = new BaseColor(238, 237, 254);
    private static final BaseColor COLOR_TEXT      = new BaseColor(44, 44, 42);
    private static final BaseColor COLOR_MUTED     = new BaseColor(136, 135, 128);
    private static final BaseColor COLOR_WHITE     = BaseColor.WHITE;

    // Fonts
    private static Font fontName;
    private static Font fontTitle;
    private static Font fontSection;
    private static Font fontBody;
    private static Font fontMuted;
    private static Font fontWhite;

    static {
        try {
            fontName    = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD,   COLOR_WHITE);
            fontTitle   = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, COLOR_MUTED);
            fontSection = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD,   COLOR_PRIMARY);
            fontBody    = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_TEXT);
            fontMuted   = new Font(Font.FontFamily.HELVETICA,  9, Font.NORMAL, COLOR_MUTED);
            fontWhite   = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exports the CV as a PDF file and returns the output path.
     * @param cv          the CV data
     * @param utilisateur the owner
     * @param outputPath  full path of the output PDF file
     */
    public String exporter(CV cv, Utilisateur utilisateur, String outputPath) throws DocumentException, IOException {

        Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
        PdfWriter.getInstance(doc, new FileOutputStream(outputPath));
        doc.open();

        // ── LEFT SIDEBAR ──────────────────────────────────────────────────
        PdfPTable layout = new PdfPTable(new float[]{2f, 3f});
        layout.setWidthPercentage(100);
        layout.setSpacingBefore(0);

        PdfPCell sidebar = new PdfPCell();
        sidebar.setBackgroundColor(COLOR_PRIMARY);
        sidebar.setPadding(20);
        sidebar.setBorder(Rectangle.NO_BORDER);

        // Photo
        if (cv.getPhotoPath() != null && Files.exists(Paths.get(cv.getPhotoPath()))) {
            try {
                Image photo = Image.getInstance(cv.getPhotoPath());
                photo.scaleToFit(120, 120);
                // Make it round-ish with a white border cell
                PdfPTable photoTable = new PdfPTable(1);
                photoTable.setWidthPercentage(100);
                PdfPCell photoCell = new PdfPCell(photo);
                photoCell.setBorderColor(COLOR_WHITE);
                photoCell.setBorderWidth(3);
                photoCell.setPadding(2);
                photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                photoTable.addCell(photoCell);
                sidebar.addElement(photoTable);
                sidebar.addElement(new Paragraph(" "));
            } catch (Exception ignored) {}
        }

        // Name
        Paragraph name = new Paragraph(utilisateur.getPrenom() + "\n" + utilisateur.getNom().toUpperCase(), fontName);
        name.setAlignment(Element.ALIGN_CENTER);
        sidebar.addElement(name);

        // CV Title
        Paragraph cvTitle = new Paragraph(cv.getTitre() != null ? cv.getTitre() : "", fontWhite);
        cvTitle.setAlignment(Element.ALIGN_CENTER);
        cvTitle.setSpacingBefore(4);
        sidebar.addElement(cvTitle);

        addSidebarSeparator(sidebar);

        // Contact
        addSidebarSection(sidebar, "CONTACT");
        addSidebarItem(sidebar, "✉  " + utilisateur.getEmail());

        // Languages
        if (cv.getLangues() != null && !cv.getLangues().isBlank()) {
            addSidebarSeparator(sidebar);
            addSidebarSection(sidebar, "LANGUES");
            for (String lang : cv.getLangues().split(",")) {
                addSidebarItem(sidebar, "•  " + lang.trim());
            }
        }

        // Skills
        if (cv.getCompetences() != null && !cv.getCompetences().isBlank()) {
            addSidebarSeparator(sidebar);
            addSidebarSection(sidebar, "COMPÉTENCES");
            for (String skill : cv.getCompetences().split(",")) {
                addSidebarItem(sidebar, "•  " + skill.trim());
            }
        }

        // ── RIGHT CONTENT ─────────────────────────────────────────────────
        PdfPCell content = new PdfPCell();
        content.setPadding(28);
        content.setBorder(Rectangle.NO_BORDER);

        // Formation
        if (cv.getFormation() != null && !cv.getFormation().isBlank()) {
            addContentSection(content, "FORMATION");
            addContentBody(content, cv.getFormation());
        }

        // Experiences
        if (cv.getExperiences() != null && !cv.getExperiences().isBlank()) {
            addContentSection(content, "EXPÉRIENCES");
            addContentBody(content, cv.getExperiences());
        }

        // Date
        Paragraph date = new Paragraph("Créé le " + cv.getDateCreation(), fontMuted);
        date.setSpacingBefore(20);
        content.addElement(date);

        layout.addCell(sidebar);
        layout.addCell(content);
        doc.add(layout);

        doc.close();
        return outputPath;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void addSidebarSeparator(PdfPCell cell) {
        Paragraph sep = new Paragraph(" ");
        sep.setSpacingBefore(8);
        sep.setSpacingAfter(4);
        cell.addElement(sep);
    }

    private void addSidebarSection(PdfPCell cell, String title) {
        Font f = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, COLOR_LIGHT);
        Paragraph p = new Paragraph(title, f);
        p.setSpacingAfter(4);
        cell.addElement(p);
    }

    private void addSidebarItem(PdfPCell cell, String text) {
        Paragraph p = new Paragraph(text, fontWhite);
        p.setSpacingAfter(3);
        cell.addElement(p);
    }

    private void addContentSection(PdfPCell cell, String title) {
        Paragraph p = new Paragraph(title, fontSection);
        p.setSpacingBefore(16);
        p.setSpacingAfter(6);
        cell.addElement(p);

        // Underline separator
        com.itextpdf.text.pdf.draw.LineSeparator line = new com.itextpdf.text.pdf.draw.LineSeparator();
        line.setLineColor(COLOR_PRIMARY);
        line.setLineWidth(0.5f);
        cell.addElement(new Chunk(line));
    }

    private void addContentBody(PdfPCell cell, String text) {
        Paragraph p = new Paragraph(text, fontBody);
        p.setSpacingBefore(6);
        p.setLeading(14);
        cell.addElement(p);
    }
}