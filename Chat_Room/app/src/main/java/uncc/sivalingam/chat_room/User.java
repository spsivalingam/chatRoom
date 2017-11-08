package uncc.sivalingam.chat_room;

import java.io.Serializable;

/**
 * Created by sivalingam on 11/7/2017.
 */

public class User implements Serializable {

    private String fName;
    private String lName;
    private String email;
    private long user_id;
    private String token;

    public User(){

    }

    public User(String fName, String lName,long user_id) {
        this.fName = fName;
        this.lName = lName;
        this.user_id=user_id;
    }

    public User(String fName, String lName, String email, long user_id, String token) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.user_id = user_id;
        this.token = token;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
