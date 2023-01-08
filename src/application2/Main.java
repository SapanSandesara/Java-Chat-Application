package application2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
//	String serveraddress= "localhost";
//	int port = 9093;

	SSLSocket clientsocket;
	BufferedReader input;
	java.io.PrintWriter output;
	Text text = new Text("Waiting");
	String user;
	ObservableList<message> messagelist = FXCollections.observableArrayList();
	ObservableList<message> unreadmessagelist = FXCollections.observableArrayList();
	Key key;

	@Override
	public void start(Stage PrimaryStage) throws Exception {
		// TODO Auto-generated method stub

		HBox hb = new HBox();
		Label addresslabel = new Label("Server address:");
		TextField serveraddress = new TextField();
		serveraddress.setText("localhost");
		serveraddress.setPromptText("server address");
		hb.getChildren().addAll(addresslabel, serveraddress);
		HBox hb1 = new HBox();
		Label portlabel = new Label("Port number:");
		TextField port = new TextField();
		port.setText("9094");
		port.setPromptText("Enter port number");
		hb1.getChildren().addAll(portlabel, port);

		Button submit = new Button("submit");

		submit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int portt = Integer.parseInt(port.getText());
				try {

					System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
					// System.setProperty("javax.net.ssl.trustStoreType", "JTS");
					System.setProperty("javax.net.ssl.trustStore", "myTrustStore.jts");
					System.setProperty("javax.net.ssl.trustStorePassword", "cloudandsecurityproject");
					// System.setProperty("trustServerCertificate", "true");
					// System.setProperty("javax.net.ssl.trustStore",
					// System.getProperty("java.home") + "/lib/security/cacerts");

					// System.setProperty("javax.net.ssl.trustStorePassword","changeit");
					SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					// clientsocket = new Socket(serveraddress.getText(),portt);
					clientsocket = (SSLSocket) sslsocketfactory.createSocket(serveraddress.getText(), portt);
					input = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));

					output = new java.io.PrintWriter(clientsocket.getOutputStream(), true);
					text.setText("Connected");
					login(PrimaryStage);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					text.setText("Invalid address");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					text.setText("Something went wrong");
				}

			}

		});

		VBox vb = new VBox();
		vb.getChildren().addAll(new Label("Welcome to chat app"), hb, hb1, submit, text);

		Scene setupscene = new Scene(vb, 1000, 1000);
		PrimaryStage.setScene(setupscene);
		PrimaryStage.show();

