package com.techacademy.constants;


// エラーメッセージ定義
public enum ErrorKinds {

    // エラー内容
    // 空白チェックエラー
    BLANK_ERROR,
    // パスワード空白チェックエラー
    PASSWORD_BLANK_ERROR,
    // パスワード半角英数字チェックエラー
    PASSWORD_HALFSIZE_ERROR,
    // パスワードE桁数(8桁〜16桁以外)チェックエラー
    PASSWORD_RANGECHECK_ERROR,
    // 桁数(20文字以内)チェックエラー
    TEXT_RANGECHECK_ERROR,
    // 桁数(3文字以内)チェックエラー
    THREE_RANGECHECK_ERROR,
    // 桁数(1文字)チェックエラー
    ONE_RANGECHECK_ERROR,
    // 権限チェックエラー
    GOOGLESPREADSHEET_AUTHORITY_ERROR,
    // チェックOK
    CHECK_OK,
    // 正常終了
    SUCCESS;

}
