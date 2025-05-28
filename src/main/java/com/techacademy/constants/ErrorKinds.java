package com.techacademy.constants;


// エラーメッセージ定義
public enum ErrorKinds {

    // エラー内容
    // 空白チェックエラー
    BLANK_ERROR,
    // メール区白チェックエラー
    EMAIL_BLANK_ERROR,
    // メール重複チェックエラー
    EMAIL_DUPLICATION_ERROR,
    // パスワード空白チェックエラー
    PASSWORD_BLANK_ERROR,
    // パスワード半角英数字チェックエラー
    PASSWORD_HALFSIZE_ERROR,
    // パスワードE桁数(8桁〜16桁以外)チェックエラー
    PASSWORD_RANGECHECK_ERROR,
    // 文字空白チェックエラー
    TEXT_BLANK_ERROR,
    // 桁数(20文字以内)チェックエラー
    TEXT_RANGECHECK_ERROR,
    // 桁数(20文字以内)チェックエラー
    TEXT_20RANGECHECK_ERROR,
    // 桁数(3文字以内)チェックエラー
    THREE_RANGECHECK_ERROR,
    // 桁数(1文字)チェックエラー
    ONE_RANGECHECK_ERROR,
    // 権限チェックエラー
    GOOGLESPREADSHEET_AUTHORITY_ERROR,
    // ユーザー有無エラー
    EMPLOYEE_NOTFOUND_ERROR,
    // ファイルサイズエラー
    FILE_SIZE_ERROR,
    // ファイルタイプエラー
    FILE_TYPE_ERROR,
    //
    // チェックOK
    CHECK_OK,
    // 正常終了
    SUCCESS;

}
