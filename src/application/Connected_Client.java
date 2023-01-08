package application;

import java.io.PrintWriter;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

public class Connected_Client {

	private String username;
	private String ipaddress;
	private String timestamp;
	private SSLSocket socket;
	private PrintWriter output;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Connected_Client(String u, String i, String t) {
		this.username = u;
		this.ipaddress = i;
		this.timestamp = t;
	}

	public String toString() {
		return username + " " + ipaddress + " " + timestamp;
	}

	public SSLSocket getSocket() {
		return socket;
	}

	public void setSocket(SSLSocket socket) {
		this.socket = socket;
	}

	public PrintWriter getOutput() {
		return output;
	}

	public void setOutput(PrintWriter output) {
		this.output = output;
	}

}