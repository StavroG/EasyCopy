package gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jsch.ConnectToRemote;
import mongo.MongoDB;

public class RemoteConnectGUI {
	private Label formatMessage;
	private Label connectLabel;
	
	private TextField username;
	private TextField host;
	private TextField port;

	private PasswordField password;
	
	private Label userLabel;
	private Label hostLabel;
	private Label passwordLabel;
	private Label portLabel;
	
	private CheckBox saveValues;

	private Button connectBtn;
	private Button disconnectBtn;
	private Button sendToRemoteBtn;
	
	Stage stage;
	GridPane layout;
	Scene scene;

	public RemoteConnectGUI() throws Exception {
		stage = new Stage();
		layout = new GridPane();
		buildGUI();	//Starts to create the GUI for the app
	}

	private void buildGUI() throws Exception {
		//Text Fields
		username = new TextField();
		username.setPrefWidth(300);
		host = new TextField();
		port = new TextField();
		
		//Password Fields
		password = new PasswordField();

		//Labels
		userLabel = new Label("Username:");
		hostLabel = new Label("Host:");
		passwordLabel = new Label("Password:");
		portLabel = new Label("Port:");
		formatMessage = new Label();
		formatMessage.setStyle("-fx-font-weight: bold");
		connectLabel = new Label();
		connectLabel.setStyle("-fx-font-weight: bold");
		
		//Check Boxes
		saveValues= new CheckBox("Save Values");
		
		//Buttons
		connectBtn = new Button("Connect");
		connectBtn.setOnAction(e ->  {try {new ConnectToRemote(this).connect();} catch (Exception e1) {formatMessage.setText(e1.getMessage());}});
		disconnectBtn = new Button("Disconnect");
		disconnectBtn.setVisible(false);
		disconnectBtn.setOnAction(e -> {try {new ConnectToRemote(this).disconnect();} catch (Exception e1) {formatMessage.setText(e1.getMessage());}});
		sendToRemoteBtn = new Button("Send File to Remote Server");
		sendToRemoteBtn.setVisible(false);
		sendToRemoteBtn.setOnAction(e -> new ToRemoteGUI(this));
		
		//Layout
		layout = new GridPane();
		layout.getColumnConstraints().add(new ColumnConstraints(100));
		layout.setHgap(12);
		layout.setVgap(8);
		layout.setPadding(new Insets(8));
		layout.getChildren().addAll(username, host, password, port, saveValues, connectBtn, disconnectBtn, formatMessage, connectLabel, userLabel, hostLabel, passwordLabel, portLabel, sendToRemoteBtn);
		
		//Adds the components to the grid
		GridPane.setConstraints(userLabel, 0, 0);
		GridPane.setConstraints(username, 1, 0);
		GridPane.setConstraints(hostLabel, 2, 0);
		GridPane.setConstraints(host, 3, 0);
		GridPane.setConstraints(passwordLabel, 0, 1);
		GridPane.setConstraints(password, 1, 1);
		GridPane.setConstraints(portLabel, 2, 1);
		GridPane.setConstraints(port, 3, 1);
		GridPane.setConstraints(saveValues, 0, 7);
		GridPane.setConstraints(connectBtn, 0, 8);
		GridPane.setConstraints(disconnectBtn, 0, 8);
		GridPane.setConstraints(formatMessage, 0, 11, 4, 1);
		GridPane.setConstraints(connectLabel, 0, 12, 4, 1);
		GridPane.setConstraints(sendToRemoteBtn, 0, 14, 4, 1);
		
		getSavedValues();
		
		//Scene and Stage
		stage.setTitle("Easy Copy");
		scene = new Scene(layout, 800, 450);	//Creates a new window 800 pixels wide and 450 pixels tall.
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(e -> System.exit(0)); 	//Makes sure that the program terminates after closing the window.
	}
	
	//If successfully connected to remote server display message and show buttons.
	public void connectedToRemote() {
		if (saveValues.isSelected()) {
			setSavedValues();
		}
		connectLabel.setTextFill(Color.GREEN);
		connectLabel.setText("Connected to remote host.");
		connectLabel.setVisible(true);
		sendToRemoteBtn.setVisible(true);
		saveValues.setDisable(true);
		username.setDisable(true);
		password.setDisable(true);
		port.setDisable(true);
		host.setDisable(true);
		connectBtn.setVisible(false);
		disconnectBtn.setVisible(true);
	}
	
	//If failed to connect tell the user and hide the buttons.
	public void failedToConnect(String error) {
		connectLabel.setTextFill(Color.RED);
		connectLabel.setText("Failed to connect to remote host. " + error);
		connectLabel.setVisible(true);
		sendToRemoteBtn.setVisible(false);
		saveValues.setDisable(false);
		username.setDisable(false);
		password.setDisable(false);
		port.setDisable(false);
		host.setDisable(false);
	}
	
	//If the users is connected to a server and wants to disconnect, display message.
	public void disconnected() {
		connectLabel.setTextFill(Color.BLACK);
		connectLabel.setText("Disconnected from server.");
		connectLabel.setVisible(true);
		sendToRemoteBtn.setVisible(false);
		formatMessage.setVisible(false);
		saveValues.setDisable(false);
		username.setDisable(false);
		password.setDisable(false);
		port.setDisable(false);
		host.setDisable(false);
		disconnectBtn.setVisible(false);
		connectBtn.setVisible(true);
	}
	
	//If the fields are correctly formated display a message to let the user know.
	public void correctFormatMessage() {
		formatMessage.setTextFill(Color.GREEN);
		formatMessage.setText("Data is formatted correctly, connecting to server...");
		formatMessage.setVisible(true);
	}
	
	//If the fields are not correctly formated display an error message.
	public void wrongFormatMessage(String errorMessage) {
		formatMessage.setTextFill(Color.RED);
		formatMessage.setText("Error: " + errorMessage);
		connectLabel.setVisible(true);
		connectLabel.setVisible(false);
		sendToRemoteBtn.setVisible(false);
	}
	
	public String getUsername() {	//Returns user name
		return username.getText();
	}
	
	public String getHost() {	//Returns host name
		return host.getText();
	}
	
	public String getPassword() {	//Returns password
		return password.getText();
	}
	
	public String getPort() {	//Returns port
		return port.getText();
	}
	
	private void getSavedValues() {
		MongoDB db = new MongoDB();	//Creates instance of Mongo class
		
		if (db.hasValues()) {	//If database already has values saved in, fill those values in the fields.
			username.setText(db.getUsername());
			host.setText(db.getHost());
			port.setText(db.getPort());
			password.requestFocus();
			saveValues.setSelected(true);
		} 
	}
	
	private void setSavedValues() {
		MongoDB db = new MongoDB();
		
		if (!db.hasValues()) {	//If this is the first time saving any values to the database create a new file
			db.createDoc();
		}	
		db.setUsername(username.getText());	//Save user name as user name field
		db.setPort(port.getText());		//Save port as port field 
		db.setHost(host.getText());		//Save host name as host field
	}
}



























