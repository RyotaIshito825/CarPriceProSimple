package com.techacademy.controller;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.MailSenderService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Controller
public class TopController {

    private final MailSenderService mailSenderService;
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String SECRET_KEY = "your-256-bit-long-secret-key-should-be-here"; // 署名に使用する秘密鍵


    public TopController(MailSenderService mailSenderService, EmployeeService employeeService, PasswordEncoder passwordEncoder, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.mailSenderService = mailSenderService;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ログイン画面表示
    @GetMapping(value = "/login")
    public String login() {

        return "login/login";
    }
    // ログイン後のトップページ表示
    @GetMapping(value = "/")
    public String top() {
        return "redirect:/cars/list";
    }
    // パスワード再設定画面の表示
    @GetMapping(value = "/password_reset")
    public String showPasswordResetPage() {
        return "login/passwordreset";
    }
    // アカウント新規登録画面
    @GetMapping(value = "/login/create_account")
    public String create(@ModelAttribute Employee employee) {
        return "login/create_account";
    }
    // アカウント新規登録処理
    @PostMapping(value = "/login/create_account")
    public String add(Employee employee, Model model) {
        List<ErrorKinds> result = employeeService.add(employee);

        model.addAttribute("errorList", result);
        if (!result.contains(ErrorKinds.SUCCESS)) {
            for (ErrorKinds res : result) {
                String key = ErrorMessage.getErrorName(res);
                String value = ErrorMessage.getErrorValue(res);
                model.addAttribute(key, value);
            }
            return "login/create_account";
        }
        if (result.contains(ErrorKinds.SUCCESS)) {
            employeeService.addSaveEmployee(employee);
        }
        return "login/user_registration";
    }
    // パスワードリセット画面表示
    @GetMapping(value = "/login/password_reset")
    public String reset() {
        return "login/send_email";
    }
    // パスワード再設定URL送信後画面の表示
    @PostMapping(value = "/login/password_reset_submit")
    public String submitPasswordResetPage(String email, Model model) {

        Employee employee = employeeService.findByEmail(email);
        // employeeがnullの場合はエラーで再表示&メールは送らない
        if (employee == null) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.EMPLOYEE_NOTFOUND_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.EMPLOYEE_NOTFOUND_ERROR));
            return showPasswordResetPage();
        }

        // 秘密鍵をKeyオブジェクトに変換
        Key key = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        String token = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .signWith(key)
                .compact();

        mailSenderService.sendResetTokenEmail(email, token);
        return "login/passwordreset_submit";
    }

    // トークンURLからの再設定画面の表示
    @GetMapping(value = "/password/reset")
    public String emailSubmitPasswordResetPage(String token, Model model) {
        Employee employee = employeeService.findByEmail(getClaims(token).getSubject());
        model.addAttribute("employee", employee);
        model.addAttribute("token", token);
        return "login/email_passwordreset";
    }

    @PostMapping(value = "/password/reset/completion")
    public String passwordResetUpdate(String token, String email, String password, Model model) {
        Employee employee = employeeService.findByEmail(email);

        employee.setPassword(password);
        ErrorKinds result = checkPassword(employee);
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return emailSubmitPasswordResetPage(token, model);
        }
        LocalDateTime now = LocalDateTime.now();
        employee.setPassword(passwordEncoder.encode(password)); // パスワードをエンコードしてセット
        employee.setUpdatedAt(now);
        employeeRepository.save(employee);

        return "login/password_setting_complete";
    }


    // JWT情報を返す(※JSON形式のトークンを用いて、クライアントとサーバー間で認証や情報交換を行うための標準規格)
    public Claims getClaims(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims;
    }
    // JWTトークンチェック
    public boolean checkTokenBoolean(Claims claims) {
        try {
            // クレーム情報の取得
            String subject = claims.getSubject();
            Date expiration = claims.getExpiration();
            claims.getExpiration();

            // トークンの有効期限が過ぎていないか
            if (expiration.before(new Date())) {
                return false;
            }
            return true;

        } catch (ExpiredJwtException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (SignatureException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (Exception e) {
            return false;
        }
    }

 // パスワードのチェック
    @Transactional
    public ErrorKinds checkPassword(Employee employee) {
        ErrorKinds result = employeeService.passwordCheck(employee);
        if (ErrorKinds.CHECK_OK != null) {
            return result;
        }
        return ErrorKinds.SUCCESS;
    }

}
