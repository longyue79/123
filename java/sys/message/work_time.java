package sys.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import sys.tool.DateTimeSet;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@TableName("work_time")
public class work_time {
    @TableId(value = "time_id", type = IdType.AUTO)
    private Integer timeId;
    private String morningStartTime;
    private String morningEndTime;
    private String afternoonStartTime;
    private String afternoonEndTime;

    public work_time(String morningStartTime, String morningEndTime, String afternoonStartTime, String afternoonEndTime) {
        // 使用 DateTimeSet 类的方法格式化时间字符串
        this.morningStartTime = DateTimeSet.formatTimeString(morningStartTime);
        this.morningEndTime = DateTimeSet.formatTimeString(morningEndTime);
        this.afternoonStartTime = DateTimeSet.formatTimeString(afternoonStartTime);
        this.afternoonEndTime =  DateTimeSet.formatTimeString(afternoonEndTime);
        validateTimes();
    }





    private void validateTimes() {
        // 检查上午时间段有效性
        if (parseTime(morningStartTime).isAfter(parseTime(morningEndTime))) {
            throw new IllegalArgumentException("上午上班时间不能晚于下班时间");
        }

        // 检查下午时间段有效性
        if (parseTime(afternoonStartTime).isAfter(parseTime(afternoonEndTime))) {
            throw new IllegalArgumentException("下午上班时间不能晚于下班时间");
        }

        // 检查上午和下午时间是否重叠
        if (!parseTime(morningEndTime).isBefore(parseTime(afternoonStartTime))) {
            throw new IllegalArgumentException("上午下班时间必须早于下午上班时间");
        }
    }

    private LocalTime parseTime(String timeString) {
        try {
            // 使用 DateTimeSet 类的方法解析时间字符串
            String formattedTime = DateTimeSet.parseTime(timeString);
            return LocalTime.parse(formattedTime);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("时间格式应为HH:mm:ss，例如09:00:00");
        }
    }

    // Getter方法
    public String getMorningStartTime() {
        return morningStartTime;
    }

    public String getMorningEndTime() {
        return morningEndTime;
    }

    public String getAfternoonStartTime() {
        return afternoonStartTime;
    }

    public String getAfternoonEndTime() {
        return afternoonEndTime;
    }

    // 转换为字符串格式
    public String getMorningStartTimeStr() {
        return morningStartTime;
    }

    public String getMorningEndTimeStr() {
        return morningEndTime;
    }

    public String getAfternoonStartTimeStr() {
        return afternoonStartTime;
    }

    public String getAfternoonEndTimeStr() {
        return afternoonEndTime;
    }
}