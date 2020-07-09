package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Job;
import model.Post;
import model.Sale;
import model.Status;
import model.exceptions.InvalidOfferPrice;
import model.exceptions.NegativeNumberException;
import model.hsql_db.ConnectionTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReplyJobController {
    @FXML private Label postIDlabel;
    @FXML private Label usernameLabel;
    @FXML private Label jobTitleLabel;
    @FXML private Label jobPropLabel;
    @FXML private Label jobLowestLabel;
    @FXML private TextField offerTextfield;
    private String postID;
    private String username;
    private double lowest;
    private double proposed;

    ObservableList<Job> jobs = FXCollections.observableArrayList();

//info from previous screen(main)
     public void setDetails(String postID, String username) {
        this.username = username;
        this.postID = postID;
        postIDlabel.setText(postID);
        usernameLabel.setText(username);
        loadJob();
        for(Job j: jobs){
            if(j.getPostID().equals(postID)){
                jobTitleLabel.setText(j.getTitle()) ;
                String low = Double.toString(j.getLowest());
                jobLowestLabel.setText(low);
                String prop = Double.toString(j.getProposed());
                jobPropLabel.setText(prop);
            }
        }
    }
    //Load the Jobs in the Database
    private void loadJob(){
        final String DB_NAME = "testDB";
        final String JOB_TABLE = "JOB";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "SELECT * FROM " + JOB_TABLE;
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    String imageName = resultSet.getString("imageName");
                    Image image = new Image("file:Images/" + imageName, 70, 70, false, false);
                    String stat = resultSet.getString("status");
                    Status status = Status.valueOf(stat);
                    Post jobOb = new Job(resultSet.getString("postID"), image, resultSet.getString("title"),
                            resultSet.getString("description"), resultSet.getString("creatorID"), status,
                            resultSet.getDouble("jobProposed"), resultSet.getDouble("jobLowest"));

                    jobs.add((Job) jobOb);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    //Check if value is lower than the current lowest or if 0 lower than the proposed
    //ADD TO REPLY TABLE AND GIVE A CONFIRMATION AND BACK TO MAIN
    @FXML private void submitClicked(ActionEvent actionEvent) {
        try{
            if(offerTextfield.getText().isEmpty()){
                throw new NullPointerException("Offer cannot be Empty");
            }
            if(!offerTextfield.getText().isEmpty()){
                if(!doubleParse(offerTextfield.getText())){
                    throw new NumberFormatException("Your offer should be a number");
                }
                else if(!negativeParse(offerTextfield.getText())){
                    throw new NegativeNumberException("Offer should be Positive");
                }
                else{
                    for(Job j: jobs){
                        if(j.getPostID().equals(postID)){
                            lowest= j.getLowest();
                            proposed = j.getProposed();
                            break;
                        }
                    }
                    double offer = Double.parseDouble(offerTextfield.getText());
                    if(lowest ==0){
                        if(offer>=proposed){
                            throw new InvalidOfferPrice("Offer should be lower than Proposed Price");
                        }
                        else {
                            addToReply(offer);
                            Alert Confirmed = new Alert(Alert.AlertType.INFORMATION);
                            Confirmed.setTitle("Reply Success");
                            Confirmed.setHeaderText("Thanks for the offer!");
                            Confirmed.showAndWait();
                            backTomain(actionEvent);
                        }
                    }
                    else if(lowest!=0){
                        if(offer>=lowest||offer>=proposed){
                            throw new InvalidOfferPrice("Offer should be lower than current Lowest and Proposed Price");
                        }
                        if(offer<lowest&&offer<proposed){
                            addToReply(offer);
                            Alert Confirmed = new Alert(Alert.AlertType.INFORMATION);
                            Confirmed.setTitle("Reply Success");
                            Confirmed.setHeaderText("Thanks for the offer!");
                            Confirmed.showAndWait();
                            backTomain(actionEvent);
                        }
                        else{}
                    }
                }
            }
        }
        catch(InvalidOfferPrice i){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid offer Error!!");
            alert.setContentText(i.getMessage());
            alert.showAndWait();
        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Offer Error!!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
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

    //Back to main clicked
    @FXML private void backToMainClicked(ActionEvent actionEvent) throws IOException {
        backTomain(actionEvent);
    }
//Either back to main clicked or was a successful reply both takes back to the Main screen
    private void backTomain(ActionEvent actionEvent) throws IOException {
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

    //Add offer to replies and update the current Lowest offer.
    private void addToReply(double offer){
        final String DB_NAME = "testDB";
        final String REPLY_TABLE ="REPLY";
        final String JOB_TABLE ="JOB";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "INSERT INTO "+REPLY_TABLE+" VALUES ('"+postID+"','"+username+"', "+offer+")";
            int r = stmt.executeUpdate(query);
            con.commit();
            String query2 = "UPDATE " + JOB_TABLE + " SET jobLowest = "+offer+ " WHERE postID LIKE '"+postID+"'";
            int r3 = stmt.executeUpdate(query2);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+r);
            System.out.println("Insert into table " + JOB_TABLE + " executed successfully "+r3);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
