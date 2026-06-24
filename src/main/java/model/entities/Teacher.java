package model.entities;

public class Teacher {
    private User user;
    private String registration_number;

    public String getResgistration_number() {
        return registration_number;
    }

    public void setResgistration_number(String resgistration_number) {
        this.registration_number = resgistration_number;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

}
