package com.techacademy.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.techacademy.entity.Car;
import com.techacademy.service.CarService;
import com.techacademy.service.PdfService;

@Controller
public class PdfController {

    private final PdfService pdfService;
    private final CarService carService;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    public PdfController(PdfService pdfService, CarService carService) {
        this.pdfService = pdfService;
        this.carService = carService;
    }

    @PostMapping("/generate-pdf/{id}")
    public String generatePdf(String priceCardName, Integer id, Model model) {

        System.out.println("test test");
        if (priceCardName == null) {
            return "redirect:/cars";
        }

        Car car = carService.findById(id);

        model.addAttribute("car", car);
        int carPrice = Integer.parseInt(String.valueOf(car.getPrice()) + String.valueOf(car.getPriceDpf()));
        int carTotalPrice = Integer.parseInt(String.valueOf(car.getTotalPrice()) + String.valueOf(car.getTotalPriceDpf()));

        int calcPrice = carTotalPrice - carPrice;

        int calcPriceOfInt = Integer.parseInt(String.valueOf(calcPrice).substring(0, String.valueOf(calcPrice).length() - 1));
        int calcPriceOfDpf = Integer.parseInt(String.valueOf(calcPrice).substring(String.valueOf(calcPrice).length() - 1, String.valueOf(calcPrice).length()));

        model.addAttribute("calcPriceOfInt", calcPriceOfInt);
        model.addAttribute("calcPriceOfDpf", calcPriceOfDpf);

        return "/pricecards/pricecard1";
    }

    @PostMapping("/generate-pdfs")
    public String generatePdfLists(@RequestParam(name = "carIds", required = false) List<String> carIds, String priceCardName, Model model) {

        if (priceCardName == null) {
            return "redirect:cars";
        }

        for (String carId : carIds) {
            generatePdf(priceCardName, Integer.parseInt(carId), model);
            System.out.println(carId);
        }


        System.out.println(carIds);
        return "redirect:/cars";
    }

//    @GetMapping("/generateAndSave")
//    public String generateAndSaveHtml(String maker) {
//        Context context = new Context();
//        context.setVariable("maker", maker);
//
//        String htmlContent = templateEngine.process("pricecard1", context);
//        htmlContent = htmlContent.replace("</head>", "<link rel=\"stylesheet\" href=\"/css/style.css\">" + "</head>");
//
//        Path currentDirectoryPath = FileSystems.getDefault().getPath("");
//        String currentDirectoryName = currentDirectoryPath.toAbsolutePath().toString();
//
//        System.out.println("currentDirectoryName : " + currentDirectoryName);
//
//        try {
//            File file = new File("src/main/resources/templates/pricecards/generated-file.html");
////            File file = new File("generated-file.html");
//
//            file.getParentFile().mkdirs();
//            Writer writer = new FileWriter(file);
//            writer.write(htmlContent);
//            writer.close();
//
//
//            if (file.exists()) {
//                System.out.println("HTMLファイルは正常に保存されました: " + file.getAbsolutePath());
//            } else {
//                System.out.println("HTMLファイルの保存に失敗しました。");
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return "redirect:/cars";
//    }

    @GetMapping("/g")
    public String g() {
        return "/pricecards/generated-file";
    }
}