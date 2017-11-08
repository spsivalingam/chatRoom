package uncc.sivalingam.chat_room;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sivalingam on 11/7/2017.
 */

public class ThreadMsg implements Serializable {
    private long thread_id;
    private String topic;
    private User createdUser;
    private String creationStamp;

    public ThreadMsg(long thread_id, String topic, User createdUser, String creationStamp) {
        this.thread_id = thread_id;
        this.topic = topic;
        this.createdUser = createdUser;
        this.creationStamp = creationStamp;
    }

    public long getThread_id() {
        return thread_id;
    }

    public void setThread_id(long thread_id) {
        this.thread_id = thread_id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public String getCreationStamp() {
        return creationStamp;
    }

    public void setCreationStamp(String creationStamp) {
        this.creationStamp = creationStamp;
    }
}
