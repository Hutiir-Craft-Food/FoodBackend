package com.khutircraftubackend.confirm;

public class ConfirmResponseMessages {

    public static final String EMAIL_ACCEPTED = "Ваш email вже потвержден.";
    public static final String CONFIRM_NOT_FOUND = "Не правильній токен пітвердження почти.";
    public static final String CONFIRM_TIME_IS_UP = "Время жизни токена пітвердження почти вишел.";
    public static final String CONFIRM_ACCEPTED = "Ваш email потвержден";
    public static final String VERIFICATION_CODE_SUBJECT = "Підтвердження реєстрації. ";
    public static final String VERIFICATION_CODE_TEXT =
            "Будь ласка, підтвердіть вашу реєстрацію. Вести 6 значний код %s ";

    private ConfirmResponseMessages() {
    }
}
