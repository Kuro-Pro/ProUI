package com.kuro.proui.cascade;

import java.util.List;

/**
 * @author Kuro
 * @date 2018/5/1
 */
public interface CascadeBean {

    int PRIMARY = 0;
    int MINOR = 1;
    int LOWEST = 2;

    int getLevel();

    String getItemName();

    List<? extends CascadeBean> next();

}
