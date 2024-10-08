package com.fzq.xiaopotato.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator {
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,}$";

    private static final String PHONE_REGEX = "^\\d{3}-?\\d{3}-?\\d{4}$";

    private static final String ACCOUNT_REGEX = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";


    public static boolean isValidEmail(String email) {
        return validatePattern(email, EMAIL_REGEX);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return validatePattern(phoneNumber, PHONE_REGEX);
    }

    public static boolean isNotValidAccount(String userAccount) {
        return validatePattern(userAccount, ACCOUNT_REGEX);
    }

    private static boolean validatePattern(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
