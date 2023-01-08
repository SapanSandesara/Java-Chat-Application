package application2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.net.ssl.SSLSocket;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Clientthread extends Thread {
	SSLSocket socket;
	ObservableList<message> messagelist;
	BufferedReader input;
	ObservableList<message> unreadmessagelist;
	Text text;
	Main main;
	Stage ps;

	public Clientthread(SSLSocket socket, ObservableList<message> messagelist, BufferedReader input,
			ObservableList<message> unreadmessagelist, Text text, Main main, Stage ps) {
		this.socket = socket;
		this.messagelist = messagelist;
		this.input = input;
		this.unreadmessagelist = unreadmessagelist;
		this.text = text;
		this.main = main;
		this.ps = ps;

	}

	public void run() {

		while (true) {

			try {

				String command = input.readLine();

				if (command == null) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							Stage stage = new Stage();
							try {

								main.start(stage);
								main.closepopup();
								ps.close();

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					});
					return;
				}

				if (command.equals("new message")) {
					Key key;
					int size = Integer.parseInt(input.readLine());
					byte[] message = new byte[size];
					byte[] decryptedmessage = new byte[size];

					System.out.println("HERE!");
					String date = input.readLine();
					String sender = input.readLine();

					// String message = input.readLine();
					byte[] keybyte = new byte[8];
					socket.getInputStream().read(keybyte);
					DESKeySpec keySpec = new DESKeySpec(keybyte); // create a DESKeySpec object
					SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); // create a SecretKeyFactory
																						// object
					key = keyFactory.generateSecret(keySpec);

					socket.getInputStream().read(message);
					Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
					cipher.init(Cipher.DECRYPT_MODE, key);
					decryptedmessage = cipher.doFinal(message);
					String textmessage = new String(decryptedmessage, StandardCharsets.UTF_8);

					String code = "1";
					message messageobj = new message(code, date, sender, textmessage);
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							unreadmessagelist.add(messageobj);
						}

					});

				} else if (command.equals("retrieving messages")) {
					int size = Integer.parseInt(input.readLine());

//						while(!(input.readLine().equals("messages sent?>/>?"))) 
					for (int i = 0; i < size; i++) {
						Key key;

						String code = input.readLine();
						String date = input.readLine();
						String sender = input.readLine();
						int bytesize = Integer.parseInt(input.readLine());
						// String message = input.readLine();
						byte[] message = new byte[bytesize];
						byte[] decryptedmessage = new byte[bytesize];
						byte[] keybyte = new byte[8];
						socket.getInputStream().read(keybyte);
						DESKeySpec keySpec = new DESKeySpec(keybyte); // create a DESKeySpec object
						SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); // create a SecretKeyFactory
																							// object
						key = keyFactory.generateSecret(keySpec);

						socket.getInputStream().read(message);

						Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
						cipher.init(Cipher.DECRYPT_MODE, key);
						decryptedmessage = cipher.doFinal(message);
						String textmessage = new String(decryptedmessage, StandardCharsets.UTF_8);

						message messageobj = new message(code, date, sender, textmessage);
						if (code.equals("1")) {
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									messagelist.add(messageobj);
								}

							});
						} else {
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									unreadmessagelist.add(messageobj);
								}

							});
						}

					}
				}

				else if (command.equals("No such user")) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							main.popup();
							text.setText("Send failed! User doesn't exist. Please enter a valid recipient");
						}

					});

				}

			} catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
					| IllegalBlockSizeException | BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				try {
					socket.close();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
