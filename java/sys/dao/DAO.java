package sys.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sys.message.Ask_for_leave;
import sys.message.clock_record;
import sys.people.Admin;
import sys.people.User;
import sys.message.work_time;
import sys.people.UserLogin;
import sys.tool.Email;
import sys.tool.PhoneNumber;


public interface DAO {
    public Set<User> getAllUsers();

    public User getUser(int id);

    public User getUser(Email email);

    public User getUser(PhoneNumber phone);

    public User getUser(String code);

    public void addUser(User eminformation);

    public void updateUser(int id, Map<String, Object> updates);

    public void deleteUser(int id);

    public work_time getWorkTime();

    public void updateWorkTime(work_time worktime);

    public void setWorkTime(work_time worktime);

    public void addClockRecord(int id, LocalDateTime now, boolean isLate, boolean isLeave);

    public void deleteClockRecord(int id);

    public List<clock_record> getAllClockRecords();

    public void addLeave(Ask_for_leave leave);

    public void deleteLeave(Ask_for_leave leave);

    public List<Ask_for_leave> getAllAskForLeave();

    public void ExportForExcelMessage();

    public void ExportForExcelClockRecord();

    public boolean AdminLogin(Admin admin);

    public boolean UserLogin(UserLogin UserLogin);
}
