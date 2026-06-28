package model.entities;

public class User {
    private int idUser;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private boolean status;

    public User() {
    }
    public User (String name, String email, String password, UserRole role )
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = true;
    }

    public int getIdUser() {
        return idUser;
    }
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
    public boolean getStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    
}
