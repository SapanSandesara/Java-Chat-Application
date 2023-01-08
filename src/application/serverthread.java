package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.net.ssl.SSLSocket;

import javafx.application.Platform;
import javafx.collections.ObservableList;

public class serverthread extends Thread {
	SSLSocket socket;
	String databaseuser = "admin";
	String pass = "cloudandsecurityproject";
	Connection connection;
	ObservableList<Connected_Client> clientlist;
	String user;
	PrintWriter outgoing;
	DESecnryption ds = new DESecnryption();

	public serverthread(SSLSocket s, ObservableList<Connected_Client> clientlist) {
		this.socket = s;
		this.clientlist = clientlist;
		try {
			// Class.forName("com.mysql.cj.jdbc.Driver");

			/*
			 * I think having specifying the system.settrustproperty to my own self signed
			 * certificate in the trust store file is throwing unknown ca exception when
			 * using SSL connection with AWS RDS database. I have disabled useSSL in
			 * properties for now. I can't seem to edit the system trust store and it
			 * doesn't seem like a good idea anyway so I will leave it like this for now.
			 * All sensitive data like password and messages are sent encrypted to the
			 * database anyway.
			 */

			Class.forName("software.aws.rds.jdbc.mysql.Driver");
			Properties properties = new Properties();
			properties.put("user", "admin");
			properties.put("password", "cloudandsecurityproject");

			properties.put("useSSL", "false");

			connection = null;

			connection = DriverManager.getConnection(
					"jdbc:mysql:aws://mswdev-messaging-app-sapan.c619xmfld4ra.ap-southeast-2.rds.amazonaws.com:3306/social_messaging_app",
					properties);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		try {
			outgoing = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (true) {

			try {
				System.out.println("server waiting");

				BufferedReader incoming = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String incoming_request = incoming.readLine();
				System.out.println(incoming_request);

				if (incoming_request == null) {

					// It is throwing a stupid "not on application thread" exception so I am using
					// this solution
					// I found on google. Apparently you cannot update JAVA FX objects from a thread
					// without this.

					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							for (int i = 0; i < clientlist.size(); i++) {
								Connected_Client c = clientlist.get(i);
								if (c.getSocket() == socket) {
									clientlist.remove(i);
									break;
								}
							}

						}

					});
					socket.close();
					return;
				}

				if (incoming_request.equals("connection log in")) {

					String username = incoming.readLine();
					String password = incoming.readLine();
					PreparedStatement loginstatement = connection
							.prepareStatement("SELECT * FROM users WHERE username=?");
//				Statement statement = connection.createStatement();
					loginstatement.setString(1, username);
//				ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE username=?");
					ResultSet rs = loginstatement.executeQuery();
					byte[] check;
					String usercheck;
					if (rs.next()) {
						usercheck = rs.getString("username");
						check = rs.getBytes("password");
						Key passkey = ds.loadkey("password");
						Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
						cipher.init(Cipher.DECRYPT_MODE, passkey);
						byte[] decryptedpass = cipher.doFinal(check);
						String decryptpass = new String(decryptedpass, StandardCharsets.UTF_8);
						// System.out.println(rs.getString("password"));
						Date date = new Date();
						Timestamp ts = new Timestamp(date.getTime());
						if (password.equals(decryptpass)) {

							outgoing.println("Login successful");
							this.user = usercheck;
							Key key = ds.loadkey(username.toLowerCase());

//							
							socket.getOutputStream().write(key.getEncoded());
							socket.getOutputStream().flush();
							// statement.execute("INSERT INTO connections
							// VALUES('"+username+"','"+socket.getRemoteSocketAddress()+"','"+ts+"')");
							Connected_Client cc = new Connected_Client(this.user,
									socket.getRemoteSocketAddress().toString(), ts.toString());
							cc.setSocket(socket);
							cc.setOutput(outgoing);
							Platform.runLater(new Runnable() {

								@Override
								public void run() {

									// TODO Auto-generated method stub
									clientlist.add(cc);

								}

							});

						} else {

							outgoing.println("invalid password");
						}
					} else {
						outgoing.println("invalid username");
					}

				}

				else if (incoming_request.equals("sign up")) {
//				boolean taken = false;

					String username = incoming.readLine();
					String firstname = incoming.readLine();
					String lastname = incoming.readLine();
					String password = incoming.readLine();
					// DESecnryption des = new DESecnryption("password");
					Key key = ds.loadkey("password");
					Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
					cipher.init(Cipher.ENCRYPT_MODE, key);
					byte[] encryptedpassword = cipher.doFinal(password.getBytes());

					PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=?");
					PreparedStatement insertstatement = connection
							.prepareStatement("INSERT INTO users VALUES(?,?,?,?)");

					statement.setString(1, username);
//				while(rs.next()) {
//					if(rs.getString("username").equals(username)) {
//						outgoing.println("username taken");
//						taken = true;
//						break;
//					}
//				}
					ResultSet rs = statement.executeQuery();

					if (rs.next()) {
						outgoing.println("username taken");
					} else {

						DESecnryption ds = new DESecnryption(username.toLowerCase());

						insertstatement.setString(1, username);
						insertstatement.setBytes(2, encryptedpassword);
						insertstatement.setString(3, firstname);
						insertstatement.setString(4, lastname);

//					statement.execute("INSERT INTO users VALUES('"+username+"','"+password+"','"+firstname+"','"+lastname+"')");
						insertstatement.execute();
						outgoing.println("signup success");
					}

				}

				else if (incoming_request.equals("Send message")) {
					int size = Integer.parseInt(incoming.readLine());
					byte[] message = new byte[size];
					boolean abc = true;
					String sender = incoming.readLine();
					String recipient = incoming.readLine();
					// String message = incoming.readLine();
					socket.getInputStream().read(message);
					PreparedStatement select = connection.prepareStatement("SELECT * FROM users WHERE username=?");
					PreparedStatement insert = connection.prepareStatement("INSERT INTO messages VALUES(?,?,?,?,?)");
//				Statement statement = connection.createStatement();
					select.setString(1, recipient);
//				ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE username='"+recipient+"'");
					ResultSet rs = select.executeQuery();
					if (rs.next()) {
						for (Connected_Client c : clientlist) {
							if (c.getUsername().equalsIgnoreCase(recipient)) {
//							
								Date date = new Date();
								Timestamp ts = new Timestamp(date.getTime());
								insert.setObject(1, ts);
								insert.setString(2, sender);
								insert.setString(3, recipient);
								insert.setBytes(4, message);
								insert.setInt(5, 1);

								insert.execute();
//							statement.execute("INSERT INTO messages VALUES('"+ts+"','"+sender+"','"+recipient+"','"+message+"','1')");

								PrintWriter mess = c.getOutput();
								mess.println("new message");
								mess.println(size);
								// mess.println(ts +" "+sender+" "+message);
								mess.println(ts);
								mess.println(sender);
								Key key = ds.loadkey(sender.toLowerCase());
								c.getSocket().getOutputStream().write(key.getEncoded());
								c.getSocket().getOutputStream().flush();
								c.getSocket().getOutputStream().write(message);
								c.getSocket().getOutputStream().flush();
								// mess.println(message);
								abc = false;
								break;

							}

						}
						if (abc) {
							System.out.println("I am here");
							Date date = new Date();
							Timestamp ts = new Timestamp(date.getTime());
//						statement.execute("INSERT INTO messages VALUES('"+ts+"','"+sender+"','"+recipient+"','"+message+"','0')");
							insert.setObject(1, ts);
							insert.setString(2, sender);
							insert.setString(3, recipient);
							insert.setBytes(4, message);
							insert.setInt(5, 0);

							insert.execute();
						}

					} else {
						outgoing.println("No such user");
					}
				}

				else if (incoming_request.equals("retrieve messages")) {
					PreparedStatement retrieve = connection.prepareStatement(
							"SELECT * FROM messages WHERE recipientID=?", ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					PreparedStatement insert = connection
							.prepareStatement("UPDATE messages SET issent = ? WHERE recipientID=?");
//				Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					retrieve.setString(1, this.user);
//				ResultSet rs = statement.executeQuery("SELECT * FROM messages WHERE recipientID='"+this.user+"'");
					ResultSet rs = retrieve.executeQuery();
					outgoing.println("retrieving messages");
//				outgoing.println(" ");
					int size = 0;
					if (rs != null) {
						rs.last(); // moves cursor to the last row
						size = rs.getRow(); // get row id
					}
					rs.beforeFirst();
					outgoing.println(size);

					while (rs.next()) {
						if (rs.getInt("issent") == 1) {
							// outgoing.println(rs.getString("time_stamp")+ " "+rs.getString("senderID")+
							// rs.getString("message"));
							outgoing.println("1");
							outgoing.println(rs.getString("time_stamp"));
							outgoing.println(rs.getString("senderID"));
							int bytesize = rs.getBytes("message").length;
							outgoing.println(bytesize);
							// outgoing.println(rs.getString("message"));
							Key key = ds.loadkey(rs.getString("senderID"));
							socket.getOutputStream().write(key.getEncoded());
							socket.getOutputStream().flush();

							socket.getOutputStream().write(rs.getBytes("message"));
							socket.getOutputStream().flush();

						} else {
							// outgoing.println("NEW MESSAGE: "+rs.getString("time_stamp")+ "
							// "+rs.getString("senderID")+ rs.getString("message"));
							outgoing.println("0");
							outgoing.println(rs.getString("time_stamp"));
							outgoing.println(rs.getString("senderID"));
							int bytesize = rs.getBytes("message").length;
							outgoing.println(bytesize);
							// outgoing.println(rs.getString("message"));
							Key key = ds.loadkey(rs.getString("senderID"));
							socket.getOutputStream().write(key.getEncoded());
							socket.getOutputStream().flush();

							socket.getOutputStream().write(rs.getBytes("message"));
							socket.getOutputStream().flush();

						}

					}
//				outgoing.println("messages sent?>/>?");
					// statement.execute("UPDATE messages SET issent = '1' WHERE
					// recipientID='"+this.user+"'");
					insert.setInt(1, 1);
					insert.setString(2, this.user);
					insert.execute();
				}

			}

			catch (Exception e) {
			//	e.printStackTrace();
				System.out.println("Line 358 class:serverthread");
				try {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							for (int i = 0; i < clientlist.size(); i++) {
								Connected_Client c = clientlist.get(i);
								if (c.getSocket() == socket) {
									clientlist.remove(i);
									break;
								}
							}

						}

					});

					socket.close();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
//					e1.printStackTrace();
					System.out.println("Line 382 class:serverthread");
				}

			}

		}
	}

}
