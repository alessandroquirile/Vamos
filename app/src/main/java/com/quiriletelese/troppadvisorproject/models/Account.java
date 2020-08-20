package com.quiriletelese.troppadvisorproject.models;

import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class Account {
    private String name;
    private String lastname;
    private String username;
    private String email;
    private String password;

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Account(String name, String lastname, String username, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @NotNull
    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
