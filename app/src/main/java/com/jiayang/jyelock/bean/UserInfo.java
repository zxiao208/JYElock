package com.jiayang.jyelock.bean;

/**
 * Created by Administrator on 2017/6/13.
 */

public class UserInfo {

    /**
     * id : 7050
     * username : 18622358828
     */

    private int id;
    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
