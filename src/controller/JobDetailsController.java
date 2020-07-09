package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

public class JobDetailsController implements Initializable {
    @FXML private Button uploadButton;
    @FXML private Button saveButton;
    @FXML private TableView JobTableview;
    @FXML private TableColumn responderCol;
    @FXML private TableColumn offerCol;
    @FXML private Label usernameLabel;
    @FXML private Label jobIDLabel;
    @FXML private ImageView upNewPic;
    @FXML private TextField jobtitleTextfield;
    @FXML private TextField jobDescTextfielld;
    @FXML private TextField jobPropTextfield;
    @FXML private  Label jobTitleLabel;
    @FXML private Label jobDescLabel;
    @FXML private Label jobPropLabel;
    @FXML private Label jobLowOfferLabel;
    @FXML private Label jobStatusLabel;

    private String username;
    private String postID;
    private FileChooser imageChoose;
    private File filePath;
    private Image upImage;
    private String filename;
    ObservableList<Responders> responder = FXCollections.observableArrayList();

    @FXML private void uploadPicButtonClicked(ActionEvent actionEvent) {

        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        imageChoose = new FileChooser();
        imageChoose.setTitle("Choose a Pic");
        imageChoose.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG","*.png"),
                new FileChooser.ExtensionFilter("JPEG","*.jpg"));
        this.filePath = imageChoose.showOpenDialog(stage);

