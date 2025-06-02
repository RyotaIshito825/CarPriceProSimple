package com.techacademy.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Collections;

public class GoogleSheetsService {

    public static Sheets getSpreadsheets() throws IOException, GeneralSecurityException {
        // Base64でエンコードされた環境変数からJSONを読み込む
        String base64Json = System.getenv("GOOGLE_CREDENTIALS_BASE64");

        if (base64Json == null || base64Json.isEmpty()) {
            throw new IllegalStateException("環境変数 GOOGLE_CREDENTIALS_BASE64 が設定されていません。");
        }

        byte[] decodedJson = Base64.getDecoder().decode(base64Json);
        GoogleCredentials credential = GoogleCredentials.fromStream(new ByteArrayInputStream(decodedJson))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credential);

        return new Sheets.Builder(transport, jsonFactory, requestInitializer)
                .setApplicationName("CarPricePro") // ← アプリ名を必ず設定
                .build();
    }
}

