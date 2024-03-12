package com.driver;

import java.util.Objects;

public class User {
    private String name;
    private String mobile;
    public User(String name, String mobile){
        this.name=name;
        this.mobile=mobile;
    }
    public String getName(){
        return this.name;
    }
    public String getMobile(){
        return this.mobile;
    }

    public boolean equals(User user) {
        if (this == user) return true;
        if (user == null || getClass() != user.getClass()) return false;
        return Objects.equals(name, user.name) && Objects.equals(mobile, user.mobile);
    }
}
