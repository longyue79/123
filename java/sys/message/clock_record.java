package sys.message;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.*;

@TableName("clock_record")
public class clock_record {
    @ExcelProperty("员工 ID")
    @TableId(value = "employee_id", type = IdType.AUTO)
    private Integer employeeId;
    @ExcelProperty("员工姓名")
    private String name;
    @ExcelProperty("打卡时间")
    private LocalDateTime clockTime;
    @ExcelProperty("是否迟到")
    private boolean isLate;
    @ExcelProperty("是否请假")
    private boolean isLeave;

    public static final String STATUS_NORMAL = "正常";
    public static final String STATUS_LATE = "迟到";
    public static final String STATUS_LEAVE_EARLY = "早退";
    public static final String STATUS_ABSENT = "缺席";

    public clock_record() {

    }

    public clock_record(Integer employeeId, String name, String clockTime, boolean isLate, boolean isLeave) {
        this.employeeId = employeeId;
        this.name = name;
        this.clockTime = LocalDateTime.parse(clockTime);
        this.isLate = isLate;
        this.isLeave = isLeave;
    }


    public String getStatus() {
        if (isLeave) {
            return "请假";
        } else if (isLate) {
            return STATUS_LATE;
        } else {
            return STATUS_NORMAL;
        }
    }

    public LocalDateTime getCheckInTime() {
        return clockTime;
    }

    // 新增：判断是否为某天的打卡记录
    public boolean isOnDate(LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
        return !clockTime.isBefore(startOfDay) && !clockTime.isAfter(endOfDay);
    }


    // Getters 和 Setters
    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getClockTime() {
        return clockTime;
    }

    public void setClockTime(String clockTime) {
        this.clockTime = LocalDateTime.parse(clockTime);
    }

    public boolean isLate() {
        return isLate;
    }

    public void setLate(boolean late) {
        isLate = late;
    }

    public boolean isLeave() {
        return isLeave;
    }

    public void setLeave(boolean leave) {
        isLeave = leave;
    }
}
