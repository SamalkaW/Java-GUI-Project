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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Event;
import model.Job;
import model.Post;
import model.Status;
import model.hsql_db.ConnectionTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class ReplyEventController {


    @FXML
    private Label eventIDLabel;
    @FXML private Label usernameLabel;
    private String postID;
    private String username;

    ObservableList<Event> events = FXCollections.observableArrayList();

    //Yes to Joining the event
    @FXML private void YesClicked(ActionEvent actionEvent) throws IOException {

        final String DB_NAME = "testDB";
        final String REPLY_TABLE ="REPLY";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "INSERT INTO "+REPLY_TABLE+" VALUES ('"+postID+"','"+username+"',1.0)";
            int r = stmt.executeUpdate(query);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+r);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //UPDATE THE EVENT ATTENDEE COUNT BY 1
        int newAttend = newAttendCount(postID);
        updateEventAtCount(postID,newAttend);
        capExceed(postID);
        Alert Confirmed = new Alert(Alert.AlertType.INFORMATION);
        Confirmed.setTitle("Join Success");
        Confirmed.setHeaderText("Congrats! You successfully Joined");
        Confirmed.showAndWait();
        backTomain(actionEvent);
    }


    //Method finds whether capacity exceeds
    private void capExceed(String postid){
        final String DB_NAME = "testDB";
        final String EVENT_TABLE = "EVENT";
        int attendeeCount=0;
        int Capacity=0;
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();){
            String query = "SELECT * FROM " + EVENT_TABLE+" WHERE postID ='"+postid+"'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while(resultSet.next()) {
                    attendeeCount = resultSet.getInt("eventAttendCount");
                    Capacity = resultSet.getInt("eventCapacity");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if(attendeeCount>=Capacity){
            try (Connection con = ConnectionTest.getConnection(DB_NAME);
                 Statement stmt = con.createStatement();
            ) {
                String query2 = "UPDATE " + EVENT_TABLE + " SET status = 'CLOSED'" + " WHERE postID LIKE '"+postid+"'";
                int r = stmt.executeUpdate(query2);
                System.out.println("Update table " + EVENT_TABLE + " executed successfully");
                System.out.println(r + " row(s) affected");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //No clicked
    @FXML private void NoClicked(ActionEvent actionEvent) throws IOException {
       backTomain(actionEvent);
    }

    //Going back to main screen
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

    //Setting details gotten from the Main Screen
    public void setDetails(String postID, String username) {
      this.postID=postID;
      this.username=username;
      eventIDLabel.setText(postID);
      usernameLabel.setText(username);
      LoadEvent();
    }

    //Get the current attendee count in the Event and add 1
    private int newAttendCount(String postid){
        final String DB_NAME = "testDB";
        final String EVENT_TABLE = "EVENT";
        int oldcount;
        int newCount = 0;
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();){
            System.out.println(postid);
            String query = "SELECT * FROM " + EVENT_TABLE+" WHERE postID ='"+postid+"'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while(resultSet.next()) {
                    oldcount = resultSet.getInt("eventAttendCount");
                    newCount = oldcount+1;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return newCount;
    }
//Update the event attenddee count in DB
    private void updateEventAtCount(String postid,int newAttend){
        final String DB_NAME = "testDB";
        final String TABLE_NAME = "EVENT";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();
        ) {
            String query = "UPDATE " + TABLE_NAME + " SET eventAttendCount = " +newAttend+ " WHERE postID LIKE '"+postid+"'";
            int r = stmt.executeUpdate(query);
            System.out.println("Update table " + TABLE_NAME + " executed successfully");
            System.out.println(r + " row(s) affected");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //Load Event Posts from the Database
    private void LoadEvent() {
        final String DB_NAME = "testDB";
        final String EVENT_TABLE = "EVENT";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "SELECT * FROM " + EVENT_TABLE;
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    String date1 = resultSet.getString("eventDate");
                    LocalDate eventDate = LocalDate.parse(date1);
                    String imageName = resultSet.getString("imageName");
                    Image image = new Image("file:Images/" + imageName, 70, 70, false, false);
                    String stat = resultSet.getString("status");
                    Status status = Status.valueOf(stat);
                    Post evOb = new Event(resultSet.getString("postID"), image, resultSet.getString("title"),
                            resultSet.getString("description"), resultSet.getString("creatorID"),
                            status, resultSet.getString("eventVenue"), eventDate, resultSet.getInt("eventCapacity"),
                            resultSet.getInt("eventAttendCount")); // Creating a new object
                    // Adding it to the list
                    events.add((Event) evOb);

                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}


