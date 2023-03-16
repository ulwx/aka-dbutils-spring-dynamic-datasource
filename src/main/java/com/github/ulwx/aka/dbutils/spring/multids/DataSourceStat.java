package com.github.ulwx.aka.dbutils.spring.multids;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicInteger;

public class DataSourceStat {
    private DataSource dataSource;
    private AtomicInteger errCnt=new AtomicInteger(0);
    private long lastErrMillSec=0;

    public DataSourceStat(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public AtomicInteger getErrCnt() {
        return errCnt;
    }

    public void setErrCnt(AtomicInteger errCnt) {
        this.errCnt = errCnt;
    }

    public int inc(){
        this.lastErrMillSec=System.currentTimeMillis();
        return errCnt.incrementAndGet();
    }
}
