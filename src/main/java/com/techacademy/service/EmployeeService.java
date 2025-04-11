package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 従業員一覧表示
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    // IDで1件取得
    public Employee findById(Integer id) {
        Optional<Employee> option = employeeRepository.findById(id);
        Employee employee = option.orElse(null);
        return employee;
    }
    // メールで1件取得
    public Employee findByEmail(String email) {
        Optional<Employee> option = employeeRepository.findByEmail(email);
        Employee employee = option.orElse(null);
        return employee;
    }
    // OauthIdで1件取得
    public Employee findByOauthId(String oauthId) {
        Optional<Employee> option = employeeRepository.findByOauthId(oauthId);
        Employee employee = option.orElse(null);
        return employee;
    }

    // 従業員新規登録保存
    public void addSaveEmployee(Employee employee) {
        LocalDateTime now = LocalDateTime.now();
        employee.setAffiliatedStore("フリー");
        employee.setDepartment("営業");
        employee.setRole(Employee.Role.GENERAL);
        employee.setDeleteFlg(false);
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        employeeRepository.save(employee);
    }
    // 従業員新規登録
    public List<ErrorKinds> add(Employee employee) {

        List<ErrorKinds> listErrorKinds = new ArrayList<>();
        ErrorKinds result = !employee.getPassword().equals("") ? ErrorKinds.CHECK_OK : passwordCheck(employee);
        // パスワードチェック
        if (employee.getPassword().equals("")) {
            listErrorKinds.add(ErrorKinds.PASSWORD_BLANK_ERROR);
        } else if (ErrorKinds.CHECK_OK != passwordCheck(employee)) {
            listErrorKinds.add(passwordCheck(employee));
        }
        // メールチェック
        if (employee.getEmail().equals("")) {
            listErrorKinds.add(ErrorKinds.EMAIL_BLANK_ERROR);
        }
        // メール重複チェック
        Employee emp = findByEmail(employee.getEmail());
        if (emp != null) {
            listErrorKinds.add(ErrorKinds.EMAIL_DUPLICATION_ERROR);
        }
        // 名前チェック文字数チェック
        if (employee.getName().equals("")) {
            listErrorKinds.add(ErrorKinds.TEXT_BLANK_ERROR);
        }
        if (isOutOfRangeText(employee)) {
            listErrorKinds.add(ErrorKinds.TEXT_20RANGECHECK_ERROR);
        }
        if (listErrorKinds.isEmpty()) {
            listErrorKinds.add(ErrorKinds.SUCCESS);
            return listErrorKinds;
        }

        return listErrorKinds;
    }

    // 従業員更新保存
    public void updateSaveEmployee(Employee employee) {
        Employee emp = findById(employee.getId());
        employee.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        if (employee != null) {
            employee.setUpdatedAt(now);
            employee.setName(employee.getName());
            employee.setDepartment(employee.getDepartment());
            employee.setEmail(employee.getEmail());
            employee.setCreatedAt(emp.getCreatedAt());
            employee.setOauthId(emp.getOauthId());
            employee.setRole(emp.getRole());
        }
        if (employee.getPassword().equals("")) {
            employee.setPassword(emp.getPassword());
        } else {
            employee.setPassword(employee.getPassword());
        }
        employeeRepository.save(employee);
    }

    // 従業員情報更新
    @Transactional
    public ErrorKinds update(Employee employee) {
        // パスワードチェック
        ErrorKinds result = employee.getPassword().equals("") ? ErrorKinds.CHECK_OK : passwordCheck(employee);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }
        // メール重複チェック
        Employee nowEmployee = findById(employee.getId());
        if (findById(employee.getId()).getEmail() != null && !nowEmployee.getEmail().equals(employee.getEmail())) {
            return ErrorKinds.EMAIL_BLANK_ERROR;
        }
        return ErrorKinds.SUCCESS;
    }

    // パスワードチェック
    public ErrorKinds passwordCheck(Employee employee) {
        // パスワードの半角英数字チェック処理
        if (isHalfSizeCheckError(employee)) {
            return ErrorKinds.PASSWORD_HALFSIZE_ERROR;
        }
        // パスワードの8文字〜16文字チェック
        if (isOutOfRangePassword(employee)) {
            return ErrorKinds.PASSWORD_RANGECHECK_ERROR;
        }
        //
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return ErrorKinds.CHECK_OK;
    }
    // パスワードの半角英数字チェック処理
    private boolean isHalfSizeCheckError(Employee employee) {
        // 半角英数字チェック
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(employee.getPassword());
        return !matcher.matches();
    }
    // パスワードの8文字〜16文字チェック処理
    public boolean isOutOfRangePassword(Employee employee) {
        // 桁数チェック
        int passwordLength = employee.getPassword().length();
        return passwordLength < 8 || 16 < passwordLength;
    }
    // 空白チェック
    public ErrorKinds blankCheck(Employee employee) {
        return employee.getName().isEmpty() || employee.getEmail().isEmpty() || employee.getDepartment().isEmpty() ? ErrorKinds.BLANK_ERROR : ErrorKinds.CHECK_OK;
    }
    // テキストの文字数チェック処理
    public boolean isOutOfRangeText(Employee employee) {
        String name = employee.getName();
        if (name == null) {
            return false;
        }
        return name.length() > 20;
    }

}
