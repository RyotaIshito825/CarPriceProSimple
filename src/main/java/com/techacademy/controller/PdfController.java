package com.techacademy.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;

import com.techacademy.entity.Car;
import com.techacademy.entity.PriceCard;
import com.techacademy.service.CarService;
import com.techacademy.service.PdfService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    @GetMapping("/generate-pdf")
//    public String generatePdf(HttpServletRequest req, String priceCardName, Integer id, Model model) {
    public String generatePdf(HttpServletRequest req, String priceCardName, @ModelAttribute Car oneCar, HttpSession session, Model model) {

        // 支払月の設定
        int currentMonth = LocalDate.now().getMonthValue(); // 1〜12
        model.addAttribute("currentMonth", currentMonth);

        String priceCardNum = priceCardName.replace("template_image-", "");

        PriceCard priceCard = (PriceCard) req.getSession().getAttribute("priceCard");
        if (priceCard == null) {
            return "redirect:/cars/list";
        }

        String shopName = (String) session.getAttribute("shopName");
        String shopImageBase64 = (String) session.getAttribute("shopImageBase64");

        model.addAttribute("shopName", shopName);
        model.addAttribute("shopImageBase64", shopImageBase64);

        List<Car> carList = new ArrayList<>();
        Car car = oneCar;
        carList.add(car);

        model.addAttribute("carList", carList);

        int carPrice = Integer.parseInt(String.valueOf(car.getPrice()) + String.valueOf(car.getPriceDpf()));
        int carTotalPrice = Integer.parseInt(String.valueOf(car.getTotalPrice()) + String.valueOf(car.getTotalPriceDpf()));

        int calcPrice = carTotalPrice - carPrice;

        int calcPriceOfInt = Integer.parseInt(String.valueOf(calcPrice).substring(0, String.valueOf(calcPrice).length() - 1));
        int calcPriceOfDpf = Integer.parseInt(String.valueOf(calcPrice).substring(String.valueOf(calcPrice).length() - 1, String.valueOf(calcPrice).length()));

        model.addAttribute("calcPriceOfInt", calcPriceOfInt);
        model.addAttribute("calcPriceOfDpf", calcPriceOfDpf);
        if (car.getClassification().getValue().equals("新車・未使用車")) {
            model.addAttribute("with", false);
            model.addAttribute("none", true);
        } else {
            model.addAttribute("with", true);
            model.addAttribute("none", false);
        }
        return "pricecards/pricecard" + priceCardNum;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/generate-pdfs")
    public String generatePdfLists(@RequestParam(required = false) List<String> id,
            @RequestParam String option,
            HttpServletRequest req, String priceCardName, Model model, HttpSession session) {

        String priceCardNum = priceCardName.replace("template_image-", "");
        // 支払月の設定
        int currentMonth = LocalDate.now().getMonthValue(); // 1〜12

        List<Car> carList = (List<Car>) session.getAttribute("carList");
        String shopName = (String) session.getAttribute("shopName");
        String shopImageBase64 = (String) session.getAttribute("shopImageBase64");

        model.addAttribute("shopName", shopName);
        model.addAttribute("shopImageBase64", shopImageBase64);

        if (option.equals("一括削除") || option.equals("delete")) {
            System.out.println(option);
            carList = new ArrayList<>();
            session.setAttribute("carList", carList);
        }

        if (id == null) {
            return "redirect:/cars/list";
        }

        PriceCard priceCard = (PriceCard) req.getSession().getAttribute("priceCard");
        if (priceCard == null) {
            return "redirect:/cars/list";
        }

        if (option.equals("create") || option.equals("pdf作成")) {

            carList = (List<Car>) session.getAttribute("carList");
            System.out.println(carList);
            if (carList == null) {
                carList = new ArrayList<>();
            }
            List<Integer> calcPriceOfIntList = new ArrayList<>();
            List<Integer> calcPriceOfDpfList = new ArrayList<>();

//            for (String carId : id) {
            for (Car car : carList) {
//                Car car = carService.findById(Integer.parseInt(carId));
//                carList.add(car);

                int carPrice = Integer.parseInt(String.valueOf(car.getPrice()) + String.valueOf(car.getPriceDpf()));
                int carTotalPrice = Integer.parseInt(String.valueOf(car.getTotalPrice()) + String.valueOf(car.getTotalPriceDpf()));

                int calcPrice = carTotalPrice - carPrice;

                Integer calcPriceOfInt = Integer.parseInt(String.valueOf(calcPrice).substring(0, String.valueOf(calcPrice).length() - 1));
                Integer calcPriceOfDpf = Integer.parseInt(String.valueOf(calcPrice).substring(String.valueOf(calcPrice).length() - 1, String.valueOf(calcPrice).length()));

                calcPriceOfIntList.add(calcPriceOfInt);
                calcPriceOfDpfList.add(calcPriceOfDpf);
            }
            model.addAttribute("carList", carList);
            model.addAttribute("calcPriceOfIntList", calcPriceOfIntList);
            model.addAttribute("calcPriceOfDpfList", calcPriceOfDpfList);

            model.addAttribute("shopName", shopName);
            model.addAttribute("shopImageBase64", shopImageBase64);
            model.addAttribute("currentMonth", currentMonth);

            session.setAttribute("carList", carList);

            return "pricecards/pricecard" + priceCardNum;
        } else {
            return "redirect:/cars/list";
        }
    }
}