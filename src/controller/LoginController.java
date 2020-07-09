package controller;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.exceptions.InvalidUserInput;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField loginText;
    Alert alert = new Alert(Alert.AlertType.ERROR);

    ObservableList<String> IDinLogin = FXCollections.observableArrayList();
    //When the login button is pushed:
    @FXML private void loginButtonPushed(ActionEvent log) throws IOException, InvalidUserInput {

        try {
            if (loginText.getText().isEmpty()) {
                throw new NullPointerException("Username Cannot be empty Try Again!!!!!");
            } else if (loginText.getText().charAt(0) != 's') {
                throw new InvalidUserInput("Username should start with 's'");
            } else if (loginText.getText().substring(1).length() != 7) {
                throw new InvalidUserInput("Username should have 8 characters including 's'");
            }
            //call intpass method to handle the Numberformat Exception
            else if (intPass(loginText.getText().substring(1)) == false) {
                throw new InvalidUserInput("Last 7 characters of username must be numbers!");
            }
            //change to the main window when login input validation satisfied
            else {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/MainWindow.fxml"));
                Parent main = loader.load();

                Scene mainScene = new Scene(main);
                //access the controller
                MainWindowController mainControl = loader.getController();
                //Passing the username to the Main Window
                mainControl.setUsername(loginText.getText());
                Stage window = (Stage) ((Node) log.getSource()).getScene().getWindow();
                window.setTitle("Main Window");
                window.setScene(mainScene);
                window.centerOnScreen();
                window.show();

            }
        } catch(Exception e){
            alert.setTitle("Error alert!");
            alert.setHeaderText("Can not Login");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            loginText.clear();
        }

    }

    //Checking for numbers in the username
    private boolean intPass(String user){
        String str = user;
        try{
            int x = Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }
    @Override
   @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


}
