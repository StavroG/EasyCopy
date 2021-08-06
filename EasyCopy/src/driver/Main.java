/*
 * @author: Stavro Gorou
 * GitHub: https://github.com/StavroG
 * Date: 08/05/21
 * Description: A Java app that sends files and folders to a remote server using SFTP.
 */

package driver;

import gui.RemoteConnectGUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{

	public static void main(String[] args) throws Exception {
		launch(args);	//Needed for the GUI
	}

	@Override
	public void start(Stage arg0) throws Exception {
		arg0.setOnCloseRequest(e -> System.exit(0));	//Makes sure that the program terminates after closing the window.
		new RemoteConnectGUI();	//Opens a new window
	}


}