        try{
            BufferedImage bImage = ImageIO.read(filePath);
            upImage = SwingFXUtils.toFXImage(bImage, null);
            upNewPic.setImage(upImage);
            filename = filePath.getName();
        } catch (IOException e ) {
            // e.printStackTrace();
            //System.out.println("Do nothing");
        }
        //Happens when user clicks the upload picture button but changes mind and cancels uploading
        catch (IllegalArgumentException ee){
            // System.out.println("Do nothing again");
        }

    }
    private void save() throws IOException {
        //File fileOut = new File("C:\\Users\\USERPC\\Desktop\\IMAGES\\"+filename);
        File fileOut = new File("./Images/"+filename);
        BufferedImage BI = SwingFXUtils.fromFXImage(upImage,null);
        ImageIO.write(BI,"png",fileOut);

    }

    @FXML private void closePostButtonClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Job Confirmation");
        alert.setHeaderText("Close the Job?");
        Optional<ButtonType> result = alert.showAndWait();

        //If user confirms to close Event
        if(result.get()==ButtonType.OK) {
            try{
                String currentStatus = jobStatusLabel.getText();
                if(currentStatus.equals("CLOSED")){
                    throw new PostClosedException("Post is Already Closed. No need to Close again");
                }
                else {
                    final String DB_NAME = "testDB";
                    final String TABLE_NAME = "JOB";
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
                    jobStatusLabel.setText("CLOSED");
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

    @FXML private void saveChangesClicked(ActionEvent actionEvent) {
        boolean allEmpty = true;
        try{
            System.out.println(filename);
            if(filename!=null){
                String imageN = filename;
                editText(imageN,"imageName");
                save();
                allEmpty = false;
            }
            if(!jobPropTextfield.getText().isEmpty()){
                if(!doubleParse(jobPropTextfield.getText())){
                    throw new NumberFormatException("Proposed Price should be a number");
                }
                else if(!negativeParse(jobPropTextfield.getText())){
                    throw new NegativeNumberException("Proposed Price should be Positive");
                }
                //EDIT THE Proposed  price IN DATABASE
                else{
                    double prop = Double.parseDouble(jobPropTextfield.getText());
                    editValues(prop,"jobProposed");
                    jobPropLabel.setText(""+prop);
                    allEmpty = false;
                }
            }
            if(!jobtitleTextfield.getText().isEmpty()){
                //EDIT THE TITLE IN DB
                editText(jobtitleTextfield.getText(),"title");
                jobTitleLabel.setText(jobtitleTextfield.getText());
                allEmpty = false;
            }
            if(!jobDescTextfielld.getText().isEmpty()){
                //EDIT THE DESCRIPTION IN DB
                editText(jobDescTextfielld.getText(),"description");
                jobDescLabel.setText(jobDescTextfielld.getText());
                allEmpty = false;
            }
            if(allEmpty==true){
                throw new InvalidUserInput("You haven't edited Anything!");
            }
        }
        catch(InvalidUserInput e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Changes Error!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.out.println(e.getMessage());
        }
        catch(NullPointerException e){
            //dont do anything as user might edit only 1 field and maybe not all or only update pic.
            //In that case the other blank textfields might throw a null exception
            if(allEmpty==true){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save Changes Error!");
                alert.setContentText("All empty! You didn't edit anything");
                alert.showAndWait();
            }
        }

        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Changes Error!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.out.println(e.getMessage());
        }
    }

    //Method to see whether passed double is negative
    private boolean negativeParse(String num){
        String str = num;
        if(doubleParse(str)){
            double x = Double.parseDouble(str);
            if(x<0){
                return false;
            }
            else return true;
        }
        else{
            return false;
        }
    }
    //Method to convert a String to Double or whether it contains a double value
    private boolean doubleParse(String num) {
        String str = num;
        try{
            double x = Double.parseDouble(str);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    //for double type columns
    private void editValues(double value,String colname){
        final String DB_NAME = "testDB";
        final String TABLE_NAME = "JOB";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();
        ) {
            String query = "UPDATE " + TABLE_NAME +
                    " SET "+colname+" = " +value+" WHERE postID LIKE '"+postID+"'";
            int r = stmt.executeUpdate(query);
            System.out.println("Update table " + TABLE_NAME + " executed successfully");
            System.out.println(r + " row(s) affected");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void editText(String text,String column){
        final String DB_NAME = "testDB";
        final String TABLE_NAME = "JOB";
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

    @FXML private void deletePostButtonClicked(ActionEvent actionEvent) throws IOException {
       // System.out.println(username);
       // System.out.println(postID);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Job Post Confirmation");
        alert.setHeaderText("DELETE THE JOB POST? SURE?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()==ButtonType.OK) {
            final String DB_NAME = "testDB";
            final String TABLE_NAME = "JOB";
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
        MainWindowController mainWindowController = loader.getController();
        //Passing the username to the Main Window
        mainWindowController.setUsername(username);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setTitle("Main Window");
        window.setScene(main);
        window.centerOnScreen();
        window.show();
    }

    public void setJob(String username, String postID){

        this.username = username;
        this.postID = postID;
        jobIDLabel.setText(postID);
        usernameLabel.setText(username);
        showData();
        showResponders();
        if((responder.size()!=0)||(jobStatusLabel.getText().equals("CLOSED"))){
            saveButton.setDisable(true);
            jobtitleTextfield.setDisable(true);
            jobDescTextfielld.setDisable(true);
            jobPropTextfield.setDisable(true);
            uploadButton.setDisable(true);
        }

    }
    private void showData() {
        final String DB_NAME = "testDB";
        final String JOB_TABLE = "JOB";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();){
            String query = "SELECT * FROM " + JOB_TABLE+" WHERE postID ='"+postID+"'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while(resultSet.next()) {

                    String imageName = resultSet.getString("imageName");
                    Image image = new Image("file:Images/" + imageName);

                    String proposed = Double.toString(resultSet.getDouble("jobProposed"));
                    String low = Double.toString(resultSet.getDouble("jobLowest"));

                    jobTitleLabel.setText(resultSet.getString("title"));
                    jobDescLabel.setText(resultSet.getString("description"));
                    jobPropLabel.setText(proposed);
                    jobLowOfferLabel.setText(low);
                    jobStatusLabel.setText(resultSet.getString("status"));
                    upNewPic.setImage(image);

                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void showResponders() {
        final String DB_NAME = "testDB";
        final String REPLY_TABLE = "REPLY";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();){
           // System.out.println("connected");
           // System.out.println(postID);
            String query = "SELECT * FROM " + REPLY_TABLE+" WHERE postID ='"+postID+"'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while(resultSet.next()) {
                    System.out.println("next");
                    Responders resOb = new Responders(postID,resultSet.getString("responderID"),resultSet.getDouble("value"));
                    responder.add(resOb);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        responderCol.setCellValueFactory(new PropertyValueFactory<Responders,String>("responderID"));
        offerCol.setCellValueFactory(new PropertyValueFactory<Responders,Double>("value"));
        JobTableview.setItems(responder);
        offerCol.setSortType(TableColumn.SortType.ASCENDING);
        JobTableview.getSortOrder().add(offerCol);
        JobTableview.sort();
    }
}
