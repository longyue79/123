package sys.dao;

import sys.message.Ask_for_leave;
import sys.message.clock_record;
import sys.people.Admin;
import sys.people.User;
import sys.message.work_time;
import sys.people.UserLogin;
import sys.tool.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.alibaba.excel.EasyExcel;


public class DAOMysql implements DAO {
    Connection con = null;
    Statement st = null;
    PreparedStatement ps = null;
    ResultSet rs = null;


    @Override
    public Set<User> getAllUsers() {
        Set<User> eminformations = new HashSet<>();
        String sql = "select * from em_information";
        con = JDBCSet.getConnection();
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String sex = rs.getString("sex");
                Sex sex1 = Sex.fromSex(sex);
                String phoneNumber = rs.getString("phone_number");
                String mail = rs.getString("mail");
                String code = rs.getString("code");
                User eminformation = new User(id, name, sex1, phoneNumber, mail, code);
                eminformations.add(eminformation);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            JDBCSet.close(st, ps, rs);
        }
        return eminformations;
    }

    @Override
    public User getUser(int id) {
        String sql = "select name,sex,phone_number,mail,code from em_information where id = ?";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String sex = rs.getString("sex");
                Sex sex1 = Sex.fromSex(sex);
                String phoneNumber = rs.getString("phone_number");
                String mail = rs.getString("mail");
                String code = rs.getString("code");
                User eminformation = new User(id, name, sex1, phoneNumber, mail, code);
                return eminformation;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCSet.close(st, ps, rs);
        }
        return null;
    }

    @Override
    public User getUser(Email email) {
        String sql = "select id,name,sex,phone_number,code from em_information where mail = ?";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, email.getAddress());
            rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String sex = rs.getString("sex");
                Sex sex1 = Sex.fromSex(sex);
                String phoneNumber = rs.getString("phone_number");
                String code = rs.getString("code");
                User eminformation = new User(id, name, sex1, phoneNumber, email.getAddress(), code);
                return eminformation;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCSet.close(st, ps, rs);
        }
        return null;
    }

    @Override
    public User getUser(PhoneNumber phone) {
        String sql = "select id,name,sex,mail,code from em_information where phone_number = ?";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, phone.getNumber());
            rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String sex = rs.getString("sex");
                Sex sex1 = Sex.fromSex(sex);
                String mail = rs.getString("mail");
                String code = rs.getString("code");
                User eminformation = new User(id, name, sex1, phone.getNumber(), mail, code);
                return eminformation;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCSet.close(st, ps, rs);
        }
        return null;
    }

    @Override
    public User getUser(String code) {
        String sql = "select id,name,sex,phone_number,mail from em_information where code = ?";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, code);
            rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String sex = rs.getString("sex");
                Sex sex1 = Sex.fromSex(sex);
                String phoneNumber = rs.getString("phone_number");
                String mail = rs.getString("mail");
                User eminformation = new User(id, name, sex1, phoneNumber, mail, code);
                return eminformation;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCSet.close(st, ps, rs);
        }
        return null;
    }

    @Override
    public void addUser(User eminformation) {
        String sql = "insert into em_information(name,sex,phone_number,mail,code) values(?,?,?,?,?)";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, eminformation.getName());
            ps.setString(2, eminformation.getSex().getSex());
            ps.setString(3, eminformation.getPhone());
            ps.setString(4, eminformation.getMail());
            ps.setString(5, eminformation.getCode());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCSet.close(st, ps, rs);
        }
    }

