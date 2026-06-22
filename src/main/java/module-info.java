module geradorProvas.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires java.sql;

    opens view to javafx.fxml;
    
    exports view; 
}