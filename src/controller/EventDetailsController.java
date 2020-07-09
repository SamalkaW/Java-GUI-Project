package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Responders;
import model.exceptions.InvalidUserInput;
import model.exceptions.NegativeNumberException;
import model.exceptions.PostClosedException;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class EventDetailsController implements Initializable {

    @FXML private Label eventDescLabel;
    @FXML private Button uploadPicButton;
    @FXML private Button saveButton;
    @FXML private TableColumn respondersCol;
    @FXML private TableView respondersTable;
    @FXML private ImageView eventPicImg;
    @FXML private Label postIDLabel;
    @FXML private Label usernameLabel;
    @FXML private TextField eventNameTextfield;
    @FXML private TextField eventDescrTextfield;
    @FXML private TextField eventVenueTextfield;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField capacityTextfield;
    @FXML private Label eventNameLabel;
    @FXML private Label eventVenueLabel;
    @FXML private Label eventDateLabel;
    @FXML private Label eventCapacityLabel;
    @FXML private Label eventAtCountLabel;
    @FXML private Label eventStatusLabel;
    private String username;
    private String postID;
    private FileChooser imageChoose;
    private File filePath;
    private Image upImage;
    private String filename;
    ObservableList<Responders> responder = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        respondersCol.setCellValueFactory(new PropertyValueFactory<Responders,String>("responderID"));
        respondersTable.setItems(responder);
    }