//暂时有错误
    @Override
    public void updateUser(int id, Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("UPDATE em_information SET ");
        List<Object> values = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            if (index > 0) {
                sql.append(", ");
            }
            sql.append(entry.getKey()).append(" = ?");
            values.add(entry.getValue());
            index++;
        }

        sql.append(" WHERE id = ?");
        values.add(id);

        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql.toString());
            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 1, values.get(i));
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCSet.close(st, ps, rs);
        }
    }

    @Override
    public void deleteUser(int id) {
        String sql = "delete from em_information where id = ?";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCSet.close(st, ps, rs);
        }
    }

    @Override
    public work_time getWorkTime() {
        // 修改 SQL 查询语句，使用非保留关键字的字段名
        String sql = "select start, end, a_start, a_end from work_time";
        try {
            con = JDBCSet.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(sql);
            if (rs.next()) {
                // 获取数据库中的时间字符串
                String start = rs.getString("start");
                String end = rs.getString("end");
                String a_start = rs.getString("a_start");
                String a_end = rs.getString("a_end");


                return new work_time(start, end, a_start, a_end);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            JDBCSet.close(st, null, rs);
        }
        return null;
    }

    public void updateWorkTime(work_time worktime) {
        String sql = "update work_time set start = ?, end = ? , a_start = ?, a_end = ?";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, worktime.getMorningStartTime());
            ps.setString(2, worktime.getMorningEndTime());
            ps.setString(3, worktime.getAfternoonStartTime());
            ps.setString(4, worktime.getAfternoonEndTime());
            ps.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            JDBCSet.close(st, ps, rs);// 关闭数据库接口对象
        }
    }

