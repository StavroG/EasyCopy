package gui;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jsch.SendFile;


public class ToRemoteGUI {
	private TextField fileLocation;
	private TextField fileDestination;
	
	private Label fileLocationLabel;
	private Label fileDestinationLabel;
	private Label progressLabel;
	
	private Button copyBtn;
	private Button locateLocalFileBtn;
	private Button locateLocalFolderBtn;
	private Button backToMainBtn;
	
	private FileChooser localFileChooser;
	private DirectoryChooser localFolderChooser;
	
	private GridPane layout;
	private Stage stage;
	private Scene scene;
	
	private RemoteConnectGUI main;
	
	public ToRemoteGUI(RemoteConnectGUI main) {
		this.main = main;
		this.stage = main.stage;
		BuildFileTransferGUI();	//Starts to create the GUI for the sending file window
	}
	
	private void BuildFileTransferGUI() {
		//Destination Choosers
		localFileChooser = new FileChooser();
		localFolderChooser = new DirectoryChooser();
		
		//Text Fields
		fileLocation = new TextField();
		fileDestination = new TextField();
		
		//Labels
		fileLocationLabel = new Label("File Location:");
		fileDestinationLabel = new Label("File Destination:");
		progressLabel = new Label();
		progressLabel.setStyle("-fx-font-weight: bold");
		
		//Buttons
		copyBtn = new Button("Copy");
		copyBtn.setOnAction(e -> new SendFile(this));
		copyBtn.setFocusTraversable(false);		
		locateLocalFileBtn = new Button("Locate File");
		locateLocalFileBtn.setFocusTraversable(false);
		locateLocalFileBtn.setOnAction(e -> fileLocation.setText(getLocalFile()));
		locateLocalFolderBtn = new Button("Locate Folder");
		locateLocalFolderBtn.setFocusTraversable(false);
		locateLocalFolderBtn.setOnAction(e -> fileLocation.setText(getLocalFolder()));
		backToMainBtn = new Button("Back");
		backToMainBtn.setFocusTraversable(false);
		backToMainBtn.setOnAction(e -> stage.setScene(main.scene));
		
		//Layout
		layout = new GridPane();
		layout.getColumnConstraints().add(new ColumnConstraints(100));
		layout.setHgap(12);
		layout.setVgap(8);
		layout.setPadding(new Insets(8));
		layout.getChildren().addAll(fileLocationLabel, fileLocation, locateLocalFileBtn, locateLocalFolderBtn, fileDestinationLabel, fileDestination, copyBtn, backToMainBtn, progressLabel);
		
		//Adds the components to the grid
		GridPane.setConstraints(fileLocationLabel, 0, 0, 3, 1);
		GridPane.setConstraints(fileLocation, 1, 0, 30, 1);
		GridPane.setConstraints(locateLocalFileBtn, 31, 0, 3, 1);
		GridPane.setConstraints(locateLocalFolderBtn, 34, 0, 3, 1);
		GridPane.setConstraints(fileDestinationLabel, 0, 1, 3, 1);
		GridPane.setConstraints(fileDestination, 1, 1, 30, 1);
		GridPane.setConstraints(copyBtn, 0, 4, 3, 1);
		GridPane.setConstraints(backToMainBtn, 0, 6, 3, 1);
		GridPane.setConstraints(progressLabel, 0, 8, 3, 1);
		
		scene = new Scene(layout, 800, 450);	//Creates a new window 800 pixels wide and 450 pixels tall.
		stage.setScene(scene);
		stage.setOnCloseRequest(e -> {
			new SendFile(this).disconnect();;
			System.exit(0);	//Makes sure that the program terminates after closing the window.
		});
	}
	
	//Opens a file Chooser so that the user can just click on the file they want to copy.
	private String getLocalFile() {	
		File selectedFile = localFileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			return selectedFile.getAbsolutePath();
		}
		return null;
	}
	
	//Opens a folder chooser so the user can find the folder they want to send.
	private String getLocalFolder() {
		File selectedFolder = localFolderChooser.showDialog(null);
		if (selectedFolder != null) {
			return selectedFolder.getAbsolutePath();
		}
		return null;
	}
	
	//Sets the progress label to a message so that the user understands what is going on.
	public void setProgressLabel(String message, boolean error) {
		progressLabel.setText(message);
		if (error) {	//If there is an error display an error message.
			progressLabel.setTextFill(Color.RED);
		}
		else {	//If there is no error then display a success message.
			progressLabel.setTextFill(Color.GREEN);
		}
		progressLabel.setVisible(true);	//Show the progress label.
	}
	
	//Gets the location of the file that we want to send to the remote server.
	public String getLocalFileLocation() {
		return fileLocation.getText();
	}
	
	//Gets the location of where the user wants us to send the file.
	public String getRemoteDestination() {
		return fileDestination.getText();
	}
}


























