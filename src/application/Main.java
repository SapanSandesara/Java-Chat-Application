package application;

import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Main extends Application {
	private ServicedServer ss;
	ObservableList<Connected_Client> clientlist = FXCollections.observableArrayList();

	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Server Application");
			Text text = new Text("Please start a server");
			Button button1 = new Button("Start Server");

			int port = 9094;

			button1.setOnAction(value -> {

				try {
					// new ThreadedDateServer();

					// Thread.sleep(10000);

					this.ss = new ServicedServer(port, text, clientlist);
					this.ss.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			Button button2 = new Button("Stop server");
			button2.setOnAction(value -> {
				if (this.ss != null) {

					this.ss.getServer().close();
					text.setText("Server stopped");

				} else {
					text.setText("Please start a server first!");
				}

			});

			Label label = new Label("Connected clients:");
			ListView<Connected_Client> connectedclients = new ListView<Connected_Client>(clientlist);
			VBox vb = new VBox();

			HBox hbox = new HBox(button1, button2);
			vb.getChildren().addAll(hbox, text, label, connectedclients);

			Scene scene = new Scene(vb, 500, 500);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
