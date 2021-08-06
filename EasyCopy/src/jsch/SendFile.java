package jsch;

import java.io.File;
import java.io.FileInputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;


import gui.ToRemoteGUI;


public class SendFile{

	private String localFileLocation;
	private String remoteDestination;
	private String fileName;
	
	private ToRemoteGUI gui;
	
	private Channel channel = null;
	private ChannelSftp channelSftp = null;
	
	public SendFile(ToRemoteGUI gui) {
		this.gui = gui;
		localFileLocation = gui.getLocalFileLocation();
		remoteDestination = gui.getRemoteDestination();
		fileName = getName(localFileLocation);
		startSftp();
	}
	
	//Sends the file to the remote server.
	private void startSftp() {
		if (checkFileLocation() && checkFileDestination()) {
			try {	//Tries to connect to the remote server by using SFTP.
				channel = ConnectToRemote.session.openChannel("sftp");
				channel.connect();
				channelSftp = (ChannelSftp) channel;
				channelSftp.cd(remoteDestination);

				sendFiles(channelSftp, localFileLocation);
			} catch (Exception e) {	//If could not connect display an error message to the user.
				gui.setProgressLabel("Could not send file. " + e.getMessage(), true);
			}
		}
	}
	
	//Disconnects from the remote server.
	public void disconnect() {
		if (channelSftp != null && channelSftp.isConnected()) {	//If there is an SFTP connection to the server close that connection.
			channelSftp.exit();	
		}
		if (channel != null && channel.isConnected()) {	//If user is connected to the server, disconnect from the channel.
			channel.disconnect();
		}
		if (ConnectToRemote.session != null && ConnectToRemote.session.isConnected()) {	//If user is still connected to the server, disconnect.
			ConnectToRemote.session.disconnect();
		}
	}
	
	//Sends a file or folder to the server via SFTP.
	private void sendFiles(ChannelSftp channelSftp, String path) {
		try {
			File file = new File(path);
			
			if (file.isDirectory()) {	//If we are sending a folder then we have to create a directory on the server and send files individually.
				channelSftp.mkdir(getName(path));	//Creates a new directory on the remote server
				channelSftp.cd(getName(path));	//Changes directory to where the user wants to send the files
				
				File[] files = file.listFiles();	//Gets a list of all the files.
				if (files != null) {	//Makes sure the list is not empty
					for (int i = 0; i < files.length; i++) {
						if (files[i].isFile()) {	//If the file in the folder is a file copy that file to the destination in the remote server.
							channelSftp.put(new FileInputStream(files[i]), files[i].getName() );
						}
						else if (files[i].isDirectory()) {	//If it is not a file and is a folder then create a new directory in the server and use recursion to send all remaining files and subfolders.
							sendFiles(channelSftp, files[i].getAbsolutePath());
						}
					}
				}
				channelSftp.cd("..");	//Move out of the folder created.
			}
			else {	//If we are just sending one file than send that file to the destination in the remote server.
				channelSftp.put(new FileInputStream(file), fileName);
			}
			
			gui.setProgressLabel("Successfully uploaded all files.", false);	//If sent all files display a success message.
		}catch (Exception e) {
			gui.setProgressLabel("Could not send file. " + e.getMessage(), true);	//If there is an error display an error message.
		}
	}
	
	//Makes sure the file is not empty so we know what file to send.
	private boolean checkFileLocation() {
		if (localFileLocation.length() == 0) {
			gui.setProgressLabel("Please enter the file location of the file you want to copy.", true);
			return false;
		}
		return true;
	}
	
	//Makes sure that there is a destination of where the file is to be sent.
	private boolean checkFileDestination() {
		if (remoteDestination.length() == 0) {
			gui.setProgressLabel("Please enter the file destination of where you want the file to be copied.", false);
			return false;
		} 
		return true;
	}
	
	//Gets the file name by iterating through the file path.
	private String getName(String path) {
		if (path.length() == 0) {
			return " ";
		}
		
		int index = 0;
		for (int i = 0; i < path.length(); i++) {
			if (path.charAt(i) == '\\') {	//Looks for any \ in the string.
				index = i;	//The index of the latest \ in the string is saved.
			}
		}
		//Returns the name of the file by returning a substring starting at where the last \ was found.
		return path.substring(++index, path.length());	
	}
}























