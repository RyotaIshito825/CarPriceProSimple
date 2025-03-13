package com.techacademy.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class PdfGenerationService {

    private ThymeleafViewResolver thymeleafViewResolver;

    // テンプレートを使ってHTMLを生成
    public String generateHtmlFromTemplate(String templateName, Model model) throws Exception {
        String str = thymeleafViewResolver.getTemplateEngine().process(templateName, (IContext) model);
        return str;
    }

    // HTMLをPDFに変換
    public byte[] generatePdfFromHtml(String htmlContent) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.layout();
        renderer.createPDF(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // テンプレートを使ってHTMLを生成し、PDFを作成
    public byte[] generatePdfFromTemplate(String templateName, Model model) throws Exception {
        String htmlContent = generateHtmlFromTemplate(templateName, model);
        return generatePdfFromHtml(htmlContent);
    }

    // PDFをファイルに保存
    public void savePdfToFile(byte[] pdfContent, String outputPath) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(outputPath))) {
            fileOutputStream.write(pdfContent);
        }
    }

}
