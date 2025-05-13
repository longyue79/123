package sys.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.time.*;

@TableName("ask_for_leave")
public class Ask_for_leave {
    @TableId(value = "leave_em_id", type = IdType.AUTO)
    int id;
    String name;
    LocalDateTime start_time;
    LocalDateTime end_time;

    public Ask_for_leave(int id, String name, String start_time, String end_time) {
        this.id = id;
        this.name = name;
        this.start_time = LocalDateTime.parse(start_time);
        this.end_time = LocalDateTime.parse(end_time);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = LocalDateTime.parse(start_time);
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = LocalDateTime.parse(end_time);
    }
}
