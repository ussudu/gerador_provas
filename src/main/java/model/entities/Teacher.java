package model.entities;

public class Teacher {
    private User user;
    private String registration_number;

    public Teacher(){}
    public Teacher(User user, String registration_number)
    {
        this.user = user;
        this.registration_number = registration_number;
    }
    public String getRegistration_number() {
        return registration_number;
    }
    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

}
