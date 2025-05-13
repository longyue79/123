package sys.tool;

import sys.dao.DAO;
import sys.dao.DAOMysql;
import sys.message.Ask_for_leave;
import sys.message.clock_record;
import sys.message.work_time;
import sys.people.User;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Judgment {
    private static final String CLOCK_START = "S";
    private static final String CLOCK_END = "E";
    private static final String LATE = "L";
    private static final String LEAVE_EARLY = "LE";
    private static final String ABSENT = "A";
    private static final String HOLIDAY = "H";

    public Judgment(){}

    public boolean isChinaPhone(String phone) {
        if (phone == null) {
            throw new IllegalArgumentException("phone number can not be null");
        }
        if(phone.length() != 14) {
            return false;
        }
        if(phone.charAt(0) != '+' || phone.charAt(1) != '8' || phone.charAt(2) != '6') {
            return false;
        }
        return true;
    }

    public boolean isLate(User eminformation) throws ParseException {
        DAOMysql dao = new DAOMysql();
        work_time worktime = dao.getWorkTime();
        LocalDateTime m_start = DateTimeSet.dateOf(worktime.getMorningStartTimeStr());
        LocalDateTime m_end = DateTimeSet.dateOf(worktime.getMorningEndTimeStr());
        LocalDateTime a_start = DateTimeSet.dateOf(worktime.getAfternoonStartTimeStr());
        LocalDateTime a_end = DateTimeSet.dateOf(worktime.getAfternoonEndTimeStr());
        List<clock_record> records = dao.getAllClockRecords();
        List<clock_record> one_day_records = new ArrayList<>();

        return false;
    }

    public boolean isLeave(User eminformation){
        DAO dao = new DAOMysql();
        List<Ask_for_leave> leaves = dao.getAllAskForLeave();
        for(Ask_for_leave leave : leaves){
            if(eminformation.getId().equals(leave.getId())){
                return true;
            }
        }
        return false;
    }

}
