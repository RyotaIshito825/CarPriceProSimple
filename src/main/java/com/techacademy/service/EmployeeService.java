package com.techacademy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // 従業員一覧表示
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    // 従業員1件表示
    public Employee findById(Integer id) {
        Optional<Employee> option = employeeRepository.findById(id);
        Employee employee = option.orElse(null);
        return employee;
    }
}
