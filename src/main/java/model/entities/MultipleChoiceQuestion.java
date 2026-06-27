package model.entities;

import java.util.List;

public class MultipleChoiceQuestion extends Question {
    private List<String> alternatives;

    @Override
    public String getType() {
        return "MULTIPLE_CHOICE";
    }

    public List<String> getAlternatives() { 
        return alternatives; 
    }

    public void setAlternatives(List<String> alternatives) {
         this.alternatives = alternatives; 
    }
}