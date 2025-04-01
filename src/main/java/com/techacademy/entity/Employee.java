package com.techacademy.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "employees")
//@SQLRestriction("")
public class Employee {

    public static enum Role {
        GENERAL("一般"), ADMIN("管理者");
        private String name;
        private Role(String name) {
            this.name = name;
        }
        private String getValue() {
            return this.name;
        }
    }

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // メールアドレス
    @Column(length = 255, nullable = false, unique = true)
    private String email;

    // 氏名(名前)
    @Column(length = 20, nullable = false)
    private String name;

    // パスワード
    @Column(length = 255, nullable = false)
    private String password;

    // 所属店舗
    @Column(columnDefinition="VARCHAR(10)", nullable = false)
    private String affiliatedStore;

    // 所属
    @Column(columnDefinition="VARCHAR(10)", nullable = false)
    private String department;

    // 権限
    @Column(columnDefinition="VARCHAR(10)", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // 削除フラグ(論理削除を行うため)
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // OauthId
    @Column(columnDefinition="VARCHAR(50)")
    private String oauthId;
}
