package com.jiayang.jyelock.bean;

/**
 * Created by Administrator on 2017/6/15.
 */

public class Token {

    /**
     * access_token : tM3O3tD7kpb9PvxO4B94FzobMP0GT2v00Jf8EJQs
     * token_type : Bearer
     * expires_in : 2678400
     * refresh_token : XQoEKRmBqrW2TusPofO5IkDfV3LteDqnkAxtBLOZ
     */

    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
