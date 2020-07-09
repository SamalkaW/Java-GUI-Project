package model.hsql_db;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import model.Event;
import model.Post;
import model.Responders;
import model.Status;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

public class ReadDatabase {

    List<Post> postsArrayListDB = new ArrayList<>();


    public void readEvent() {
        final String DB_NAME = "testDB";
        final String EVENT_TABLE = "EVENT";

        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "SELECT * FROM " + EVENT_TABLE;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.printf("Post ID : %s | Image Name: %s | Title: %s | Description: %s | CreatorID: %s| Status: %s |" +
                                    "\n Venue: %s | Date: %s | EventCap: %d | AttCount: %d | \n",
                            resultSet.getString("postID"), resultSet.getString("imageName"),
                            resultSet.getString("title"), resultSet.getString("description"),
                            resultSet.getString("creatorID"), resultSet.getString("status"),
                            resultSet.getString("eventVenue"), resultSet.getString("eventDate"),
                            resultSet.getInt("eventCapacity"), resultSet.getInt("eventAttendCount"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void readSale() {
        final String DB_NAME = "testDB";
        final String SALE_TABLE = "SALE";

        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "SELECT * FROM " + SALE_TABLE;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.printf("Post ID : %s | Image Name: %s | Title: %s | Description: %s | CreatorID: %s| Status: %s |" +
                                    "\n HighestOffer: %.2f | Min Raise: %.2f | Asking Price: %.2f | \n",
                            resultSet.getString("postID"), resultSet.getString("imageName"),
                            resultSet.getString("title"), resultSet.getString("description"),
                            resultSet.getString("creatorID"), resultSet.getString("status"),
                            resultSet.getDouble("saleHighOffer"), resultSet.getDouble("saleMinRaise"),
                            resultSet.getDouble("saleAskingPrice"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void readJob() {
        final String DB_NAME = "testDB";
        final String JOB_TABLE = "JOB";

        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "SELECT * FROM " + JOB_TABLE;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.printf("Post ID : %s | Image Name: %s | Title: %s | Description: %s | CreatorID: %s| Status: %s |" +
                                    "\n Proposed: %.2f | Lowest: %.2f | \n",
                            resultSet.getString("postID"), resultSet.getString("imageName"),
                            resultSet.getString("title"), resultSet.getString("description"),
                            resultSet.getString("creatorID"), resultSet.getString("status"),
                            resultSet.getDouble("jobProposed"), resultSet.getDouble("jobLowest"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void readReply() {
        final String DB_NAME = "testDB";
        final String REPLY_TABLE = "REPLY";

        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "SELECT * FROM " + REPLY_TABLE;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.printf("Post ID : %s | Responder: %s " +
                                    " Value: %.2f \n",
                            resultSet.getString("postID"), resultSet.getString("responderID"),
                            resultSet.getDouble("value"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public int countQuery(String tableName) {
        final String DB_NAME = "testDB";
        final String TABLE_NAME = tableName;
        int count = 0;
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "SELECT count(*) FROM " + TABLE_NAME;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                resultSet.next();
                count = resultSet.getInt(1);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return count;
    }

    public ObservableList<String> responders(String postID) {
        final String DB_NAME = "testDB";
        final String REPLY_TABLE = "REPLY";
        ObservableList res = FXCollections.observableArrayList();
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();){
            System.out.println("connected");
            System.out.println(postID);
            String query = "SELECT * FROM " + REPLY_TABLE+" WHERE postID ='"+postID+"'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while(resultSet.next()) {
                    System.out.println("next");

                    res.add(resultSet.getString("responderID"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

}