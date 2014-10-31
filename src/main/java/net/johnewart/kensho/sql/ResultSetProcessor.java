package net.johnewart.kensho.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetProcessor {

    public void process(ResultSet resultSet,
                        long currentRow)
            throws SQLException;

}