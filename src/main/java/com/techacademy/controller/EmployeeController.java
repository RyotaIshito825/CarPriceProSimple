package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Employee;
import com.techacademy.service.EmployeeService;

@Controller
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ユーザー一覧画面表示
    @GetMapping
    public String list(Model model) {

        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            System.out.println(employee.getName());
        }

        model.addAttribute("employees", employees);

        return "employees/list";
    }

    // ユーザー詳細画面表示
    @GetMapping(value = "/{code}")
    public String detail() {
        return "employees/detail";
    }

    // ユーザー新規登録画面
    @GetMapping(value = "/add")
    public String create() {
        return "employees/create";
    }

    // ユーザー新規登録処理
    @PostMapping(value = "/add")
    public String add() {
        return "redirect:/employees";
    }

    // ユーザー情報更新画面表示
    @GetMapping(value = "/{code}/update")
    public String edit() {
        return "employees/edit";
    }

    // ユーザー情報更新処理
    @PostMapping(value = "/{code}/update")
    public String update() {
        return "redirect:/employees";
    }

    // ユーザー削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete() {
        return "redirect:/employees";
    }
}
