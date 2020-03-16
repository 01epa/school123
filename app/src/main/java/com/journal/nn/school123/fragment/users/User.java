package com.journal.nn.school123.fragment.users;

import android.support.annotation.NonNull;

public class User {
    @NonNull
    private final String id;
    @NonNull
    private final String username;
    @NonNull
    private final String password;
    @NonNull
    private final String school;

    public User(@NonNull String id,
                @NonNull String username,
                @NonNull String password,
                @NonNull String school) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.school = school;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSchool() {
        return school;
    }
}
