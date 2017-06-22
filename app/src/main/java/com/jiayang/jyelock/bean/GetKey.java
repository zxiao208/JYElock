package com.jiayang.jyelock.bean;

/**
 * Created by Administrator on 2017/6/15.
 */

public class GetKey {

    /**
     * lock_id : -1
     * password_amount : 1
     */

    private String lock_id;
    private int password_amount;

    public String getLock_id() {
        return lock_id;
    }

    public void setLock_id(String lock_id) {
        this.lock_id = lock_id;
    }

    public int getPassword_amount() {
        return password_amount;
    }

    public void setPassword_amount(int password_amount) {
        this.password_amount = password_amount;
    }
}
