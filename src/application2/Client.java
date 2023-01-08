package application2;

import java.io.BufferedReader;
import java.net.Socket;

import javafx.collections.ObservableList;

public class Client {
	Socket socket;
	ObservableList<message> messagelist;
	BufferedReader input;

	public Client(Socket socket, ObservableList<message> messagelist, BufferedReader input) {
		this.socket = socket;
		this.messagelist = messagelist;
		this.input = input;

	}

	public void start() {
//		new Clientthread(socket,messagelist,input).start(); 
	}

}
