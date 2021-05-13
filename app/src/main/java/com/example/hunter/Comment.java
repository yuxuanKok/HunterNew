package com.example.hunter;

import java.util.Date;
import java.util.List;

/**
 * Created by User on 8/22/2017.
 */

public class Comment {

    private String comment;
    private String user_id, commentid;
    private Date date_created;

    public Comment() {
    }

    public Comment(String commentid, String comment, String user_id, Date date_created) {
        this.commentid=commentid;
        this.comment = comment;
        this.user_id = user_id;
        this.date_created = date_created;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }
}
