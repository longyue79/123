package sys.tool;

import java.util.Objects;
import java.util.regex.Pattern;

public class Employee_id {
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Z0-9]{8}$");
    private final String id;

    public Employee_id(String id) {
        Objects.requireNonNull(id, "员工编号不能为null");

        if (!ID_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException("员工编号必须为8位");
        }
        this.id = id;
    }

    public static Employee_id generateRandom() {
        String characters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 去掉了容易混淆的字符
        StringBuilder sb = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }

        return new Employee_id(sb.toString());
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee_id that = (Employee_id) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

    public static Employee_id generatePatterned(String departmentCode, int year, int sequence) {
        String formatted = String.format("%2s%02d%04d",
                departmentCode,
                year % 100,
                sequence);
        return new Employee_id(formatted);
    }
}