//11111
    @Override
    public void setWorkTime(work_time worktime) {
        StringBuilder sql = new StringBuilder("UPDATE work_time SET ");
        boolean isFirst = true;
        int paramIndex = 1;

        if (worktime.getMorningStartTime() != null) {
            if (!isFirst) {
                sql.append(", ");
            }
            sql.append("morning_start_time = ?");
            isFirst = false;
        }
        if (worktime.getMorningEndTime() != null) {
            if (!isFirst) {
                sql.append(", ");
            }
            sql.append("morning_end_time = ?");
            isFirst = false;
        }
        if (worktime.getAfternoonStartTime() != null) {
            if (!isFirst) {
                sql.append(", ");
            }
            sql.append("afternoon_start_time = ?");
            isFirst = false;
        }
        if (worktime.getAfternoonEndTime() != null) {
            if (!isFirst) {
                sql.append(", ");
            }
            sql.append("afternoon_end_time = ?");
        }

        if (isFirst) {
            return;
        }

        sql.append(" WHERE id = 1");

        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql.toString());
            if (worktime.getMorningStartTime() != null) {
                ps.setString(paramIndex++, worktime.getMorningStartTime().toString());
            }
            if (worktime.getMorningEndTime() != null) {
                ps.setString(paramIndex++, worktime.getMorningEndTime().toString());
            }
            if (worktime.getAfternoonStartTime() != null) {
                ps.setString(paramIndex++, worktime.getAfternoonStartTime().toString());
            }
            if (worktime.getAfternoonEndTime() != null) {
                ps.setString(paramIndex, worktime.getAfternoonEndTime().toString());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCSet.close(st, ps, rs);
        }
    }

    //44444
    @Override
    public void addClockRecord(int id, LocalDateTime now, boolean isLate, boolean isLeave) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = now.format(formatter);
        String sql = "INSERT INTO clock_record (em_id, clock_time, is_late, is_leave) VALUES (?,?,?,?)";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, formattedTime);
            ps.setBoolean(3, isLate);
            ps.setBoolean(4, isLeave);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCSet.close(st, ps, rs);
        }
    }

    //55555
    @Override
    public void deleteClockRecord(int id) {
        String sql = "DELETE FROM clock_record WHERE em_id = ?";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCSet.close(st, ps, rs);
        }
    }

    //66666
    @Override
    public List<clock_record> getAllClockRecords() {
        List<clock_record> records = new ArrayList<>();
        String sql = "SELECT clock_record.em_id, clock_record.clock_time, clock_record.is_late, clock_record.is_leave, em_information.name " +
                "FROM clock_record INNER JOIN em_information ON clock_record.em_id = em_information.id";

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = JDBCSet.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                int em_id = rs.getInt("em_id");
                String name = rs.getString("name");
                String clockTime = rs.getString("clock_time");
                boolean isLate = rs.getBoolean("is_late");
                boolean isLeave = rs.getBoolean("is_leave");

                // 使用 DateTimeSet 处理日期时间转换
                clock_record record = new clock_record(
                        em_id,
                        name,
                        DateTimeSet.formatDateTime(DateTimeSet.dateOf(clockTime)),
                        isLate,
                        isLeave
                );

                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCSet.close(st, null, rs);
        }

        return records;
    }

    public List<clock_record> getOneDayClockRecord(int id, int year, int month, int day) {
        List<clock_record> dayRecord = new ArrayList<>();

        return dayRecord;
    }

    @Override
    public List<Ask_for_leave> getAllAskForLeave() {
        List<Ask_for_leave> leaves = new ArrayList<>();
        String sql = "select * from ask_for_leave ";
        con = JDBCSet.getConnection();
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("leave_em_id");
                String name = rs.getString("name");
                String startTime = rs.getString("start");
                String endTime = rs.getString("end");
                Ask_for_leave leave = new Ask_for_leave(id, name, DateTimeSet.dateOf(startTime).toString(),
                        DateTimeSet.dateOf(endTime).toString());
                leaves.add(leave);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCSet.close(st, ps, rs);
        }
        return leaves;
    }

    @Override
    public void addLeave(Ask_for_leave leave) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "insert into ask_for_leave(leave_em_id, name, start, end) values(?,?,?,?)";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, leave.getId());
            ps.setString(2, leave.getName());
            ps.setString(3, sdf.format(leave.getStart_time()));
            ps.setString(4, sdf.format(leave.getEnd_time()));
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCSet.close(st, ps, rs);
        }
    }

    @Override
    public void deleteLeave(Ask_for_leave leave){
        String sql = "delete from ask_for_leave where leave_em_id = ?";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, leave.getId());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCSet.close(st, ps, rs);
        }
    }

    //77777
    @Override
    public void ExportForExcelMessage() {
        String sql = "select * from em_information ";
        con = JDBCSet.getConnection();
        List<User> eminformations = new ArrayList<>();
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String sex = rs.getString("sex");
                Sex sex1 = Sex.fromSex(sex);
                String phoneNumber = rs.getString("phone_number");
                String mail = rs.getString("mail");
                String code = rs.getString("code");
                User eminformation = new User(id, name, sex1, phoneNumber, mail, code);
                eminformations.add(eminformation);
            }
            // 导出到 Excel
            EasyExcel.write("e:/FacesSystem/resources/excel/em_information.xlsx", User.class).sheet("员工信息").doWrite(eminformations);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCSet.close(st, ps, rs);
        }
    }

    public void ExportForExcelClockRecord(){
        String sql = "SELECT clock_record.em_id, clock_record.clock_time, clock_record.is_late, clock_record.is_leave, em_information.name FROM clock_record INNER JOIN em_information ON clock_record.em_id = em_information.id";
        con = JDBCSet.getConnection();
        List<clock_record> clockrecords = new ArrayList<>();
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                int em_id = rs.getInt("em_id");
                String name = rs.getString("name");
                String clockTime = rs.getString("clock_time");
                boolean isLate = rs.getBoolean("is_late");
                boolean isLeave = rs.getBoolean("is_leave");
                clock_record record = new clock_record(em_id, name, DateTimeSet.dateOf(clockTime).toString(), isLate, isLeave);
                clockrecords.add(record);
            }
            // 导出到 Excel
            EasyExcel.write("e:/FacesSystem/resources/excel/em_information.xlsx", clock_record.class).sheet("打卡信息").doWrite(clockrecords);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCSet.close(st, ps, rs);
        }
    }

    //88888
    @Override
    public boolean AdminLogin(Admin admin) {
        String sql = "select * from admin where username = ? and password = ?";
        con = JDBCSet.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCSet.close(st, ps, rs);
        }
        return false;
    }

    public boolean UserLogin(UserLogin UserLogin) {
        String sql = "select * from em where username = ? and password = ?";
        con = JDBCSet.getConnection();
        try{
            ps = con.prepareStatement(sql);
            ps.setString(1, UserLogin.getUsername());
            ps.setString(2, UserLogin.getPassword());
            rs = ps.executeQuery();
            return rs.next();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCSet.close(st, ps, rs);
        }
        return false;
    }
}
