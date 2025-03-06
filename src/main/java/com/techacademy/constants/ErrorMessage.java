package com.techacademy.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// エラーメッセージ用クラス
public class ErrorMessage {
    // エラーメッセージ情報マップ
    private final static Map<ErrorKinds, List<String>> errorMessageMap = new HashMap<ErrorKinds, List<String>>() {
        private static final long serialVersionUID = 1L;
        {
            put(ErrorKinds.BLANK_ERROR, new ArrayList<String>(Arrays.asList("blankError", "値を入力してください")));
            put(ErrorKinds.PASSWORD_BLANK_ERROR, new ArrayList<String>(Arrays.asList("passwordError", "値を入力してください")));
            put(ErrorKinds.PASSWORD_HALFSIZE_ERROR, new ArrayList<String>(Arrays.asList("passwordError", "パスワードは半角英数字のみで入力してください")));

            put(ErrorKinds.PASSWORD_RANGECHECK_ERROR, new ArrayList<String>(Arrays.asList("passwordError", "8文字以上16文字以下で入力してください")));
            put(ErrorKinds.TEXT_RANGECHECK_ERROR, new ArrayList<String>(Arrays.asList("textError", "20文字以内で入力してください")));
            put(ErrorKinds.THREE_RANGECHECK_ERROR, new ArrayList<String>(Arrays.asList("textError", "3文字以内で入力してください")));
            put(ErrorKinds.ONE_RANGECHECK_ERROR, new ArrayList<String>(Arrays.asList("textError", "1文字以内で入力してください")));
        }
    };

    // エラーメッセージマップにあるエラーかどうかのチェック
    public static boolean contains(ErrorKinds errorKinds) {
        if (errorMessageMap.containsKey(errorKinds)) {
            return true;
        } else {
            return false;
        }
    }

    // エラーメッセージの名称を取得
    public static String getErrorName(ErrorKinds errorKinds) {
        return errorMessageMap.get(errorKinds).get(0);
    }

    // エラーメッセージの値を取得
    public static String getErrorValue(ErrorKinds errorKinds) {
        return errorMessageMap.get(errorKinds).get(1);
    }

}
