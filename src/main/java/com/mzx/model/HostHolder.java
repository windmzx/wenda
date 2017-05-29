package com.mzx.model;

import org.springframework.stereotype.Component;

/**
 * Created by mengz on 2017/5/19.
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public static User getUser() {
        return users.get();
    }

    public static void setUser(User user) {
        users.set(user);
    }
    public static void clear(){
        users.remove();
    }
}
