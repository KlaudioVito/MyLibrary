/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author kvito
 */
public class MyLibrary extends Application {
    public static Connection CON = null;
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
       
        stage.setScene(scene);
        stage.setTitle("My Library");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        CON = getConnection();
        launch(args);
    }
    
    public static Connection getConnection() throws SQLException {
	final String CONNECTION_USERNAME = "postgres";
	final String CONNECTION_PASSWORD = "password";
	final String URL = "jdbc:postgresql://localhost:5432/MyLibrary";
	Connection connection = null;
	if (connection == null) {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Could not register driver!");
			//e.printStackTrace();
		}
		connection = DriverManager.getConnection(URL, CONNECTION_USERNAME, CONNECTION_PASSWORD);
	}
	
	//If connection was closed then retrieve a new connection
	if (connection.isClosed()){
		System.out.println("Opening new connection...");
		connection = DriverManager.getConnection(URL, CONNECTION_USERNAME, CONNECTION_PASSWORD);
	}
	return connection;
}
    
}
