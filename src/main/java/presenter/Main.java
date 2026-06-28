package presenter;

import javafx.application.Application;
import javafx.stage.Stage;
import presenter.utils.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        SceneManager.getInstance().setPrimaryStage(primaryStage);
        
        primaryStage.setResizable(false);
    
        SceneManager.getInstance().navigateTo("/view/login.fxml", "Sistema de Provas - Entrar");
    }

    public static void main(String[] args) {

        launch(args);
    }
}