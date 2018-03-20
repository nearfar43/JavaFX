package demo;

/*
 * author: Jeffrey Cheng
 * 
 */
	


import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	
	
	private Stage window;
	
	
    @Override
    public void start(Stage primaryStage) throws IOException{
            
            window = primaryStage;
            
            initRootLayout();
      
    
    }
    
    public void initRootLayout() {
    	
    	try {
            // Load root layout from fxml file.
    		FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/demo/LoginScene.fxml"));
    		LoginController controller = (LoginController) loader.getController();       
            
    		//loader.setLocation(Main.class.getResource("/demo/MainScene.fxml"));
            Parent root = loader.load();
             

            //set size of window depends on the screen resolution 
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int width = gd.getDisplayMode().getWidth();
            int height = gd.getDisplayMode().getHeight();
            
            // Show the scene containing the root layout.
            
            
            
            Scene scene = new Scene(root);
            window.setTitle("DISCO demo version");         
            window.setScene(scene);
            window.show();
           
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
