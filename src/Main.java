import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Controller CnV = new Controller();
        CnV.setView();
        Scene scene = new Scene(CnV.root, 1000, 600);
        stage.setScene(scene);
        stage.setMaxWidth(1200);
        stage.setMaxHeight(800);
        stage.setMinWidth(800);
        stage.setMinHeight(400);
        stage.show();
        CnV.setResizable(stage);
    }
}
