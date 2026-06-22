module geradorProvas.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires java.sql;

    opens view.screens to javafx.fxml;
    
    exports view.screens; 
}