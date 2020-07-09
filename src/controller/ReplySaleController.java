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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
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
import java.util.Optional;

public class ReplySaleController {


    @FXML private Label postIDLabel;
    @FXML private Label saleNameLabel;
    @FXML private Label saleHighLabel;
    @FXML private Label saleMinLabel;
    @FXML private TextField yourOfferTextfield;
    @FXML private Label usernameLabel;
    private String postID;
    private String username;
    private double askingP;
    private double minRaise;
    private double highest;

    ObservableList<Sale> sales = FXCollections.observableArrayList();

    //When user clicks submit to offer
    @FXML private void submitOfferClicked(ActionEvent actionEvent) {
        try{
            if(yourOfferTextfield.getText().isEmpty()){
                throw new NullPointerException("Offer cannot be Empty");
            }
            if(!yourOfferTextfield.getText().isEmpty()){
                if(!doubleParse(yourOfferTextfield.getText())){
                    throw new NumberFormatException("Offer should be a number");
                }
                else if(!negativeParse(yourOfferTextfield.getText())){
                    throw new NegativeNumberException("Offer should be Positive");
                }
                //Check if value is higher than the current highest + min  raise
                //ADD TO REPLY TABLE AND GIVE A CONFIRMATION AND BACK TO MAIN
                else{
                    for(Sale s: sales){
                        if(s.getPostID().equals(postID)){
                            highest= s.getHighestOffer();
                            minRaise =s.getMinRaise();
                            askingP =s.getAskingPrice();
                            break;
                        }
                    }
                    double offer = Double.parseDouble(yourOfferTextfield.getText());
                    if((offer>(highest+minRaise))&&(offer<askingP)){
                        addToReply(offer);
                        Alert Confirmed = new Alert(Alert.AlertType.INFORMATION);
                        Confirmed.setTitle("Reply Success");
                        Confirmed.setHeaderText("Thanks for the offer, but item still on Sale!!!!");
                        Confirmed.showAndWait();
                        backTomain(actionEvent);
                    }
                    if((offer>(highest+minRaise))&&(offer>=askingP)){
                        addToReply(offer);
                        closeTheSale();
                        Alert Confirmed = new Alert(Alert.AlertType.INFORMATION);
                        Confirmed.setTitle("Reply Success");
                        Confirmed.setHeaderText("Thanks for the offer it's all yours!!!!!");
                        Confirmed.showAndWait();
                        backTomain(actionEvent);
                    }
                    if(offer <=(highest+minRaise)){
                        throw new InvalidOfferPrice("Offer price should be greater than Minimum Raise + Current Highest");
                    }
                }
            }
        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Offer Error!!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    //Close the sale if the current offer exceeds the Asking P
    private void closeTheSale(){
        final String DB_NAME = "testDB";
        final String SALE_TABLE = "SALE";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();
        ) {
            String query2 = "UPDATE " + SALE_TABLE + " SET status = 'CLOSED'" + " WHERE postID LIKE '"+postID+"'";
            int r = stmt.executeUpdate(query2);
            con.commit();
            System.out.println("Update table " + SALE_TABLE + " executed successfully");
            System.out.println(r + " row(s) affected");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //Method that is used to set details obtained from the Main
    public void setDetails(String postID, String username){
        this.username = username;
        this.postID = postID;
        postIDLabel.setText(postID);
        usernameLabel.setText(username);
        loadSale();
        for(Sale s: sales){
            if(s.getPostID().equals(postID)){
                saleNameLabel.setText(s.getTitle()) ;
                String high = Double.toString(s.getHighestOffer());
                saleHighLabel.setText(high);
                String min = Double.toString(s.getMinRaise());
                saleMinLabel.setText(min);
            }
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

    //Loading Sales
    private void loadSale(){
        final String DB_NAME = "testDB";
        final String SALE_TABLE = "SALE";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "SELECT * FROM " + SALE_TABLE;
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    String imageName = resultSet.getString("imageName");
                    Image image = new Image("file:Images/" + imageName, 70, 70, false, false);
                    String stat = resultSet.getString("status");
                    Status status = Status.valueOf(stat);
                    Post salOb = new Sale(resultSet.getString("postID"), image, resultSet.getString("title"),
                            resultSet.getString("description"), resultSet.getString("creatorID"),
                            status, resultSet.getDouble("saleHighOffer"), resultSet.getDouble("saleMinRaise"),
                            resultSet.getDouble("saleAskingPrice"));
                    // Adding it to the list
                    sales.add((Sale) salOb);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //When User clicks cancel button
    @FXML private void cancelPressed(ActionEvent actionEvent) throws IOException {
            backTomain(actionEvent);

    }

    //Back to the main screen
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

    //Add offer to replies and update the current Highest offer.
    private void addToReply(double offer){
        final String DB_NAME = "testDB";
        final String REPLY_TABLE ="REPLY";
        final String SALE_TABLE ="SALE";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "INSERT INTO "+REPLY_TABLE+" VALUES ('"+postID+"','"+username+"', "+offer+")";
            int r = stmt.executeUpdate(query);
            con.commit();
            String query2 = "UPDATE " + SALE_TABLE + " SET saleHighOffer = "+offer+ " WHERE postID LIKE '"+postID+"'";
            int r3 = stmt.executeUpdate(query2);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+r);
            System.out.println("Insert into table " + SALE_TABLE + " executed successfully "+r3);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
