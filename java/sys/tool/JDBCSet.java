package sys.tool;

import javax.naming.ConfigurationException;
import java.io.*;
import java.sql.*;
import java.util.Properties;

public class JDBCSet {
    private static String driver_name;
    private static String username;
    private static String password;
    private static String url;
    private static Connection connection = null;

    static {
        Properties properties = new Properties();
        try{
            InputStream fis = JDBCSet.class.getClassLoader().getResourceAsStream("jdbc.properties");
            properties.load(fis);
            driver_name = properties.getProperty("driver_name");
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            url = properties.getProperty("url");
            if(driver_name == null || url == null || username == null || password == null){
                throw new ConfigurationException("配置文件信息缺失");
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (ConfigurationException e){
            System.err.println("配置文件获取的内容为:[driver_name:" + driver_name + ",url:" + url + ",username:" + username + ",password:" + password + "]");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if(connection == null || connection.isClosed()){
                Class.forName(driver_name);
                connection = DriverManager.getConnection(url, username, password);
            }
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }

    public static void close(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(Statement st) {
        try {
            if (st != null)
                st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close(PreparedStatement ps) {
        try {
            if (ps != null)
                ps.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close(Statement st,PreparedStatement ps, ResultSet rs) {
        close(st);
        close(rs);
        close(ps);
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
