package com.github.ulwx.aka.dbutils.spring.multids;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomGroupDecider extends GroupDecider{
    public static RandomGroupDecider instance=new RandomGroupDecider();
    @Override
    public String decide(List<String> dsList) {
        int number =ThreadLocalRandom.current().nextInt(dsList.size());
        return dsList.get(number);
    }

    @Override
    public String getType() {
        return GroupDecider.Random;
    }
}
