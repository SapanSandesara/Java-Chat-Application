package application;

import java.util.ArrayList;

import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.text.Text;

public class ServicedServer extends Service<String> {
	private int port;

	private server server;
	private ObservableList<Connected_Client> clientlist;

	public server getServer() {

		return server;

	}

//	public void stop() {
//		server.close();
//	}

	public ServicedServer(int port, Text text, ObservableList<Connected_Client> clientlist) {
		setOnRunning(event -> {
			text.setText("server created");

		});

		this.port = port;
		this.clientlist = clientlist;

		// this.ipaddresslist = ipaddresslist;

	}

	@Override
	protected Task<String> createTask() {
		// TODO Auto-generated method stub

		return new Task<String>() {

			@Override
			protected String call() throws Exception {
				// TODO Auto-generated method stub

				server = new server(port, clientlist, this);

				server.start();

				return "I think this is unreachable as server is in a perpetual loop";

			}

		};

	}

}
