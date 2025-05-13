package sys.Service;

import org.springframework.stereotype.Service;
import sys.dao.DAO;
import sys.dao.DAOFactory;

import sys.Session.Session;
import sys.message.clock_record;
import sys.message.Record_Except_id;
import sys.message.work_time;
import sys.people.Admin;
import sys.people.User;
import sys.people.UserLogin;
import sys.tool.*;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;


@Service
public class HRService {
    private static final String CLOCK_START = "S";
    private static final String CLOCK_END = "E";
    private static final String LATE = "L";
    private static final String LEAVE_EARLY = "LE";
    private static final String ABSENT = "A";
    private static final String HOLIDAY = "H";

    private static DAO dao = DAOFactory.getDAO();
    private static Judgment judgment = new Judgment();

    public static void loadAllUsers() {
        Session.EMINFORMATIONS.clear();// 全局会话清空所有员工
        // 重新从数据库中加载所有员工的对象集合
        Session.EMINFORMATIONS.addAll(dao.getAllUsers());
    }

    public static boolean AdminLogin(String AdminName, String password) {
        Admin admin = new Admin(AdminName, password);
        if(dao.AdminLogin(admin)) {
            Session.admin = admin;
            return true;
        }
        return false;
    }

    public static Admin validateAdminLogin(String AdminName, String password){
        return null;
    }

    public static boolean UserLogin(String AdminName, String password) {
        UserLogin login = new UserLogin(AdminName, password);
        if(dao.UserLogin(login)) {
            Session.UserLogin = login;
            return true;
        }
        return false;
    }

    public static User addUser(String name, Sex sex, String phoneNumber, String email, BufferedImage image){
        String code = UUID.randomUUID().toString().replace("-", "");
        User eminformation = new User(null, name, sex, phoneNumber, email, code);
        dao.addUser(eminformation);
        eminformation = dao.getUser(code);
        Session.EMINFORMATIONS.add(eminformation);
        return eminformation;
    }

    public static User getUser(int id) {
        for (User eminformation : Session.EMINFORMATIONS) {// 遍历所有员工
            if (eminformation.getId().equals(id)) {// 如果编号是一样的
                return eminformation;// 返回该员工
            }
        }
        return null;// 没找到返回null
    }

    public static User getUser(String code) {
        for (User eminformation : Session.EMINFORMATIONS) {// 遍历所有员工
            if (eminformation.getCode().equals(code)) {// 如果特征码是一样的
                return eminformation;// 返回该员工
            }
        }
        return null;// 没找到返回null
    }

    public static void deleteUser(int id) {
        User eminformation = getUser(id);// 根据编号获取该员工对象
        if (eminformation != null) {// 如果存在该员工
            Session.EMINFORMATIONS.remove(eminformation);// 从员工列表中删除
        }
        dao.deleteUser(id);// 在数据库中删除该员工信息
        dao.deleteClockRecord(id);// 在数据库中删除该员工所有打卡记录
        UserImageService.deleteFaceImage(eminformation.getCode());// 删除该员工人脸照片文件
        Session.FACE_FEATURE_HASH_MAP.remove(eminformation.getCode());// 删除该员工人脸特征
        Session.TIME_HASH_MAP.remove(eminformation.getId());// 删除该员工打卡记录
    }

    public static void loadAllWorkTimes(){
        Session.worktime = dao.getWorkTime();
    }

    public static void loadAllClockRecord(){
        List<clock_record> records = dao.getAllClockRecords();
        if (records == null) {
            System.out.println("表中无打卡数据");
            return;
        }

        for (int i = 0; i < records.size(); i++) {
            Integer id = records.get(i).getEmployeeId();
            if (!Session.TIME_HASH_MAP.containsKey(id)) {
                Session.TIME_HASH_MAP.put(id, new HashSet<>());
            }
            Record_Except_id record_except_id = new Record_Except_id(records.get(i).getName(), records.get(i).getClockTime().toString(),
                    records.get(i).isLate(), records.get(i).isLeave());
            Session.TIME_HASH_MAP.get(id).add(record_except_id);
        }
    }

