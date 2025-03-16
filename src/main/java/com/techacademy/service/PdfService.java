package com.techacademy.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class PdfService {

    public void generatePdf() {
        // Node.jsのフルパスを指定
        String nodePath = "/usr/local/bin/node";  // Linux/Macの場合、Windowsの場合は"C:\\Program Files\\nodejs\\node.exe"
        String nodeScriptPath = "src/main/resources/node-scripts/puppeteer-pdf.js";

        ProcessBuilder processBuilder = new ProcessBuilder(nodePath, nodeScriptPath);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();  // 終了コードを取得

            if (exitCode == 0) {
                System.out.println("PDF生成成功！");
            } else {
                System.out.println("PDF生成失敗！");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}