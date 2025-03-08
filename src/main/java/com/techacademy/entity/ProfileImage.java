package com.techacademy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "profileimages")
public class ProfileImage {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ファイル名
    @Column(nullable = false)
    private String fileName;

    // ファイルパス
    @Column(nullable = false)
    private String filePath;

    // ファイルタイプ
    @Column(nullable = false)
    private String fileType;

    // ファイルサイズ
    @Column(nullable = false)
    private String fileSize;

    @OneToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee;

}
