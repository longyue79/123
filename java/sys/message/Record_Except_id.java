package sys.message;

import com.alibaba.excel.annotation.ExcelProperty;

import java.util.Date;
import java.time.*;

public class Record_Except_id {
    @ExcelProperty("员工姓名")
    private String name;
    @ExcelProperty("打卡时间")
    private LocalDateTime clockTime;
    @ExcelProperty("是否迟到")
    private boolean isLate;
    @ExcelProperty("是否请假")
    private boolean isLeave;

    public Record_Except_id(){}

    public Record_Except_id(String name, String clockTime, boolean isLate, boolean isLeave){
        this.name = name;
        this.clockTime = LocalDateTime.parse(clockTime);
        this.isLate = isLate;
        this.isLeave = isLeave;
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
