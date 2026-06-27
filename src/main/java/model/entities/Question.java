package model.entities;

public abstract class Question {
    private int questionId;
    private String statement;
    private String answerKey;
    private Subject subject; 
    private String topic;
    private Difficulty difficulty;
    private Teacher teacher;

    public abstract String getType();

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getAnswerKey() {
        return answerKey;
    }

    public void setAnswerKey(String answerKey) {
        this.answerKey = answerKey;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}