package net.johnewart.kensho.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SQL {
    public static void select(Connection connection,
                              String sql,
                              ResultSetProcessor processor,
                              Object... params) throws DataAccessException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int cnt = 0;
            for (Object param : params) {
                ps.setObject(++cnt, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                long rowCnt = 0;
                while (rs.next()) {
                    if (processor != null) {
                        processor.process(rs, rowCnt++);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
