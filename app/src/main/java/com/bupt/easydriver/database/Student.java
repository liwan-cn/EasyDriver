package com.bupt.easydriver.database;

import org.litepal.crud.DataSupport;

/**
 * Created by Shinelon on 2017/7/17.
 */

public class Student extends DataSupport{
    private String sid;
    private String name;
    private String sex;
    public String getId(){
        return sid;
    }
    public void setId(String sid){
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
