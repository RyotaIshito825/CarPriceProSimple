package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;
//    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ユーザー一覧画面表示
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {

        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            System.out.println(employee.getName());
        }

        Employee userDetailEmployee = employeeService.findByEmail(userDetail.getUsername());
        model.addAttribute("userDetailEmployee", userDetailEmployee);
        model.addAttribute("employees", employees);

        return "employees/list";
    }

    // ユーザー詳細画面表示
    @GetMapping(value = "/{id}")
    public String detail(@AuthenticationPrincipal UserDetail userDetail, @PathVariable Integer id, Model model) {

        Employee employee = employeeService.findById(id);
        model.addAttribute("employee", employee);

        Employee userDetailEmployee = employeeService.findByEmail(userDetail.getUsername());
        model.addAttribute("userDetailEmployee", userDetailEmployee);
        return "employees/detail";
    }

    // ユーザー新規登録画面
    @GetMapping(value = "/add")
    public String create(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        Employee userDetailEmployee = employeeService.findByEmail(userDetail.getUsername());
        model.addAttribute("userDetailEmployee", userDetailEmployee);
        return "employees/create";
    }

    // ユーザー新規登録処理
    @PostMapping(value = "/add")
    public String add() {
        return "redirect:/employees";
    }

    // ユーザー情報更新画面表示
    @GetMapping(value = "/{id}/update")
    public String edit(@AuthenticationPrincipal UserDetail userDetail, @PathVariable Integer id, Model model) {
        Employee employee = employeeService.findById(id);
        model.addAttribute("employee", employee);

        Employee userDetailEmployee = employeeService.findByEmail(userDetail.getUsername());
        model.addAttribute("userDetailEmployee", userDetailEmployee);
        return "employees/edit";
    }

    // ユーザー情報更新処理
    @PostMapping(value = "/{id}/update")
    public String update(@AuthenticationPrincipal UserDetail userDetail, @Validated Employee employee, BindingResult res, Model model) {

        ErrorKinds result = employeeService.update(employee);
        if (ErrorMessage.contains(result)) {
//            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
//            model.addAttribute("");
            return "employees/edit";
        }

        System.out.println(employee.getRole());

        employeeService.updateSaveEmployee(employee);

        if (res.hasErrors()) {
            model.addAttribute("employee", employee);
        }
        System.out.println(employee.getPassword());

        return "redirect:/employees";
    }

    // ユーザー削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete() {
        return "redirect:/employees";
    }
}
