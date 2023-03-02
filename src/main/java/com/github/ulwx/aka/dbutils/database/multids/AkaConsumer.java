package com.github.ulwx.aka.dbutils.database.multids;

import java.sql.SQLException;

@FunctionalInterface
public interface AkaConsumer<T> {

    boolean accept(T t) throws SQLException;
}
