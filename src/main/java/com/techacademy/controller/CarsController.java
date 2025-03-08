package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Car;
import com.techacademy.entity.Employee;
import com.techacademy.entity.PriceCard;
import com.techacademy.service.CarService;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.PriceCardService;

@Controller
@RequestMapping("cars")
public class CarsController {

    private final CarService carService;
    private final PriceCardService priceCardService;
    private final EmployeeService employeeService;

    @Autowired
    public CarsController(CarService carService, PriceCardService priceCardService, EmployeeService employeeService) {
        this.carService = carService;
        this.priceCardService = priceCardService;
        this.employeeService = employeeService;
    }

    // 車両一覧画面表示
    @GetMapping
    public String top(Model model) {

        List<Car> cars = carService.findAll();
        model.addAttribute("carList", cars);

        return "cars/list";
    }

    // 車両詳細画面表示
    @GetMapping(value = "/{id}")
    public String detail(@PathVariable Integer id, Model model) {

        Car car = carService.findById(id);
        boolean viExits = String.valueOf(car.getViYear()) == null ? false : true;

        model.addAttribute("viExits", viExits);
        model.addAttribute("car", car);

        return "cars/detail";
    }

    // 車両新規登録
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Car car) {
        return "cars/new";
    }
    // 車両新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Car car, BindingResult res, Model model) {

        if (res.hasErrors()) {
            model.addAttribute("car", car);
            return "cars/new";
        }

        ErrorKinds result = carService.createCar(car);
        if (result == ErrorKinds.SUCCESS) {
            carService.createCarSave(car);
        }
        return "redirect:/cars";
    }

    // 車両更新画面表示
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable Integer id, Model model) {
        Car car = carService.findById(id);
        model.addAttribute("car", car);
        return "cars/edit";
    }
    // 車両更新処理
    @PostMapping(value = "/{id}/update")
    public String update(@Validated Car car, BindingResult res, Model model) {

        if (res.hasErrors()) {
            model.addAttribute("car", car);
            return "cars/edit";
        }

        ErrorKinds result = carService.updateCar(car);
        if (result == ErrorKinds.SUCCESS) {
            carService.updateCarSave(car);
        }

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
    // テンプレート
    @PostMapping(value = "/template")
    public String confirmedTemplate(@RequestParam("priceCardName") String priceCardName) {


        PriceCard priceCard = priceCardService.findByEmployeeId(1);
        if (priceCard == null) {
            PriceCard newPriceCard = new PriceCard();
            newPriceCard.setPriceCardName(priceCardName);
            newPriceCard.setEmployee(new Employee());
            priceCardService.save(newPriceCard);
        } else {
            priceCard.setPriceCardName(priceCardName);
            priceCardService.save(priceCard);
        }
        System.out.println(priceCardName);
        return "redirect:/cars";
    }
}
