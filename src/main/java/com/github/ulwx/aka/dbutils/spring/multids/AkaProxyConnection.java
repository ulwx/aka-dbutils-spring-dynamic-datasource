package com.github.ulwx.aka.dbutils.spring.multids;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

public class AkaProxyConnection implements Connection {
    private boolean autoCommit=true;
    private static final Logger LOGGER = LoggerFactory.getLogger(AkaProxyConnection.class);

    private LinkedHashMap<DataSource,Connection> map = new LinkedHashMap<>();

    private AkaDynamicDataSource dynamicDataSource;

    public AkaProxyConnection(AkaDynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    private   Connection getConnection()throws SQLException{
        DataSource dataSource=dynamicDataSource.determineTargetDataSource();
        Connection connection= map.get(dataSource);
        if(connection==null){
            try {
                connection = dataSource.getConnection();
                if(LOGGER.isDebugEnabled()) {
                    LOGGER.debug("fetch a new connection:" + connection+" from datasource");
                }
                connection.setAutoCommit(autoCommit);
                map.put(dataSource,connection);
            }catch (Exception e){
                dynamicDataSource.IncreaseErr();
                throw e;
            }

        }
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("return ds:" + dynamicDataSource.determineCurrentLookupKey()
                    + "," + dataSource.getClass() + ";con=" + connection);
        }
        return connection;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return this.getConnection().createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.getConnection().prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.getConnection().prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return this.getConnection().nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        this.autoCommit=autoCommit;
        LOGGER.debug("start setAutoCommit...");
        exeAll((connection) -> {
            connection.setAutoCommit(autoCommit);
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("set autoCommit=" + autoCommit + ";" + connection + "");
            }
            return true;
        });
        LOGGER.debug("end setAutoCommit...");
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.autoCommit;
    }

    private void exeAll(AkaConsumer<Connection> consumer)throws SQLException {
        List<Connection> list = new ArrayList<>();
        for(DataSource key:map.keySet()){

            list.add(map.get(key));
        }
        for(int i=list.size()-1; i>=0; i--){
            boolean ret=consumer.accept(list.get(i));
            if(!ret){
                return ;
            }

        }
    }
    @Override
    public void commit() throws SQLException {
        LOGGER.debug("start commit...");
        exeAll((connection) -> {
            connection.commit();
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("committed..." + connection + "");
            }
            return true;
        });
        LOGGER.debug("end commit...");
    }

    @Override
    public void rollback() throws SQLException {
        LOGGER.debug("start rollback...");
        exeAll((connection) -> {
            connection.rollback();
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("rollbacked..." + connection + "");
            }
            return true;
        });
        LOGGER.debug("end rollback...");

    }

    @Override
    public void close() throws SQLException {
        LOGGER.debug("start close...");
        exeAll((connection) -> {
            if(!connection.isClosed()) {
                try {
                    connection.close();
                }catch (Exception e){
                    LOGGER.error(""+e,e);
                }
            }
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug(" closed.." +this+"["+ connection + "]");
            }
            return true;
        });
        LOGGER.debug("end close...");
        map.clear();

    }

    @Override
    public boolean isClosed() throws SQLException {

        TValue<Boolean> closed=new TValue<>(true);
        exeAll((connection) -> {
            if(!connection.isClosed()){
                closed.setValue(false);
                return false;
            }
            return true;
        });
        return closed.getValue();

    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.getConnection().getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.getConnection().setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.getConnection().isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.getConnection().setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.getConnection().getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.getConnection().setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.getConnection().getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.getConnection().getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.getConnection().clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.getConnection().createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.getConnection().getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.getConnection().setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.getConnection().setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.getConnection().getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("setSavepoint..." + this.getConnection() + "");
        }
        return this.getConnection().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("setSavepoint:" + name + " " + this.getConnection() + "");
        }
        return this.getConnection().setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("rollback to savepoint..." + savepoint + " " + this.getConnection() + "");
        }
        this.getConnection().rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("releaseSavepoint to savepoint..." + savepoint + " " + this.getConnection() + "");
        }
        this.getConnection().releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.getConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.getConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return this.getConnection().prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return this.getConnection().prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return this.getConnection().prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return this.getConnection().createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return this.getConnection().createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return this.getConnection().createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.getConnection().createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return this.getConnection().isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

        try {
            this.getConnection().setClientInfo(name, value);
        } catch (Exception ex) {
            if (ex instanceof SQLClientInfoException) throw (SQLClientInfoException) ex;
            else {
                throw new RuntimeException(ex);
            }
        }


    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

            try{
                this.getConnection().setClientInfo(properties);
            }catch (Exception ex){
                if(ex instanceof SQLClientInfoException) throw (SQLClientInfoException)ex;
                else{
                    throw new RuntimeException(ex);
                }
            }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return this.getConnection().getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.getConnection().getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return this.getConnection().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return this.getConnection().createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.getConnection().setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return this.getConnection().getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.getConnection().abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.getConnection().setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.getConnection().getNetworkTimeout();
    }


    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.getConnection().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.getConnection().isWrapperFor(iface);
    }


}

