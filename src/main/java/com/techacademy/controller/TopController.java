package com.techacademy.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TopController {

    public TopController() {

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
    // アカウント新規登録画面
    @GetMapping(value = "/login/create_account")
    public String create() {
        return "login/create_account";
    }
    // パスワードリセット画面表示
    @GetMapping(value = "/login/password_reset")
    public String add() {
        return "login/send_email";
    }
}
