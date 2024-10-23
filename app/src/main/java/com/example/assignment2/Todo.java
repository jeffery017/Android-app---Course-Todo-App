package com.example.assignment2;

import androidx.annotation.NonNull;
import java.io.Serializable;
import java.util.Date;
import com.google.gson.Gson;


public class Todo implements Serializable {
    private final String courseId;
    private final String title;
    private final String description;
    private final Date updateAt;
    private static final String TAG = "Todo";

    Todo(String courseId, String title, String description, Date updateAt) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.updateAt = updateAt;
    }


    public String getCourseId() {return courseId;}
    public String getDescription() {return description;}
    public String getTitle() {return title;}
    public Date getUpdateAt() {return updateAt;}

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