//Shows the details of  the event
    private void showData() {
        final String DB_NAME = "testDB";
        final String EVENT_TABLE = "EVENT";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();){
            //System.out.println("connected");

            //System.out.println(postID);
            String query = "SELECT * FROM " + EVENT_TABLE+" WHERE postID ='"+postID+"'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while(resultSet.next()) {
                    //System.out.println("next");
                    String imageName = resultSet.getString("imageName");
                    Image image = new Image("file:Images/" + imageName);
                    String capacity = Integer.toString(resultSet.getInt("eventCapacity"));
                    String count = Integer.toString(resultSet.getInt("eventAttendCount"));
                    eventNameLabel.setText(resultSet.getString("title"));
                    eventDescLabel.setText(resultSet.getString("description"));
                    eventVenueLabel.setText(resultSet.getString("eventVenue"));
                    eventDateLabel.setText(resultSet.getString("eventDate"));
                    eventCapacityLabel.setText(capacity);
                    eventAtCountLabel.setText(count);
                    eventStatusLabel.setText(resultSet.getString("status"));
                    eventPicImg.setImage(image);

                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
//Close the Post when user needs.
    @FXML private void closePostClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Event Confirmation");
        alert.setHeaderText("Close the event?");
        Optional<ButtonType> result = alert.showAndWait();

        //If user confirms to close Event
        if(result.get()==ButtonType.OK) {
            try{
                String currentStatus = eventStatusLabel.getText();
                if(currentStatus.equals("CLOSED")){
                    throw new PostClosedException("Post is Already Closed. No need to Close again");
                }
                else {
                    final String DB_NAME = "testDB";
                    final String TABLE_NAME = "EVENT";
                    try (Connection con = ConnectionTest.getConnection(DB_NAME);
                         Statement stmt = con.createStatement();
                    ) {
                        String query = "UPDATE " + TABLE_NAME +
                                " SET status = 'CLOSED'" +
                                " WHERE postID LIKE '"+postID+"'";
                        int r = stmt.executeUpdate(query);
                        System.out.println("Update table " + TABLE_NAME + " executed successfully");
                        System.out.println(r + " row(s) affected");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    eventStatusLabel.setText("CLOSED");
                }
            }
           catch (PostClosedException e) {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Closed");
                alert1.setHeaderText("Close Event Error!!");
                alert1.setContentText(e.getMessage());
                alert1.showAndWait();

            }
            catch(Exception e){
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Closed");
                alert1.setHeaderText("Close Event Error!!");
                alert1.setContentText(e.getMessage());
                alert1.showAndWait();
            }
        }
        else {}
    }
//DELETE BOTH THE EVENT AND THE REPLIES ASSOCIATED.
    @FXML private void deletePostClicked(ActionEvent actionEvent) throws IOException {
        //System.out.println(username);
        //System.out.println(postID);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Event Confirmation");
        alert.setHeaderText("DELETE THE EVENT? SURE?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()==ButtonType.OK) {
            final String DB_NAME = "testDB";
            final String TABLE_NAME = "EVENT";
            final String REPLY_TABLE = "REPLY";
            try (Connection con = ConnectionTest.getConnection(DB_NAME);
                 Statement stmt = con.createStatement();
            ) {
                String query = "DELETE FROM " + TABLE_NAME +
                        " WHERE postID LIKE '"+postID+"'";
                String query2 = "DELETE FROM "+REPLY_TABLE+" WHERE postID LIKE '"+postID+"'";
                int r2 = stmt.executeUpdate(query);
                con.commit();
                int rr = stmt.executeUpdate(query2);
                con.commit();
                System.out.print("Delete from table " + TABLE_NAME + " executed successfully");
                System.out.println(r2 + " row(s) affected");
                System.out.print("Delete from table " + TABLE_NAME + " executed successfully");
                System.out.println(rr + " row(s) affected");

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
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

    @FXML private void backToMainClicked(ActionEvent actionEvent) throws IOException {
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

    @FXML private void saveChangedClicked(ActionEvent actionEvent)  {
        boolean allEmpty = true;
        try{
               // System.out.println(filename);
            if(filename!=null){
                String imageN = filename;
                editText(imageN,"imageName");
                save();
                allEmpty = false;
            }
            if(!capacityTextfield.getText().isEmpty()){
                if(!intParse(capacityTextfield.getText())){
                    throw new NumberFormatException("Event Capacity should be a whole number");
                }
                else if(!negativeParse(capacityTextfield.getText())){
                    throw new NegativeNumberException("Event Capacity should be Positive");
                }
                //EDIT THE CAPACITY IN DATABASE
                else{
                    int capa = Integer.parseInt(capacityTextfield.getText());
                    editCapacity(capa);
                    eventCapacityLabel.setText(""+capa);
                    allEmpty = false;
                }
            }
             if(!eventNameTextfield.getText().isEmpty()){
                //EDIT THE TITLE IN DB
                 editText(eventNameTextfield.getText(),"title");
                 eventNameLabel.setText(eventNameTextfield.getText());
                 allEmpty = false;

            }
             if(!eventDescrTextfield.getText().isEmpty()){
                //EDIT THE DESCRIPTION IN DB
                 editText(eventDescrTextfield.getText(),"description");
                 eventDescLabel.setText(eventDescrTextfield.getText());
                 allEmpty = false;

             }
             if(!eventVenueTextfield.getText().isEmpty()){
                //EDIT THE VENUE IN DB
                 editText(eventVenueTextfield.getText(),"eventVenue");
                 eventVenueLabel.setText(eventVenueTextfield.getText());
                 allEmpty = false;
             }
             if(!eventDatePicker.getValue().toString().isEmpty()){
                 //EDIT THE DATE IN DB
                 editText(eventDatePicker.getValue().toString(),"eventDate");
                 eventDateLabel.setText(eventDatePicker.getValue().toString());
                 allEmpty = false;
             }
             //If All the fields and upload button is not used. May or may not throw a null pointer
            if(allEmpty==true){
                throw new InvalidUserInput("You haven't edited anything");
            }
        }
        catch(NullPointerException e){
           //When testing sometimes it gives a Null Pointer error specially when user wants to edit one field
            //but others are empty//
            if(allEmpty==true){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save Changes Error!");
                alert.setContentText("All empty!");
                alert.showAndWait();
            }
        }
        catch(InvalidUserInput e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Changes Error!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Changes Error!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.out.println(e.getMessage());
        }
    }

    //EDIT ANY TEXTFIELD except capacity
    private void editText(String text,String column){
        final String DB_NAME = "testDB";
        final String TABLE_NAME = "EVENT";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();
        ) {
            String query = "UPDATE " + TABLE_NAME +
                    " SET "+column+" = '" +text+"'"+
                    " WHERE postID LIKE '"+postID+"'";
            int r = stmt.executeUpdate(query);
            System.out.println("Update table " + TABLE_NAME + " executed successfully");
            System.out.println(r + " row(s) affected");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

//Edit capacity in DB
    private void editCapacity(int capa){
        final String DB_NAME = "testDB";
        final String TABLE_NAME = "EVENT";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();
        ) {
            String query = "UPDATE " + TABLE_NAME +
                    " SET eventCapacity = " +capa+
                    " WHERE postID LIKE '"+postID+"'";
            int r = stmt.executeUpdate(query);
            System.out.println("Update table " + TABLE_NAME + " executed successfully");
            System.out.println(r + " row(s) affected");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private boolean intParse(String Last){
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

    @FXML private void uploadNewPicClicked(ActionEvent actionEvent) {
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        imageChoose = new FileChooser();
        imageChoose.setTitle("Choose a Pic");
        imageChoose.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG","*.png"),
                new FileChooser.ExtensionFilter("JPEG","*.jpg"));
        this.filePath = imageChoose.showOpenDialog(stage);

        try{
            BufferedImage bImage = ImageIO.read(filePath);
            upImage = SwingFXUtils.toFXImage(bImage, null);
            eventPicImg.setImage(upImage);
            filename = filePath.getName();
        } catch (IOException e ) {
            // e.printStackTrace();
            System.out.println("Do nothing");
        }
        catch (IllegalArgumentException ee){
            System.out.println("Do nothing again");
        }
    }
    @FXML
    public void setUsername(String user) {
         username = user;
        usernameLabel.setText(user);
    }

    @FXML
    public void setPostID(String p){
        postID = p;
        postIDLabel.setText(postID);
        showData();
        showResponders();
        //System.out.println("Responder list size:"+responder.size());
        if((responder.size()!=0)||(eventStatusLabel.getText().equals("CLOSED"))){
            saveButton.setDisable(true);
            eventNameTextfield.setDisable(true);
            eventDescrTextfield.setDisable(true);
            eventVenueTextfield.setDisable(true);
            eventDatePicker.setDisable(true);
            capacityTextfield.setDisable(true);
            uploadPicButton.setDisable(true);
        }
    }

    private void showResponders() {
        final String DB_NAME = "testDB";
        final String REPLY_TABLE = "REPLY";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();){
            //System.out.println("connected");
            //System.out.println(postID);
            String query = "SELECT * FROM " + REPLY_TABLE+" WHERE postID ='"+postID+"'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while(resultSet.next()) {
                    System.out.println("next");
                    Responders resOb = new Responders(postID,resultSet.getString("responderID"),1.0);
                    responder.add(resOb);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void save() throws IOException {
        //File fileOut = new File("C:\\Users\\USERPC\\Desktop\\IMAGESSHIT\\"+filename);
        File fileOut = new File("./Images/"+filename);
        BufferedImage BI = SwingFXUtils.fromFXImage(upImage,null);
        ImageIO.write(BI,"png",fileOut);

    }

}
