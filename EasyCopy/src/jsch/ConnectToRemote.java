 package jsch;

import gui.RemoteConnectGUI;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ConnectToRemote{	
	public static String username, host, password;
	public static int port;
	public static JSch jsch;
	public static Session session;
	
	RemoteConnectGUI connectGui;
	
	public ConnectToRemote(RemoteConnectGUI gui) throws Exception {
		connectGui = gui;
		username = connectGui.getUsername();
		host = connectGui.getHost();
		password = connectGui.getPassword();
	}
	
	//Connects to the remote server that the user inputed.
	public void connect() {
		if (checkUsername() && checkHost() && checkPort() && checkPassword()) {
			connectGui.correctFormatMessage();
			try {
				jsch = new JSch();
				session = jsch.getSession(username, host, port);
				session.setPassword(password);
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.connect();
				connectGui.connectedToRemote();	//If successfully connected let user know.
			} catch(JSchException e) {
				connectGui.failedToConnect(e.getMessage());	//If could not connect let user know.
			}	
		}
	}
	
	//Disconnects the user from the remote server.
	public void disconnect() {
		if (session.isConnected()) {	//Checks to make sure that the user is connected.
			session.disconnect();
		}
		connectGui.disconnected();
	}
	
	//Makes sure that the user inputs a user name and if not, asks the user to do so.
	private boolean checkUsername() {
		if (username.length() == 0) {
			connectGui.wrongFormatMessage("Please enter the username of the remote server.");
			System.out.println("Username: " + username);
			return false;
		}
		return true;
	}
	
	//Makes sure that the user inputs a host and if not, asks the user to do so.
	private boolean checkHost() {
		if (host.length() == 0) {
			connectGui.wrongFormatMessage("Please enter the host name of the remote server.");
			return false;
		}
		return true;
	}
	
	//Makes sure that the user inputs a password and if not, asks the user to do so.
	private boolean checkPassword() {
		if (password.length() == 0) {
			connectGui.wrongFormatMessage("Please enter password");
			return false;
		}
		return true;
	}
	
	//Checks the port that the user provided to make sure that it is formated correctly.
	private boolean checkPort() {
		try { 
			if (connectGui.getPort().length() == 0) {	//If there is no port inputed, set port to 22 (The default port for most servers).
				port = 22;
			} else {	//Try to parse the string as an integer to get the port.
				port = Integer.parseInt(connectGui.getPort());
			}
			return true;
		} catch (Exception e) {	//If the string could not port to integer let the user know that the port must be a number.
			connectGui.wrongFormatMessage("Port must be a number. 22 is the default port for most servers.");
			return false;
		}
	}
}
