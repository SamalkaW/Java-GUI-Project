package controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Event;
import model.Post;
import model.Status;
import model.exceptions.NegativeNumberException;
import model.hsql_db.ConnectionTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class NewEventController implements Initializable {


    @FXML private TextField eventNameTextfield;
    @FXML private TextField eventDescriptionTextField;
    @FXML private TextField eventVenueTextField;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField eventCapacityTextField;
    @FXML private String username;
    @FXML private ImageView uploadPhoto;
    private FileChooser imageChoose;
    private File filePath;
    private Image upImage;
    private String filename;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        uploadPhoto.setImage(new Image("file:Images/uploadPhoto.png"));

    }

    //When user press SUBMIT in the New Event Window this should happen.
    @FXML private void submitPressed(ActionEvent actionEvent) throws NegativeNumberException{
        try{
            if(eventNameTextfield.getText().isEmpty()){
                throw new NullPointerException("Event Name shouldn't be Empty");
            }
            else if (eventDescriptionTextField.getText().isEmpty()){
                throw new NullPointerException("Event Description shouldn't be Empty");
            }
            else if(eventVenueTextField.getText().isEmpty()){
                throw new NullPointerException("Event Venue shouldn't be Empty");
            }
            else if(eventDatePicker.getValue()==null){
                throw new NullPointerException("Event date shouldn't be Empty");
            }
            else if(eventCapacityTextField.getText().isEmpty()){
                throw new NullPointerException("Event Capacity shouldn't be Empty");
            }
            else if(!intParse(eventCapacityTextField.getText())){
                throw new NumberFormatException("Event Capacity should be a whole number");
            }
            else if(!negativeParse(eventCapacityTextField.getText())){
                throw new NegativeNumberException("Event Capacity should be Positive");
            }
            else{
                //method to add the event to the database
                Submit(actionEvent);
            }

        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

    }

    private void Submit(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("New Event Confirmation");
        alert.setHeaderText("Create the event?");
        Optional<ButtonType>result = alert.showAndWait();
        //If user confirms to make  new Event
        if(result.get()==ButtonType.OK) {
            addEventToDB();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/MainWindow.fxml"));
            Parent mainw = loader.load();

            Scene main = new Scene(mainw);
            //access the controller
            MainWindowController mainWindowController = loader.getController();
            //Passing the username to the Main Window
            mainWindowController.setUsername(username);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setTitle("Main Window");
            window.setScene(main);
            window.centerOnScreen();
            window.show();
        }
        else {}
    }


    //Getting the new event ID
    private String newEventID(){

        String LastID ="";
        final String DB_NAME = "testDB";
        final String EVENT_TABLE = "EVENT";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "SELECT * FROM " + EVENT_TABLE+"  ORDER BY postID DESC LIMIT 1;";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                if (resultSet.next()) {
                     LastID = resultSet.getString("postID");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        int last;
        int newIDLastDigi;
        String newEveID = "";
       String LastDig = LastID.substring(3);
        if(intPass(LastDig)){
            last = Integer.parseInt(LastDig);
            newIDLastDigi = last+1;
            newEveID=String.format("EVE%03d",newIDLastDigi);
            System.out.println(newEveID);
        }
        return newEveID;
    }

    private void addEventToDB() throws IOException {
        String newEvID = newEventID();
        final String DB_NAME = "testDB";
        final String EVENT_TABLE = "EVENT";
        String imageN;
        String title = eventNameTextfield.getText();
        if(filename!=null){
            imageN = filename;
           // System.out.println("if: "+imageN);
            save();
        }
        else{
            imageN = "defaultImage.jpg";
        System.out.println("else: "+imageN);}
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {

            String query = "INSERT INTO "+EVENT_TABLE+" VALUES ('"+newEvID+"','"+imageN+"','"+eventNameTextfield.getText()+
                    "','"+eventDescriptionTextField.getText()+"','"+username+"','"+"OPEN','"+eventVenueTextField.getText()+"','"
                    +eventDatePicker.getValue().toString()+"',"+Integer.parseInt(eventCapacityTextField.getText())+",0)";

            int result = stmt.executeUpdate(query);
            con.commit();
            System.out.println("Insert into table " + EVENT_TABLE + " executed successfully "+result);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean intPass(String Last){
        String str = Last;
        try{
            int x = Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    private boolean negativeParse(String eventCap){
        String str = eventCap;
        if(intParse(str)){
            int x = Integer.parseInt(str);
            if(x<0){
                return false;
            }
            else return true;
        }
        else{
            return false;
        }

    }
    //Method to convert a String to Int and see whether it's a number
    private boolean intParse(String eventCap) {
        String str = eventCap;
        try{
            int x = Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    //This should happen when user press CANCEL in the New Event Window (No event is created)
    @FXML private void cancelPressed(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Event Cancellation");
        alert.setHeaderText("Absolutely Sure you want to Cancel Registration & Go to Main?");
        Optional<ButtonType>result = alert.showAndWait();
        //If user confirms to cancel making new Event
        if(result.get()==ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/MainWindow.fxml"));
            Parent mainw = loader.load();

            Scene main = new Scene(mainw);
            //access the controller
            MainWindowController mainWindowController = loader.getController();
            //Passing the username to the New Event Window
            mainWindowController.setUsername(username);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setTitle("Main Window");
            window.setScene(main);
            window.centerOnScreen();
            window.show();
        }
        else {}
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @FXML private void uploadImage(MouseEvent mouseEvent) {
        Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        imageChoose = new FileChooser();
        imageChoose.setTitle("Choose a Pic");
        imageChoose.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG","*.png"),
                new FileChooser.ExtensionFilter("JPEG","*.jpg"));
        this.filePath = imageChoose.showOpenDialog(stage);

        try{
            BufferedImage bImage = ImageIO.read(filePath);
             upImage = SwingFXUtils.toFXImage(bImage, null);
            uploadPhoto.setImage(upImage);
             filename = filePath.getName();
        } catch (IOException e ) {
           // e.printStackTrace();
            System.out.println("Do nothing");
        }
        catch (IllegalArgumentException ee){
            System.out.println("Do nothing again");
        }
    }

    public void save() throws IOException {
        //File fileOut = new File("C:\\Users\\USERPC\\Desktop\\IMAGESSHIT\\"+filename);
        File fileOut = new File("./Images/"+filename);
        BufferedImage BI = SwingFXUtils.fromFXImage(upImage,null);
        ImageIO.write(BI,"png",fileOut);

    }
}
