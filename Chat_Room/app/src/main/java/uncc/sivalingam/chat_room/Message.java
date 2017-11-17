package uncc.sivalingam.chat_room;

import java.io.Serializable;

/**
 * Created by sivalingam on 11/12/2017.
 */

public class Message implements Serializable{

    private User user;
    private String fname;
    private long thread_id;
    private String msg;
    private String created_At;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public long getThread_id() {
        return thread_id;
    }

    public void setThread_id(long thread_id) {
        this.thread_id = thread_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCreated_At() {
        return created_At;
    }

    public void setCreated_At(String created_At) {
        this.created_At = created_At;
    }
}
