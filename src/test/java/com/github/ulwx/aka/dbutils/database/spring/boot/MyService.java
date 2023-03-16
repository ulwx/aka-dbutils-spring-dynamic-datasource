package com.github.ulwx.aka.dbutils.database.spring.boot;

import com.github.ulwx.aka.dbutils.spring.multids.AkaDS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyService.class);
    private AddressDao addressDao;

    public void init(){
        addressDao.init();
    }
    public MyService(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @AkaDS("dataSource1")
    public void updateMdb(){
        addressDao.updateMd1(1,"123");
        try {
            addressDao.updateMd2(2, "abc");
        }catch (Exception ex){
            LOGGER.debug(ex+"");
        }
        int i=0;


    }



}