    public static void addClockRecord(User eminformation)  {
        LocalDateTime time = LocalDateTime.now();
        work_time worktime = Session.worktime;
        boolean isLate = false;
        LocalDateTime zeroTime = null, noonTime = null, lastTime = null, startTime = null, closingTime = null;
        zeroTime = DateTimeSet.dateOf(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), "00:00:00");
        // 中午12点
        noonTime = DateTimeSet.dateOf(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), "12:00:00");
        // 一天中最后一秒
        lastTime = DateTimeSet.dateOf(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), "23:59:59");

        startTime = DateTimeSet.dateOf(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), worktime.getMorningStartTimeStr());

            if (time.isAfter(zeroTime) && (time.isBefore(lastTime))) {
                // 上班前打卡
                if (!(time.isBefore(startTime) || time.equals(startTime))&&!(time.isAfter(startTime) && time.isBefore(noonTime)) ) {
                    isLate = true;
                }

            }


        dao.addClockRecord(eminformation.getId(), time, isLate, judgment.isLeave(eminformation));
    }

   /* public static Map<User, String> getOneDayClockRecord(int year, int month, int day) {
        Map<User, String> map = new HashMap<>();
        WorkTime workTime = Session.workTime;
        LocalDateTime zeroTime = null, noonTime = null, lastTime = null, startTime = null, closingTime = null;
        zeroTime = DateTimeSet.dateOf(year, month, day, "00:00:00");
        // 中午12点
        noonTime = DateTimeSet.dateOf(year, month, day, "12:00:00");
        // 一天中最后一秒
        lastTime = DateTimeSet.dateOf(year, month, day, "23:59:59");

        startTime = DateTimeSet.dateOf(year, month, day, workTime.getMorningStartTimeStr());
        closingTime = DateTimeSet.dateOf(year, month, day, workTime.getAfternoonEndTimeStr());

        for (User e : Session.USERS) {// 遍历所有员工
            String report = "";// 员工打卡记录，初始为空
            // 如果打卡记录中存在该员工的记录
            if (Session.TIME_HASH_MAP.containsKey(e.getId())) {
                boolean isAbsent = true;// 默认为缺席状态
                // 获取该员工的所有打卡记录
                Set<Record_Except_id> lockinSet = Session.TIME_HASH_MAP.get(e.getId());
                for (Record_Except_id record : lockinSet) {// 遍历所有打卡记录
                    // 如果员工在此日期内有打卡记录
                    if (record.getClockTime().isAfter(zeroTime) && record.getClockTime().isBefore(lastTime)) {
                        isAbsent = false;// 不缺席
                        // 上班前打卡
                        if (record.getClockTime().isBefore(startTime) || record.getClockTime().equals(startTime)) {
                            report += CLOCK_START;// 追加上班正常打卡标记
                        }
                        // 下班后打卡
                        if (record.getClockTime().isAfter(closingTime) || record.getClockTime().equals(closingTime)) {
                            report += CLOCK_END;// 追加下班正常打卡标记
                        }
                        // 上班后，中午前打卡
                        if (record.getClockTime().isAfter(startTime) && record.getClockTime().isBefore(noonTime)) {
                            report += LATE;// 追加迟到标记
                        }
                        // 中午后，下班前打卡
                        if (record.getClockTime().isAfter(noonTime) && record.getClockTime().isBefore(closingTime)) {
                            report += LEAVE_EARLY;// 追加早退标记
                        }
                    }
                }
                if (isAbsent) {// 此人在此日期没有打卡记录
                    report = ABSENT;// 指定为缺席标记
                }
            } else {// 如果打卡记录里没有此人记录
                report = ABSENT;// 指定为缺席标记
            }
            map.put(e, report);// 保存该员工的打卡记录
        }
        return map;
        }*/

    public User validateEmployeeLogin(String username, String password) {
        UserLogin login = new UserLogin(username, password);
        if (dao.UserLogin(login)) {
            return dao.getUser(username);
        }
        return null;
    }

    //public void recordCheckIn(User user) {
       // dao.addClockRecord(user.getId(), java.time.LocalDateTime.now(), false, false);
   // }

    public static List<clock_record> getAttendanceRecords(Integer employeeId) {
        List<clock_record> allRecords = dao.getAllClockRecords();
        List<clock_record> userRecords = new ArrayList<>();

        for (clock_record record : allRecords) {
            if (record.getEmployeeId().equals(employeeId)) {
                userRecords.add(record);
            }
        }

        return userRecords;
    }

    // 修改：获取一天的考勤记录
    public static Map<User, List<clock_record>> getOneDayClockRecord(int year, int month, int day) {
        Map<User, List<clock_record>> map = new HashMap<>();
        work_time worktime = Session.worktime;
        LocalDateTime targetDate = DateTimeSet.dateOf(year, month, day, "00:00:00");

        // 遍历所有员工
        for (User e : Session.EMINFORMATIONS) {
            List<clock_record> records = new ArrayList<>();

            // 如果打卡记录中存在该员工的记录
            if (Session.TIME_HASH_MAP.containsKey(e.getId())) {
                // 获取该员工的所有打卡记录
                Set<Record_Except_id> lockinSet = Session.TIME_HASH_MAP.get(e.getId());

                for (Record_Except_id record : lockinSet) {
                    // 如果员工在此日期内有打卡记录
                    if (record.getClockTime().isAfter(targetDate) &&
                            record.getClockTime().isBefore(targetDate.plusDays(1))) {

                        // 将 Record_Except_id 转换为 ClockRecord
                        clock_record clockrecord = new clock_record(
                                e.getId(),
                                e.getName(),
                                record.getClockTime().toString(),
                                record.isLate(),
                                record.isLeave()
                        );

                        records.add(clockrecord);
                    }
                }
            }

            map.put(e, records);
        }

        return map;
    }

    // 新增：获取一天的考勤报表
    public static String getDayReport(int year, int month, int day) {
        Set<String> lateSet = new HashSet<>();    // 迟到名单
        Set<String> leftSet = new HashSet<>();    // 早退名单
        Set<String> absentSet = new HashSet<>();  // 缺席名单

        // 获取这一天所有人的打卡数据
        Map<User, List<clock_record>> recordsMap = HRService.getOneDayClockRecord(year, month, day);

        for (User eminformation : recordsMap.keySet()) {
            List<clock_record> records = recordsMap.get(eminformation);

            if (records.isEmpty()) {
                absentSet.add(eminformation.getName());
                continue;
            }

            boolean hasNormalCheckIn = false;
            boolean hasNormalCheckOut = false;
            boolean hasLate = false;
            boolean hasLeaveEarly = false;

            work_time worktime = Session.worktime;
            LocalDateTime targetDate = DateTimeSet.dateOf(year, month, day, "00:00:00");
            LocalDateTime morningStart = DateTimeSet.dateOf(
                    year, month, day, worktime.getMorningStartTimeStr());
            LocalDateTime afternoonEnd = DateTimeSet.dateOf(
                    year, month, day, worktime.getAfternoonEndTimeStr());

            for (clock_record record : records) {
                if (record.getClockTime().isBefore(morningStart)) {
                    hasNormalCheckIn = true;
                } else if (record.getClockTime().isAfter(afternoonEnd)) {
                    hasNormalCheckOut = true;
                } else if (record.isLate()) {
                    hasLate = true;
                }
            }

            // 如果有迟到且没有正常上班打卡
            if (hasLate && !hasNormalCheckIn) {
                lateSet.add(eminformation.getName());
            }

            // 早退逻辑可以根据实际需求扩展
        }

        StringBuilder report = new StringBuilder();
        int count = Session.EMINFORMATIONS.size();

        // 拼接报表内容
        report.append("-----  " + year + "年" + month + "月" + day + "日  -----\n");
        report.append("应到人数：" + count + "\n");

        report.append("缺席人数：" + absentSet.size() + "\n");
        report.append("缺席名单：");
        if (absentSet.isEmpty()) {
            report.append("（空）\n");
        } else {
            for (String name : absentSet) {
                report.append(name + " ");
            }
            report.append("\n");
        }

        report.append("迟到人数：" + lateSet.size() + "\n");
        report.append("迟到名单：");
        if (lateSet.isEmpty()) {
            report.append("（空）\n");
        } else {
            for (String name : lateSet) {
                report.append(name + " ");
            }
            report.append("\n");
        }

        report.append("早退人数：" + leftSet.size() + "\n");
        report.append("早退名单：");
        if (leftSet.isEmpty()) {
            report.append("（空）\n");
        } else {
            for (String name : leftSet) {
                report.append(name + " ");
            }
            report.append("\n");
        }

        return report.toString();
    }

   /* public static String getDayReport(int year, int month, int day) {
        Set<String> lateSet = new HashSet<>();// 迟到名单
        Set<String> leftSet = new HashSet<>();// 早退名单
        Set<String> absentSet = new HashSet<>();// 缺席名单
        // 获取这一天所有人的打卡数据
        Map<User, String> record = HRService.getOneDayClockRecord(year, month, day);
        for (User user : record.keySet()) {// 遍历每一个员工
            String oneRecord = record.get(user);// 获取该员工的考勤标记
            // 如果有迟到标记，并且没有正常上班打卡标记
            if (oneRecord.contains(LATE) && !oneRecord.contains(CLOCK_START)) {
                lateSet.add(user.getName());// 添加到迟到名单
            }
            // 如果有早退标记，并且没有正常下班打卡标记
            if (oneRecord.contains(LEAVE_EARLY) && !oneRecord.contains(CLOCK_END)) {
                leftSet.add(user.getName());// 添加到早退名单
            }
            // 如果有缺席标记
            if (oneRecord.contains(ABSENT)) {
                absentSet.add(user.getName());// 添加到缺席名单
            }
        }

        StringBuilder report = new StringBuilder();// 报表字符串
        int count = Session.USERS.size();// 获取员工人数
        // 拼接报表内容
        report.append("-----  " + year + "年" + month + "月" + day + "日  -----\n");
        report.append("应到人数：" + count + "\n");

        report.append("缺席人数：" + absentSet.size() + "\n");
        report.append("缺席名单：");
        if (absentSet.isEmpty()) {// 如果缺席名单是空的
            report.append("（空）\n");
        } else {
            // 创建缺席名单的遍历对象
            Iterator<String> it = absentSet.iterator();
            while (it.hasNext()) {// 遍历名单
                // 在报表中添加缺席员工的名字
                report.append(it.next() + " ");
            }
            report.append("\n");
        }

        report.append("迟到人数：" + lateSet.size() + "\n");
        report.append("迟到名单：");
        if (lateSet.isEmpty()) {// 如果迟到名单是空的
            report.append("（空）\n");
        } else {
            // 创建迟到名单的遍历对象
            Iterator<String> it = lateSet.iterator();
            while (it.hasNext()) {// 遍历名单
                // 在报表中添加迟到员工的名字
                report.append(it.next() + " ");
            }
            report.append("\n");
        }

        report.append("早退人数：" + leftSet.size() + "\n");
        report.append("早退名单：");
        if (leftSet.isEmpty()) {// 如果早退名单是空的
            report.append("（空）\n");
        } else {
            // 创建早退名单的遍历对象
            Iterator<String> it = leftSet.iterator();
            while (it.hasNext()) {// 遍历名单
                // 在报表中添加早退员工的名字
                report.append(it.next() + " ");
            }
            report.append("\n");
        }
        return report.toString();
    }

    */


    public static void updateWorkTime(work_time time) {
        dao.updateWorkTime(time);// 更新数据库中的作息时间
        Session.worktime = time;// 更新全局会话中的作息时间
    }

    public static List<String> getAttendanceRecords(User eminformation) {
        return null;
    }
}

