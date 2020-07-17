package com.quiriletelese.troppadvisorproject.models;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class Account {
    private String name;
    private String lastname;
    private String nickname;
    private String email;
    private String password;


    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Account(String name, String lastname, String nickname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
