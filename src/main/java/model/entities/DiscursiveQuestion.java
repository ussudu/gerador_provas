package model.entities;
public class DiscursiveQuestion extends Question {
    private int expectedLines;

    @Override
    public String getType() {
        return "DISCURSIVE";
    }

    public int getExpectedLines() { 
        return expectedLines; 
    }
    public void setExpectedLines(int expectedLines) {
        this.expectedLines = expectedLines; 
    }
}