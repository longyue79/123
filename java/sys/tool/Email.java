package sys.tool;

import java.util.regex.*;

public class Email {
    private static final Pattern Email_Pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,63}$");

    private final String address;

    public Email(String address) {
        if (address == null || !Email_Pattern.matcher(address).matches()) {
            throw new IllegalArgumentException("Invalid email address format");
        }
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return address;
    }
}
