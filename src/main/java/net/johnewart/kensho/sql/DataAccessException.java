package net.johnewart.kensho.sql;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: jewart
 * Date: 10/23/14
 * Time: 8:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataAccessException extends Throwable {
    public DataAccessException(SQLException e) {
    }
}
