/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kvito
 */
public class FXMLaddController implements Initializable {

    @FXML
    Button btnAdd, btnCancel;
    
    @FXML
    Label lbID, lbAuthor, lbPlayTime, lbArtist, lbDirector;
    
    @FXML
    TextField txtID,txtTitle, txtAuthor, txtPlayTime, txtArtist, txtDirector;
    
    @FXML
    DatePicker date;
    
    @FXML
    ChoiceBox choiceBox;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lbAuthor.setDisable(true);
        lbPlayTime.setDisable(true);
        lbArtist.setDisable(true);
        lbDirector.setDisable(true);
        
        txtAuthor.setDisable(true);
        txtPlayTime.setDisable(true);
        txtArtist.setDisable(true);
        txtDirector.setDisable(true);
        
        btnAdd.setDisable(true);
        
        choiceBox.getItems().add(0, "Textbook");
        choiceBox.getItems().add(1, "CD");
        choiceBox.getItems().add(2, "Video");
        
        
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int n = newValue.intValue();
                switch (n) {
                case 0:
                    lbAuthor.setDisable(false);
                    lbPlayTime.setDisable(true);
                    lbArtist.setDisable(true);
                    lbDirector.setDisable(true);
                    
                    txtAuthor.setDisable(false);
                    txtPlayTime.setDisable(true);
                    txtArtist.setDisable(true);
                    txtDirector.setDisable(true);
                    setIDtype(n);
                    break;
                case 1:
                    lbPlayTime.setDisable(false);
                    lbArtist.setDisable(false);
                    lbAuthor.setDisable(true);
                    lbDirector.setDisable(true);
                    
                    txtPlayTime.setDisable(false);
                    txtArtist.setDisable(false);
                    txtAuthor.setDisable(true);
                    txtDirector.setDisable(true);
                    
                    setIDtype(n);
                    break;
                case 2:
                    lbPlayTime.setDisable(false);
                    lbDirector.setDisable(false);
                    lbAuthor.setDisable(true);    
                    lbArtist.setDisable(true);
        
                    txtPlayTime.setDisable(false);
                    txtDirector.setDisable(false);
                    txtAuthor.setDisable(true);
                    txtArtist.setDisable(true);
                    
                    setIDtype(n);
                    break;
                }
            }
        });
        
        choiceBox.getSelectionModel().selectFirst();
        date.setValue(LocalDate.now());
        
        txtID.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals(""))btnAdd.setDisable(true);
            else                   btnAdd.setDisable(false);
            
        });
    }
    
    private void setIDtype(int value){
        switch(value){
            case 0:
                lbID.setText("T -");
                break;
            case 1:
                lbID.setText("C -");
                break;
            case 2:
                lbID.setText("V -");
                break;
        }
    }
    
    @FXML
    private void cancel(){
        Stage g = (Stage)btnCancel.getScene().getWindow();
        g.close();
    }
    
    @FXML
    private void addItem() {
        boolean added;
        String id = lbID.getText() + txtID.getText();
        id = id.replaceAll("[- ]+", "");
        String title = txtTitle.getText();
        String addedOn = date.getValue().toString();
        
        added = updateItem(title, id, addedOn);
        
        String author, artist, director;
        Double playTime;
       
        switch(choiceBox.getSelectionModel().getSelectedIndex()){
            case 0:
                author = txtAuthor.getText();
                added = updateTextbook(author, id);
                break;
            case 1:
                playTime = Double.parseDouble(txtPlayTime.getText());
                artist = txtArtist.getText();
                added = updateCD(playTime, artist, id);
                break;
            case 2:
                playTime = Double.parseDouble(txtPlayTime.getText());
                director = txtDirector.getText();
                added = updateVideo(playTime, director, id);
                break;
        }
        
        if(added){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Added Item");
            alert.setHeaderText("The Database was updated");
            alert.show();
        }
        
        cancel();
    }
    
    private static boolean updateItem(String title, String id, String addedOn){
        boolean added;
        PreparedStatement stmt;
        try{
            String sql = "INSERT INTO \"Item\" VALUES(?,?,?);";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, id);
            stmt.setString(3, addedOn);
            stmt.executeUpdate(); 
            added = true;
        }catch(SQLException e){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("The Database was not updated!");
            alert.setContentText(e.toString());
            alert.show();
            added = false;
        }
        return added;
    }
    
    private static boolean updateTextbook(String author, String id){
        boolean added;
        PreparedStatement stmt;
        try{
            String sql = "INSERT INTO \"Textbook\" VALUES(?,?);";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.setString(1, author);
            stmt.setString(2, id);
            stmt.executeUpdate(); 
            added = true;
        }catch(SQLException e){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("The Database was not updated!");
            alert.setContentText(e.toString());
            alert.show();
            added = false;
        }
        
        return added;
    }
    
    private static boolean updateCD(double playTime, String artist, String id){
        boolean added;
        PreparedStatement stmt;
        try{
            String sql = "INSERT INTO \"Multimedia\" VALUES(?,?);";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.setDouble(1, playTime);
            stmt.setString(2, id);
            stmt.executeUpdate();
            
            sql = "INSERT INTO \"CD\" VALUES(?,?);";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.setString(1, artist);
            stmt.setString(2, id);
            stmt.executeUpdate();
            added = true;
        }catch(SQLException e){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("The Database was not updated!");
            alert.setContentText(e.toString());
            alert.show();
            added = false;
        }
        return added;
    }
    
    private static boolean updateVideo(double playTime, String director, String id){
        boolean added;
        PreparedStatement stmt;
        try{
            String sql = "INSERT INTO \"Multimedia\" VALUES(?,?);";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.setDouble(1, playTime);
            stmt.setString(2, id);
            stmt.executeUpdate();
            
            sql = "INSERT INTO \"Video\" VALUES(?,?);";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.setString(1, director);
            stmt.setString(2, id);
            stmt.executeUpdate();
            added = true;
        }catch(SQLException e){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("The Database was not updated!");
            alert.setContentText(e.toString());
            alert.show();
            added = false;
        }
        return added;
    }
}
