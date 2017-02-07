package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import songlib.Controller;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

public class SongLib extends Application {
	
	public Controller controller;
	
	@Override
	public void start(Stage primaryStage) {
		AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getClassLoader().getResource("songlib/SongLib.fxml"));
			root = (AnchorPane) loader.load(); 
		} catch(Exception e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
