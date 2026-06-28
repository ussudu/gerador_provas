module geradorProvas.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires java.sql; 

    opens presenter to javafx.graphics, javafx.fxml;
    exports presenter;

    opens presenter.controllers to javafx.fxml;
    exports presenter.controllers;
}