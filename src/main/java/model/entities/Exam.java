package model.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Exam {

    private int examId;
    private Subject subject;
    private List<Question> questions = new ArrayList<>();
    private LocalDate creationDate;
    private String semester;
    private Teacher teacher;

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }


    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestao(Question questao) {
        if (questao != null && !questions.contains(questao)) {
            this.questions.add(questao);
        }
    }

    public void removerQuestao(Question questao) {
        this.questions.remove(questao);
    }
}