package com.techacademy.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;

import com.techacademy.entity.Car;
import com.techacademy.service.CarService;
import com.techacademy.service.PdfService;

import jakarta.servlet.http.HttpServletRequest;

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
    public String generatePdf(HttpServletRequest req, String priceCardName, Integer id, Model model) {

        if (priceCardName == null) {
            return "redirect:/cars/list";
        }

        List<Car> carList = new ArrayList<>();
        Car car = carService.findById(id);
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


        return "/pricecards/pricecard1";
    }

    @GetMapping("/generate-pdfs")
    public String generatePdfLists(@RequestParam(name = "id", required = false) List<String> id,
            @RequestParam(name = "option") String option,
            HttpServletRequest req, String priceCardName, Model model) {

        if (id == null) {
            return "redirect:/cars/list";
        }

        if (priceCardName == null) {
            return "redirect:/cars/list";
        }

        if (option.equals("create") || option.equals("pdf作成")) {
            for (String carId : id) {
                generatePdf(req, priceCardName, Integer.parseInt(carId), model);
            }

            List<Car> carList = new ArrayList<>();
            List<Integer> calcPriceOfIntList = new ArrayList<>();
            List<Integer> calcPriceOfDpfList = new ArrayList<>();

            for (String carId : id) {
                Car car = carService.findById(Integer.parseInt(carId));
                carList.add(car);

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

            return "/pricecards/pricecard1";
        } else {
            return "redirect:/cars/list";
        }
    }
}