package application2;

import java.io.BufferedReader;
import java.net.Socket;
import java.security.Key;

import javax.net.ssl.SSLSocket;

import application.server;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ServicedClient extends Service<String> {
	SSLSocket socket;
	ObservableList<message> messagelist;
	BufferedReader input;
	ObservableList<message> unreadmessagelist;
	Text text;
	Main main;
	Stage ps;

	public ServicedClient(SSLSocket socket, ObservableList<message> messagelist, BufferedReader input,
			ObservableList<message> unreadmessagelist, Text text, Main main, Stage ps) {
		this.socket = socket;
		this.messagelist = messagelist;
		this.input = input;
		this.unreadmessagelist = unreadmessagelist;
		this.text = text;
		this.main = main;
		this.ps = ps;
	}

	@Override
	protected Task<String> createTask() {
		// TODO Auto-generated method stub
		return new Task<String>() {

			@Override
			protected String call() throws Exception {
				// TODO Auto-generated method stub

//				Client c = new Client(socket,messagelist,input);
//				
//				c.start();
				new Clientthread(socket, messagelist, input, unreadmessagelist, text, main, ps).start();

				return "whatever";

			}

		};
	}

}
