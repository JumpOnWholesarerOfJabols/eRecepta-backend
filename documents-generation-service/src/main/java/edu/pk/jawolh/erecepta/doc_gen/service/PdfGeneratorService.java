package edu.pk.jawolh.erecepta.doc_gen.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import edu.pk.jawolh.erecepta.doc_gen.model.PrescriptionPdfData;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfGeneratorService {

    private final TemplateEngine templateEngine;

    public PdfGeneratorService() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.XML);
        templateResolver.setCharacterEncoding("UTF-8");

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
    }

    public byte[] generatePrescriptionPdf(PrescriptionPdfData data) {
        Context context = new Context();
        context.setVariable("data", data);

        String htmlContent = templateEngine.process("prescription", context);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}