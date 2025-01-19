package giybat.uz.util;


import giybat.uz.profile.enums.UsernameEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidationUtil {
            private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
            private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

            private static final String PHONE_REGEX = "^\\+998\\d{9}$";
            private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

            public UsernameEnum identifyInputType(String input) {
                if (isEmail(input)) {
                    return UsernameEnum.EMAIL;
                } else if (isPhoneNumber(input)) {
                    return UsernameEnum.PHONE_NUMBER;
                } else {
                    return UsernameEnum.UNKNOWN;
                }
            }

            private boolean isEmail(String input) {
                Matcher matcher = EMAIL_PATTERN.matcher(input);
                return matcher.matches();
            }

            private boolean isPhoneNumber(String input) {
                Matcher matcher = PHONE_PATTERN.matcher(input);
                return matcher.matches();
            }
}
