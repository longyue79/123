package sys.dao;

public class DAOFactory {
    public static DAO getDAO() {
        return new DAOMysql();
    }
}
