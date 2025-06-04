package me.abdul.axi_api.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import me.abdul.axi_api.dtos.Report.AnswerDto;
import me.abdul.axi_api.dtos.Report.FeedbackReportDto;
import me.abdul.axi_api.dtos.Report.FormReportDto;
import me.abdul.axi_api.dtos.Report.ReportDto;
import me.abdul.axi_api.dtos.Report.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportPdfService {

    // Title font
    public static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD, new BaseColor(102, 222, 128));

    // Subtitle font
    public static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);

    // Header 1 font
    public static final Font HEADER1_FONT = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.BLACK);

    // Header 2 font
    public static final Font HEADER2_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);

    // Header 3 font
    public static final Font HEADER3_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY);

    // Header 4 font
    public static final Font HEADER4_FONT = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, BaseColor.DARK_GRAY);

    // Normal text font
    public static final Font NORMAL_TEXT_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);

    // normal bold text font
    public static final Font NORMAL_BOLD_TEXT_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);

    // Caption font
    public static final Font CAPTION_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    private static final DateTimeFormatter dtff = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").withZone(ZoneId.systemDefault());

    public byte[] generatePdf(List<ReportDto> reports) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            if (reports == null || reports.isEmpty()) {
                // Handle empty reports case
                document.add(new Paragraph("Geen rapporten beschikbaar.", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            } else {
                // Front page content
                addFrontPageContent(document, dtf, reports.get(0));

                // Start new page for the report details
                document.newPage();
                addReportDetails(document, reports);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return byteArrayOutputStream.toByteArray();
    }

    private void addFrontPageContent(Document document, DateTimeFormatter dtf, ReportDto report) throws DocumentException {
        Paragraph empty = new Paragraph(" ");
        Paragraph companyName = new Paragraph("Anchr Rapport", TITLE_FONT);
        companyName.setAlignment(Element.ALIGN_CENTER);
        companyName.setSpacingAfter(7);

        Paragraph details = new Paragraph("Datum : " + dtf.format(LocalDateTime.now()), SUBTITLE_FONT);
        details.setAlignment(Element.ALIGN_CENTER);
        details.setSpacingAfter(10);

        // get team name and period of the first raport
        String teamName = report.getTeam() != null ? report.getTeam().getName() : "Geen team";
        String period = formatter.format(report.getStartDate()) + " tot " + formatter.format(report.getEndDate());

        Paragraph teamDetails = new Paragraph("Team: " + teamName, SUBTITLE_FONT );
        teamDetails.setAlignment(Element.ALIGN_CENTER);
        teamDetails.setSpacingAfter(5);

        Paragraph periodDetails = new Paragraph("Periode: " + period, SUBTITLE_FONT);
        periodDetails.setAlignment(Element.ALIGN_CENTER);
        periodDetails.setSpacingAfter(5);


        // Add content to the document, centered
        float currentHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
        float contentHeight = companyName.getTotalLeading() + details.getTotalLeading();
        float spaceToCenter = (currentHeight - contentHeight) / 2;

        companyName.setSpacingBefore(spaceToCenter);
        document.add(empty);
        document.add(companyName);
        document.add(details);
        document.add(teamDetails);
        document.add(periodDetails);

        // Adding page border
        Rectangle pageBorder = new Rectangle(30, 30, 565, 812);
        pageBorder.setBorder(Rectangle.BOX);
        pageBorder.setBorderWidth(2);
        pageBorder.setBorderColor(new BaseColor(102, 222, 128));
        document.add(pageBorder);
    }

    private void addReportDetails(Document document, List<ReportDto> reports) throws DocumentException {
        for (ReportDto report : reports) {

            // === Recipient Information Section ===
            addSectionTitle(document, "Ontvanger Informatie", HEADER1_FONT);

            Paragraph recipientInfo = new Paragraph(
                    String.format("Naam: %s %s %s\nCategorie: %s",
                            report.getRecipient().getFirstName(),
                            defaultIfNull(report.getRecipient().getInfix(), ""),
                            report.getRecipient().getLastName(),
                            defaultIfNull(report.getCategory() != null ? report.getCategory().getName() : null, "Onbekend")),
                    NORMAL_TEXT_FONT
            );
            addParagraphWithSpacing(document, recipientInfo, 15);

            // === Form List Section ===
            addSectionTitle(document, "Formulieren in dit rapport:", HEADER2_FONT);

            for (FormReportDto form : report.getForms()) {
                addParagraphWithSpacing(document, new Paragraph(form.getName(), NORMAL_TEXT_FONT), 5);
            }

            // === Feedback Section ===
            addSectionTitle(document, "Feedback", HEADER3_FONT);

            for (FormReportDto form : report.getForms()) {
                for (FeedbackReportDto feedback : form.getFeedbacks()) {

                    // Feedback Title
                    Paragraph feedbackTitle = new Paragraph(
                            String.format("Feedback van %s %s %s",
                                    feedback.getSender().getFirstName(),
                                    defaultIfNull(feedback.getSender().getInfix(), ""),
                                    feedback.getSender().getLastName()),
                            HEADER4_FONT
                    );
                    addParagraphWithSpacing(document, feedbackTitle, 3);
                    // Feedback Details
                    Paragraph feedbackInfo = new Paragraph(
                            String.format("Datum: %s\nScore: %d",
                                    dtff.format(feedback.getDate()),
                                    feedback.getScore()),
                            NORMAL_TEXT_FONT
                    );
                    addParagraphWithSpacing(document, feedbackInfo, 8);

                    addSeparator(document);
                    // Answer Details
                    for (AnswerDto answer : feedback.getAnswers()) {
                        // add space before
                        addParagraphWithSpacing(document, new Paragraph(" ", NORMAL_TEXT_FONT), 2);
                        int questionId = answer.getQuestionId();
                        FormQuestionDto question = form.getQuestions().stream()
                                .filter(q -> q.getId() == questionId)
                                .findFirst()
                                .orElse(null);

                        // Question
                        Paragraph questionText = new Paragraph(question != null ? question.getQuestion() : "Vraag niet gevonden", NORMAL_BOLD_TEXT_FONT);
                        addParagraphWithSpacing(document, questionText, 2);
                        // Answer
                        Paragraph answerText = new Paragraph(answer.getContent(), NORMAL_TEXT_FONT);
                        addParagraphWithSpacing(document, answerText, 1);

                        // add line separator
                        addSeparator(document);
                    }

                    // Space between feedback entries
                    document.add(Chunk.NEWLINE);
                    document.newPage();
                }
            }

            // == new page for the next report
            document.newPage();
        }
    }

    // Helper Method: Adds a section title with spacing
    private void addSectionTitle(Document document, String title, Font font ) throws DocumentException {
        Paragraph sectionTitle = new Paragraph(title, font);
        sectionTitle.setSpacingBefore(10);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
    }

    // Helper Method: Adds a paragraph with defined spacing
    private void addParagraphWithSpacing(Document document, Paragraph paragraph, float spacingAfter) throws DocumentException {
        paragraph.setSpacingAfter(spacingAfter);
        document.add(paragraph);
    }

    // Helper Method: Adds a visual separator line
    private void addSeparator(Document document) throws DocumentException {
        document.add(new Chunk(new LineSeparator(
                0.1f, // Line thickness
                100, // Line width as a percentage
                new BaseColor(240, 239, 235), // Line color
                Element.ALIGN_CENTER, // Alignment
                -2f // Y-position adjustment
        )));
        document.add(Chunk.NEWLINE);
    }

    // Helper Method: Safely gets a value or defaults to a fallback
    private String defaultIfNull(String value, String fallback) {
        return value != null ? value : fallback;
    }


}
