package com.techacademy.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.techacademy.service.PdfService;

@Controller
public class PdfController {

    private final PdfService pdfService;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/generate-pdf")
    public String generatePdf(Model model) {
        model.addAttribute("maker", "ダイハツ");
        return "/pricecards/pricecard1";
    }

    @GetMapping("/generateAndSave")
    public String generateAndSaveHtml(String maker) {
        Context context = new Context();
        context.setVariable("maker", maker);

        String htmlContent = templateEngine.process("pricecard1", context);
        htmlContent = htmlContent.replace("</head>", "<link rel=\"stylesheet\" href=\"/css/style.css\">" + "</head>");

        Path currentDirectoryPath = FileSystems.getDefault().getPath("");
        String currentDirectoryName = currentDirectoryPath.toAbsolutePath().toString();

        System.out.println("currentDirectoryName : " + currentDirectoryName);

        try {
            File file = new File("src/main/resources/templates/pricecards/generated-file.html");
//            File file = new File("generated-file.html");

            file.getParentFile().mkdirs();
            Writer writer = new FileWriter(file);
            writer.write(htmlContent);
            writer.close();


            if (file.exists()) {
                System.out.println("HTMLファイルは正常に保存されました: " + file.getAbsolutePath());
            } else {
                System.out.println("HTMLファイルの保存に失敗しました。");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/cars";
    }

    @GetMapping("/g")
    public String g() {
        return "/pricecards/generated-file";
    }
}