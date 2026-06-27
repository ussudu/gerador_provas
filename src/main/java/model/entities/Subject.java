package model.entities;

public class Subject {
    
    private int idSubject;
    private String name;
    private String topics;
    private Teacher teacher; 
    private String code;

    public int getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getTopics() {
        return topics;
    }
    public void setTopics(String topics) {
        this.topics = topics;
    }
}
