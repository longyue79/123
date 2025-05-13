package sys.tool;

import java.util.regex.*;

public class PhoneNumber {
    private static final Pattern Basic_Pattern = Pattern.compile("^[\\+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,4}[-\\s.]?[0-9]{2,6}$");


    private static final Pattern China_Pattern = Pattern.compile("^(\\+86)?1[3-9]\\d{9}$");

    private final String number;

    public PhoneNumber(String number, boolean isChinaPhone) {
        if (number == null) {
            throw new IllegalArgumentException("phone number can not be null");
        }

        String cleaned = number.replaceAll("[^+0-9]", "");
        Pattern pattern = isChinaPhone ? China_Pattern : Basic_Pattern;

        if (!pattern.matcher(number).matches() && !pattern.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        this.number = cleaned;
    }

    public String getNumber() {
        return number;
    }



    @Override
    public String toString() {
        return number;
    }

}
