package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Car;
import com.techacademy.service.CarService;

@Controller
@RequestMapping("cars")
public class CarsController {

    private final CarService carService;

    @Autowired
    public CarsController(CarService carService) {
        this.carService = carService;
    }

    // 車両一覧画面表示
    @GetMapping
    public String top(Model model) {

        List<Car> cars = carService.findAll();
        for (Car car : cars) {
            System.out.println(car.getMaker());
        }
        model.addAttribute("carList", cars);

        return "cars/list";
    }

    // 車両詳細画面表示
    @GetMapping(value = "/{id}")
    public String detail(@PathVariable Integer id, Model model) {

        Car car = carService.findById(id);
        model.addAttribute(car);

        return "cars/detail";
    }

    // 車両新規登録
    @GetMapping(value = "/add")
    public String create() {
        return "cars/new";
    }
    // 車両新規登録処理
    @PostMapping(value = "/add")
    public String add() {
        return "redirect:/cars";
    }

    // 車両更新画面表示
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable Integer id, Model model) {
        Car car = carService.findById(id);
        model.addAttribute(car);
        return "cars/edit";
    }
    // 車両更新処理
    @PostMapping(value = "/{id}/update")
    public String update() {
        return "redirect:/cars";
    }

    // 車両削除処理
    @PostMapping(value = "/delete")
    public String delete() {
        return "redirect:/cars";
    }

    // データ取込画面表示
    @GetMapping(value = "/intake")
    public String intake() {
        return "cars/intake";
    }
    // データ取込処理
    @PostMapping(value = "/intake")
    public String regi() {
        return "redirect:/cars";
    }

    // テンプレート一覧画面表示
    @GetMapping(value = "/template")
    public String template() {
        return "cars/template";
    }

}
