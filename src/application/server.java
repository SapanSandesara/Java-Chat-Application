package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class server {

	private ServerSocket serversocket;
	private int port;
	private ObservableList<Connected_Client> clientlist;
	private boolean close = false;
	private Task<String> t;
//	private ArrayList<serverthread>st = new ArrayList<serverthread>();

	public server(int port, ObservableList<Connected_Client> clientlist, Task<String> t) {
		this.port = port;
		this.clientlist = clientlist;
		this.t = t;

	}

	public void start() {
		try {

			// SSL Tutorial from : https://www.youtube.com/watch?v=VSi3KFlVAbE&t=600s

			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			// System.setProperty("javax.net.ssl.keyStoreType", "JKS");
			System.setProperty("javax.net.ssl.keyStore", "myKeyStore.jks");
			System.setProperty("javax.net.ssl.keyStorePassword", "cloudandsecurityproject");

			// System.setProperty("javax.net.ssl.keyStore", System.getProperty("java.home")
			// + "/lib/security/cacerts");
//			
//			
			// System.setProperty("javax.net.ssl.keyStorePassword","changeit");

			// System.setProperty("javax.net.debug","all");
			System.out.println("HI");
			SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			serversocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(port);
			// serversocket = new ServerSocket(port);
			// System.out.println("Server started on 9092");

			while (true) {
				System.out.println("Waiting for client to connect");

//				Socket clientsocket = serversocket.accept();
				SSLSocket clientsocket = (SSLSocket) serversocket.accept();

				System.out.println(clientsocket.getRemoteSocketAddress());

				new serverthread(clientsocket, clientlist).start();

//				this.st.add(st);

			}
		}

		catch (SocketException s) {
			//s.printStackTrace();
			System.out.println("Line 71 class:server");
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			System.out.println("Line 76 class: server");
		}

	}

	public void close() {
		try {

			for (Connected_Client cc : clientlist) {
				cc.getSocket().close();
			}

			serversocket.close();
			serversocket = null;
			t.cancel();

			System.out.println("server closed");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Line 97 class: server");
		}
	}

}
