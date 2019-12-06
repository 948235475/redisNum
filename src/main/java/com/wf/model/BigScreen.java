package com.wf.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class BigScreen {

    @Id
    private String id;

    private String username;

    private String screenName;

    @Override
    public String toString() {
        return "BigScreen{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }
}