//		PrimaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//
//			@Override
//			public void handle(WindowEvent arg0) {
//				// TODO Auto-generated method stub
//				try {
//					clientsocket.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//		});

	}

	public void login(Stage PrimaryStage) {
		VBox vb = new VBox();
		HBox hb = new HBox();
		Label label = new Label("Username:");
		TextField username = new TextField();
		username.setPromptText("Enter username");
		hb.getChildren().addAll(label, username);
		HBox hb1 = new HBox();
		Label label2 = new Label("Password:");
		PasswordField password = new PasswordField();
		password.setPromptText("Enter password");
		hb1.getChildren().addAll(label2, password);

		Button submit = new Button("submit");
		Button signup = new Button("signup");
		signup.setOnAction(e -> signup(PrimaryStage));
		submit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (username.getText().trim().isEmpty()) {
					text.setText("Enter a valid username");
					return;
				}
				if (password.getText().trim().isEmpty()) {
					text.setText("Enter a valid password");
					return;
				}
				
				output.println("connection log in");
				output.println(username.getText());
				output.println(password.getText());

				try {

					String a = input.readLine();
					text.setText(a);

					if (a.equals("Login successful")) {
						user = username.getText();
						byte[] keybyte = new byte[8];
						clientsocket.getInputStream().read(keybyte);
						
						//Apparently you cannot send a key directly from the output stream. Why is everything done in networks with extra steps
						
						DESKeySpec keySpec = new DESKeySpec(keybyte); 
						SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); 
						key = keyFactory.generateSecret(keySpec);
//						new ServicedClient(clientsocket, messagelist,input,unreadmessagelist,text,this).start();
//					output.println("retrieve messages");

						homepage(PrimaryStage);

					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

		vb.getChildren().addAll(hb, hb1, submit, signup, text);

		Scene loginscene = new Scene(vb, 1000, 1000);
		PrimaryStage.setScene(loginscene);
		PrimaryStage.show();

	}

	public void signup(Stage PrimaryStage) {
		
		TextField username = new TextField();
		username.setPromptText("Enter username");
		HBox hb = new HBox(new Label("Enter username: "), username);
		TextField firstname = new TextField();
		firstname.setPromptText("Enter first name");
		HBox hb1 = new HBox(new Label("Enter first name: "), firstname);
		TextField lastname = new TextField();
		lastname.setPromptText("Enter last name");
		HBox hb2 = new HBox(new Label("Enter last name: "), lastname);
		PasswordField password = new PasswordField();
		password.setPromptText("Enter password");
		HBox hb3 = new HBox(new Label("Enter password: "), password);
		Button submit = new Button("submit");
		submit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (username.getText().trim().isEmpty()|| username.getText().length()>20) {
					text.setText("Enter a valid username");
					return;
				}
				if (firstname.getText().trim().isEmpty()|| firstname.getText().length()>100) {
					text.setText("Enter a valid first name");
					return;
				}
				if (lastname.getText().trim().isEmpty()) {
					text.setText("Enter a valid last name");
					return;
				}
				if (password.getText().trim().isEmpty()) {
					text.setText("Enter a valid password");
					return;
				}
				output.println("sign up");
				output.println(username.getText());
				output.println(firstname.getText());
				output.println(lastname.getText());
				output.println(password.getText());

				try {

					String temp = input.readLine();
					if (temp.equals("signup success")) {
						text.setText("Signup success");
						login(PrimaryStage);

					} else {
						text.setText("username taken");

					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		VBox vb = new VBox();
		vb.getChildren().addAll(hb, hb1, hb2, hb3, submit, text);

		Scene signup = new Scene(vb, 1000, 1000);
		PrimaryStage.setScene(signup);
		PrimaryStage.show();

	}

	public void homepage(Stage PrimaryStage) {

		new ServicedClient(clientsocket, messagelist, input, unreadmessagelist, text, this, PrimaryStage).start();
		output.println("retrieve messages");

		ListView<message> messages = new ListView<message>(messagelist);
		ListView<message> unreadmessages = new ListView<message>(unreadmessagelist);

		// ServicedClient sc = new ServicedClient(clientsocket,messagelist);
		// output.println("retrieve messages");
		TextField recipient = new TextField();
		recipient.setPromptText("Enter recipient");
		TextField messageText = new TextField();
		messageText.setPromptText("Input message to send");
		messageText.setMinHeight(200);
		messageText.setMinWidth(200);
		Button sendButton = new Button();
		sendButton.setText("Send");

		sendButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub

				try {
					Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
					cipher.init(Cipher.ENCRYPT_MODE, key);
					byte[] encryptedMessage = cipher.doFinal(messageText.getText().getBytes());
					output.println("Send message");
					output.println(encryptedMessage.length);
					output.println(user);
					output.println(recipient.getText());
					// output.println(messageText.getText());
					clientsocket.getOutputStream().write(encryptedMessage);
					clientsocket.getOutputStream().flush();
					recipient.clear();
					messageText.clear();
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

//				
//				try {
//					input = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
//					
//					if(input.readLine().equals("no such user")) {
//						text.setText("Please enter a valid recipient");
//					}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

			}

		});
		Label label2 = new Label("Welcome " + user);
		Font font = new Font(25);
		label2.setFont(font);

		unreadmessages.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				message message = unreadmessages.getSelectionModel().getSelectedItem();
				if (message != null) {
					messagelist.add(message);
					unreadmessagelist.remove(message);
				}
			}

		});

		HBox messageBox = new HBox(10);
		messageBox.getChildren().addAll(recipient, messageText, sendButton);
		Label label = new Label("Unread messages:");
		HBox messagelists = new HBox();
		messagelists.getChildren().addAll(label, unreadmessages, new Label("       "), new Label("Read messages:"),
				messages);
		VBox vb = new VBox();
		vb.getChildren().addAll(label2, new Label(""), messagelists, messageBox);

		Scene homepage = new Scene(vb, 1000, 1000);
		PrimaryStage.setScene(homepage);
		PrimaryStage.show();
	}

	public void popup() {
		Stage stage = new Stage();
		VBox vb = new VBox();
		vb.getChildren().add(text);
		Scene popup = new Scene(vb, 500, 100);
		stage.setScene(popup);
		stage.show();
	}

	public void closepopup() {
		Stage stage = new Stage();
		VBox vb = new VBox();
		Label label = new Label("Server closed!");
		vb.getChildren().add(label);
		Scene popup = new Scene(vb, 500, 100);
		stage.setScene(popup);
		stage.show();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

}
