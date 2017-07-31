/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kvito
 */
public class FXMLremoveController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML 
    Button btnRemove, btnCancel;
    
    @FXML
    ChoiceBox choiceBox;
    
    @FXML
    TextField txtID;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        btnRemove.setDisable(true);
        
        choiceBox.getItems().add(0, "Textbook");
        choiceBox.getItems().add(1, "CD");
        choiceBox.getItems().add(2, "Video");
        
        choiceBox.getSelectionModel().selectFirst();
        
        txtID.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals(""))btnRemove.setDisable(true);
            else                   btnRemove.setDisable(false);
            
        });
    }

    @FXML
    private void remove(){
        String choiceSelect = (String)choiceBox.getSelectionModel().getSelectedItem();
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
        String idSelect = s + txtID.getText();
               
        ArrayList<ResultSet> rs = new ArrayList<>();
        try{
            switch (choiceSelect) {
            case "Textbook":
                showAlert(removeOne("Item", idSelect) && removeOne("Textbook", idSelect));
                break;
            case "CD":
                showAlert(removeOne("Item", idSelect) && removeOne("Multimedia", idSelect) && removeOne("CD", idSelect));
                break;
            case "Video":
                showAlert(removeOne("Item", idSelect) && removeOne("Multimedia", idSelect) && removeOne("Video", idSelect));
                break;
            default:
                break;
            }
        }catch(SQLException e){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("The item was not removed");
            alert.setContentText(e.toString());
            alert.show();
        }
    }
    
    private void showAlert(boolean exists){
        if(exists){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Item removed");
            alert.setHeaderText("The Database was updated");
            alert.show();
        }
        else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("The item was not found");
            alert.show();
        }
    }
    
    
    private boolean removeOne(String table, String idSelect) throws SQLException{
        String sql = "SELECT * FROM \""+ table + "\" WHERE \"ID\" = \'" + idSelect.toUpperCase() + "\'";
        PreparedStatement stmt = MyLibrary.CON.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        if(!rs.next()) return false;
        else{
            sql = "DELETE FROM \""+ table + "\" WHERE \"ID\" = \'" + idSelect.toUpperCase() + "\'";
            stmt = MyLibrary.CON.prepareStatement(sql);
            stmt.executeUpdate();
            return true;
        }
    }
    
    @FXML
    private void cancel(){
        Stage g = (Stage)btnCancel.getScene().getWindow();
        g.close();
    }
    
}
