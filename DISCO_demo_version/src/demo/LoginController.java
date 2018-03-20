package demo;

/*
 * author: Jeffrey Cheng
 * 
 */


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;



public class LoginController {
	

	
	@FXML
	private Button submitButton;	
	
	@FXML
	private TextField idText;
	
	@FXML
	private TextField scenarioText;
	
	@FXML
	private TextField trialText;
	
	@FXML
	private Button scenarioButton;
	
	@FXML
	private Button trailButton;
	
	private String id;
	
	private File sFile;
	
	private File tFile;
	

	
	
	@FXML
	private void handleSumitButtonAction (ActionEvent event) throws IOException {
		
		id = idText.getText();

		
		if (id == "" || idText.getText().isEmpty() || sFile == null || tFile == null) {
			
			return;
		}
		
		String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
		File file = new File(currentPath, id + ".txt");
		
		if (file.exists()) {
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Warning");
			alert.setContentText("The participant id exists, Do you want to overwrite it?");
			
			Optional<ButtonType> result = alert.showAndWait();
			
			if (!result.isPresent()) {
				
				return;
			}
			
			else if (result.get() == ButtonType.CANCEL) {
				
				return;
				
			}
			
			else if (result.get() == ButtonType.OK) {
				
				Stage stage = (Stage) submitButton.getScene().getWindow();
				stage.close();
				
				SceneController sc = new SceneController(id, sFile, tFile);
				
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("MainScene.fxml"));
			
				loader.setController(sc);
				
					
				Parent root = loader.load();		
				Scene scene = new Scene(root);
				Stage window = new Stage();
				window.setTitle("Disco");
				window.setScene(scene);
				window.show();
				
				
			}
			
			
		}
		
		
		else {
			
			
			Stage stage = (Stage) submitButton.getScene().getWindow();
			stage.close();
			
			SceneController sc = new SceneController(id, sFile, tFile);
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("MainScene.fxml"));
		
			loader.setController(sc);
			
				
			Parent root = loader.load();		
			Scene scene = new Scene(root);
			Stage window = new Stage();
			window.setTitle("Disco");
			window.setScene(scene);
			window.show();
			
			
		}
		


		
	}
	
	@FXML
	private void chooseScenarioFile(ActionEvent event) throws IOException {
		
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open Input Scenario File");
		String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
		chooser.setInitialDirectory(new File(currentPath));
		chooser.getExtensionFilters().addAll(new ExtensionFilter("Text File", "*.txt"));
		sFile = chooser.showOpenDialog(new Stage());				
		
		if(sFile == null) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("null File");
			alert.setContentText("Please set the scenario file");
			alert.show();
			
		}
		
		else {
			scenarioText.setText(sFile.getName());
		}
	}
	
	@FXML
	private void chooseTrialFile(ActionEvent event) throws IOException {
		
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open Input Trial File");
		String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
		chooser.setInitialDirectory(new File(currentPath));
		chooser.getExtensionFilters().addAll(new ExtensionFilter("Text File", "*.txt"));
		tFile = chooser.showOpenDialog(new Stage());
		
		if(sFile == null) {
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("null File");
			alert.setContentText("Please set the trial file");
			alert.show();
			
		}
		
		else {
			trialText.setText(tFile.getName());
		}
		
	}


}
