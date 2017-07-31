/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 *
 * @author kvito
 */
public class FXMLDocumentController implements Initializable  {
    
    @FXML
    Button btnExport, btnClear;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
       btnExport.setShape(new Circle(1));
       btnClear.setShape(new Circle(1));
      
    }
    
    @FXML
    private void clear(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Clear Database");
        alert.setHeaderText("Careful, this option will completely erase the database!");
        alert.setContentText("Are you sure you want to continue?");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.get() == ButtonType.OK && clearDatabase()){
            
            alert.setAlertType(AlertType.INFORMATION);
            alert.setTitle("Action Performed");
            alert.setHeaderText("Database clear!");
            alert.setContentText("");
            alert.show();
        }
    }
    
    @FXML
    private void get() throws SQLException{
        Dialog<Pair<ChoiceBox, String>> dialog = new Dialog<>();
        dialog.setTitle("Get Item");
        dialog.setContentText("Please select item type and ID");
        ButtonType getIt = new ButtonType("Get it", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(getIt, ButtonType.CANCEL);
        
        Node getButton = dialog.getDialogPane().lookupButton(getIt);
        getButton.setDisable(true);
        
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        ChoiceBox choice = new ChoiceBox();
        choice.getItems().add(0, "Textbook");
        choice.getItems().add(1, "CD");
        choice.getItems().add(2, "Video");
        choice.getSelectionModel().selectFirst();
        
        TextField id = new TextField();
        id.setPromptText("Enter item's ID");
        
        grid.add(new Label("Item type: "), 0, 0);
        grid.add(choice, 1, 0);
        grid.add(new Label("ID: "), 0, 1);
        grid.add(id, 1, 1);
        
        id.textProperty().addListener((observable, oldValue, newValue)->{
           if(!newValue.trim().isEmpty()) getButton.setDisable(false);
           else                           getButton.setDisable(true);
        });
        
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> id.requestFocus());
        dialog.showAndWait();
        
        String choiceSelect = (String)choice.getSelectionModel().getSelectedItem();
        String s = "";
        switch (choiceSelect) {
            case "Textbook":
                s = "T";
                break;
            case "CD":
                s = "C";
                break;
            case "Video":
                s = "V";
                break;
            default:
                break;
        }
        String idSelect = s + id.getText();
        
        boolean found = false;
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Item details");
        alert.setHeaderText("Check the details of your selected item below");
        switch(s){
            case "T":
                found = getTextbook(idSelect, alert);
                break;
            case "C":
                found = getCD(idSelect, alert);
                break;
            case "V":
                found = getVideo(idSelect, alert);
                break;
        }
        
        if(found) alert.show();
    }
    
    private boolean getTextbook(String idSelect, Alert alert) {
        String sql;
        PreparedStatement stmt;
        ResultSet rs;
        try{
            sql = "SELECT * FROM \"Item\" WHERE \"ID\" = \'" + idSelect + "\'";
            stmt = MyLibrary.CON.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();
            String thisTitle = rs.getString("Title");
            String thisAddedOn = rs.getString("AddedOn");
               
            sql = "SELECT * FROM \"Textbook\" WHERE \"ID\" = \'" + idSelect + "\'";
            stmt = MyLibrary.CON.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();
            String thisAuthor = rs.getString("Author");
            alert.setContentText("Item type: \t\tTextbook\n"
                    + "ID: \t\t\t\t" + idSelect + "\n"
                    + "Title: \t\t\t" + thisTitle + "\n"
                    + "Added on: \t\t" + thisAddedOn + "\n"
                    + "Author: \t\t\t" + thisAuthor + "\n");
            return true;
        }catch(SQLException e){
            Alert errorAlert = new Alert(AlertType.WARNING);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Cannot get item");
            errorAlert.setContentText(e.toString());
            errorAlert.show();
            return false;
        }          
    }
    
    private boolean getCD(String idSelect, Alert alert){
        String sql;
        PreparedStatement stmt;
        ResultSet rs;
        try{
            sql = "SELECT * FROM \"Item\" WHERE \"ID\" = \'" + idSelect + "\'";
            stmt = MyLibrary.CON.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();
            String thisTitle = rs.getString("Title");
            String thisAddedOn = rs.getString("AddedOn");
               
            sql = "SELECT * FROM \"CD\" WHERE \"ID\" = \'" + idSelect + "\'";
            stmt = MyLibrary.CON.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();
            String thisArtist = rs.getString("Artist");
            
             sql = "SELECT * FROM \"Multimedia\" WHERE \"ID\" = \'" + idSelect + "\'";
            stmt = MyLibrary.CON.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();
            String thisTime = String.format("%.1f", rs.getDouble("PlayingTime"));
            
            
            alert.setContentText("Item type: \t\tCD\n"
                    + "ID: \t\t\t\t" + idSelect + "\n"
                    + "Title: \t\t\t" + thisTitle + "\n"
                    + "Added on: \t\t" + thisAddedOn + "\n"
                    + "Playing time: \t\t" + thisTime + "\n"
                    + "Author: \t\t\t" + thisArtist + "\n");
            return true;
        }catch(SQLException e){
            Alert errorAlert = new Alert(AlertType.WARNING);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Cannot get item");
            errorAlert.setContentText(e.toString());
            errorAlert.show();
            return false;
        }
    }
    
    private boolean getVideo(String idSelect, Alert alert){
        String sql;
        PreparedStatement stmt;
        ResultSet rs;
        try{
            sql = "SELECT * FROM \"Item\" WHERE \"ID\" = \'" + idSelect + "\'";
            stmt = MyLibrary.CON.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();
            String thisTitle = rs.getString("Title");
            String thisAddedOn = rs.getString("AddedOn");
               
            sql = "SELECT * FROM \"Video\" WHERE \"ID\" = \'" + idSelect + "\'";
            stmt = MyLibrary.CON.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();
            String thisDirector = rs.getString("Director");
            
             sql = "SELECT * FROM \"Multimedia\" WHERE \"ID\" = \'" + idSelect + "\'";
            stmt = MyLibrary.CON.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.next();
            String thisTime = String.format("%.1f", rs.getDouble("PlayingTime"));
            
            
            alert.setContentText("Item type: \t\tVideo\n"
                    + "ID: \t\t\t\t" + idSelect + "\n"
                    + "Title: \t\t\t" + thisTitle + "\n"
                    + "Added on: \t\t" + thisAddedOn + "\n"
                    + "Playing time: \t\t" + thisTime + "\n"
                    + "Author: \t\t\t" + thisDirector + "\n");
            return true;
        }catch(SQLException e){
            Alert errorAlert = new Alert(AlertType.WARNING);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Cannot get item");
            errorAlert.setContentText(e.toString());
            errorAlert.show();
            return false;
        }
    }
    
    
    @FXML
    private void remove(){
        newWindow("FXMLremove.fxml");
    }
   
    
    @FXML
    private void add(){
        newWindow("FXMLadd.fxml");
    }
    
    @FXML
    private void export() throws IOException{
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save As");
        chooser.setInitialFileName("myLibrary");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text File", "*.txt")
            );
        File file = chooser.showSaveDialog(null);
        try (FileWriter writer = new FileWriter(file)) {
            exportItem(file, writer);
            exportTextbook(file, writer);
            exportMultimedia(file, writer);
            exportCD(file, writer);
            exportVideo(file, writer);
            writer.close();
        }
    }
    
    private static void exportItem(File file, FileWriter writer){
        if(file != null){
            try{
                file.createNewFile();
                file.setReadable(true);
                file.setWritable(true);
                
                //FileWriter writer = new FileWriter(file);
                String spacer = "|-------|---------------------------------------|--------------|";
                String header = "|______________________________________________________________|";
                
                writer.write(String.format("%s%n%s%n", 
                        "ITEMS", "----------------------------------------------------------------"));
                writer.write(String.format("%-8s%-40s%-15s%s%n", 
                        "|ID", "|Title", "|Added On", "|"));
                
                String sql = "SELECT * FROM \"Item\" ORDER BY \"ID\";";
                PreparedStatement stmt = MyLibrary.CON.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                   String id = "|" + rs.getString("ID");
                   String title = "|" + rs.getString("Title");
                   String addedon = "|" + rs.getString("AddedOn");
                   writer.write(String.format("%s%n%-8s%-40s%-15s%s%n", 
                           spacer, id, title, addedon, "|"));
                }
                writer.write(String.format("%s%n%n", header));
            }
            catch (IOException | SQLException e){
            }
        }
    }
    
    private static void exportTextbook(File file, FileWriter writer){
        if(file != null){
            try{
                file.createNewFile();
                file.setReadable(true);
                file.setWritable(true);
                
                //FileWriter writer = new FileWriter(file);
                String spacer = "|-------|------------------------------------------------------|";
                String header = "|______________________________________________________________|";
                
                writer.write(String.format("%s%n%s%n", 
                        "TEXTBOOKS", "----------------------------------------------------------------"));
                writer.write(String.format("%-8s%-55s%s%n", 
                        "|ID", "|Author", "|"));
                
                String sql = "SELECT * FROM \"Textbook\" ORDER BY \"ID\";";
                PreparedStatement stmt = MyLibrary.CON.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                   String id = "|" + rs.getString("ID");
                   String author = "|" + rs.getString("Author");
                   writer.write(String.format("%s%n%-8s%-55s%s%n", 
                           spacer, id, author, "|"));
                }
                writer.write(String.format("%s%n%n", header));
            }
            catch (IOException | SQLException e){
            }
        }
    }
    
    private static void exportMultimedia(File file, FileWriter writer){
        if(file != null){
            try{
                file.createNewFile();
                file.setReadable(true);
                file.setWritable(true);
                
                //FileWriter writer = new FileWriter(file);
                String spacer = "|-------|------------------------------------------------------|";
                String header = "|______________________________________________________________|";
                
                writer.write(String.format("%s%n%s%n", 
                        "MULTIMEDIA", "----------------------------------------------------------------"));
                writer.write(String.format("%-8s%-55s%s%n", 
                        "|ID", "|Playing Time", "|"));
                
                String sql = "SELECT * FROM \"Multimedia\" ORDER BY \"ID\";";
                PreparedStatement stmt = MyLibrary.CON.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                   String id = "|" + rs.getString("ID");
                   Double playTime = rs.getDouble("PlayingTime");
                   
                   writer.write(String.format("%s%n%-8s%s%-54.1f%s%n", 
                           spacer, id, "|", playTime, "|"));
                }
                writer.write(String.format("%s%n%n", header));
            }
            catch (IOException | SQLException e){
            }
        }
    }
    
    private static void exportCD(File file, FileWriter writer){
        if(file != null){
            try{
                file.createNewFile();
                file.setReadable(true);
                file.setWritable(true);
                
                //FileWriter writer = new FileWriter(file);
                String spacer = "|-------|------------------------------------------------------|";
                String header = "|______________________________________________________________|";
                
                writer.write(String.format("%s%n%s%n", 
                        "CDs", "----------------------------------------------------------------"));
                writer.write(String.format("%-8s%-55s%s%n", 
                        "|ID", "|Artist", "|"));
                
                String sql = "SELECT * FROM \"CD\" ORDER BY \"ID\";";
                PreparedStatement stmt = MyLibrary.CON.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                   String id = "|" + rs.getString("ID");
                   String artist = "|" + rs.getString("artist");
                   writer.write(String.format("%s%n%-8s%-55s%s%n", 
                           spacer, id, artist, "|"));
                }
                writer.write(String.format("%s%n%n", header));
            }
            catch (IOException | SQLException e){
            }
        } 
    }
    
    private static void exportVideo(File file, FileWriter writer){
        if(file != null){
            try{
                file.createNewFile();
                file.setReadable(true);
                file.setWritable(true);
                
                //FileWriter writer = new FileWriter(file);
                String spacer = "|-------|------------------------------------------------------|";
                String header = "|______________________________________________________________|";
                
                writer.write(String.format("%s%n%s%n", 
                        "VIDEOs", "----------------------------------------------------------------"));
                writer.write(String.format("%-8s%-55s%s%n", 
                        "|ID", "|Director", "|"));
                
                String sql = "SELECT * FROM \"Video\" ORDER BY \"ID\";";
                PreparedStatement stmt = MyLibrary.CON.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                   String id = "|" + rs.getString("ID");
                   String director = "|" + rs.getString("Director");
                   writer.write(String.format("%s%n%-8s%-55s%s%n", 
                           spacer, id, director, "|"));
                }
                writer.write(String.format("%s%n%n", header));
            }
            catch (IOException | SQLException e){
            }
        } 
    }
    
    @FXML
    private void quit() throws SQLException{
        MyLibrary.CON.close();
        System.out.println("Connection Closed");
        System.exit(0);
    }
    
    private void newWindow(String fxml){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxml));
        
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Add Item to Database");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }
    
    private static boolean clearDatabase(){
        String sql;
        PreparedStatement stmt;
        try{
            sql = "DELETE FROM \"Item\";";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.execute();
           
            sql = "DELETE FROM \"Textbook\";";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.execute();
            
            sql = "DELETE FROM \"Multimedia\";";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.execute();
            
            sql = "DELETE FROM \"CD\";";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.execute();
            
            sql = "DELETE FROM \"Video\";";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.execute();
            
            return true;
        }
        catch(SQLException e){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("The Database was not cleared!");
            alert.setContentText(e.toString());
            alert.show();
            return false;
        }
    }
    
}
