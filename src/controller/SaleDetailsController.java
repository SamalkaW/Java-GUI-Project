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

public class SaleDetailsController implements Initializable {

    @FXML private Label userLabel;
    @FXML private Label saleIDlabel;
    @FXML private ImageView salePic;
    @FXML private Label saleTitleLabel;
    @FXML private Label saleDesLabel;
    @FXML private Label saleAskLabel;
    @FXML private Label saleMinLabel;
    @FXML private Label saleHighLabel;
    @FXML private Label saleStatLabel;
    @FXML private TextField saleDescTextfield;
    @FXML private TextField saleTitleTextField;
    @FXML private TextField saleAskTexfield;
    @FXML private TextField saleMinTextfield;
    @FXML private TableView saleTableview;
    @FXML private TableColumn responderCol;
    @FXML private TableColumn offerCol;
    @FXML private Button saveButton;
    @FXML private Button uploadNewPic;

    private String username;
    private String postID;
    private FileChooser imageChoose;
    private File filePath;
    private Image upImage;
    private String filename;
    ObservableList<Responders> responder = FXCollections.observableArrayList();


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
            salePic.setImage(upImage);
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

    @FXML private void closePostClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Sale Confirmation");
        alert.setHeaderText("Close the Sale?");
        Optional<ButtonType> result = alert.showAndWait();

        //If user confirms to close Event
        if(result.get()==ButtonType.OK) {
            try{
                String currentStatus = saleStatLabel.getText();
                if(currentStatus.equals("CLOSED")){
                    throw new PostClosedException("Post is Already Closed. No need to Close again");
                }
                else {
                    final String DB_NAME = "testDB";
                    final String TABLE_NAME = "SALE";
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
                    saleStatLabel.setText("CLOSED");
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

    @FXML private void deletePostClicked(ActionEvent actionEvent) throws IOException {
        //System.out.println(username);
       // System.out.println(postID);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Sale Post Confirmation");
        alert.setHeaderText("DELETE THE SALE POST? SURE?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()==ButtonType.OK) {
            final String DB_NAME = "testDB";
            final String TABLE_NAME = "SALE";
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

    @FXML private void saveChangesClicked(ActionEvent actionEvent) {
        boolean allEmpty = true;
        try{
          //  System.out.println(filename);
            if(filename!=null){
                String imageN = filename;
                editText(imageN,"imageName");
                save();
                allEmpty = false;
            }
            if(!saleAskTexfield.getText().isEmpty()){
                if(!doubleParse(saleAskTexfield.getText())){
                    throw new NumberFormatException("Asking Price should be a number"); }
                else if(!negativeParse(saleAskTexfield.getText())){
                    throw new NegativeNumberException("Asking Price should be Positive"); }
                else{//EDIT THE Asking  price IN DATABASE
                    double ask = Double.parseDouble(saleAskTexfield.getText());
                    editValues(ask,"saleAskingPrice");
                    saleAskLabel.setText(""+ask);
                    allEmpty = false; }
            }
            if(!saleMinTextfield.getText().isEmpty()){
                if(!doubleParse(saleMinTextfield.getText())){
                    throw new NumberFormatException("Asking Price should be a number"); }
                else if(!negativeParse(saleMinTextfield.getText())){
                    throw new NegativeNumberException("Asking Price should be Positive"); }
                else{//EDIT THE Min raise   IN DATABASE
                    double min = Double.parseDouble(saleMinTextfield.getText());
                    editValues(min,"saleMinRaise");
                    saleMinLabel.setText(""+min);
                    allEmpty = false; }
            }
            if(!saleTitleTextField.getText().isEmpty()){//EDIT THE TITLE IN DB
                editText(saleTitleTextField.getText(),"title");
                saleTitleLabel.setText(saleTitleTextField.getText());
                allEmpty = false; }
            if(!saleDescTextfield.getText().isEmpty()){ //EDIT THE DESCRIPTION IN DB
                editText(saleDescTextfield.getText(),"description");
                saleDesLabel.setText(saleDescTextfield.getText());
                allEmpty = false; }
            if(allEmpty==true){
               throw new InvalidUserInput("You haven't edited Anything!"); }
        }
        catch(InvalidUserInput e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Changes Error!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            //System.out.println(e.getMessage());
        }
        catch(NullPointerException e){
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
            //System.out.println(e.getMessage());
        }
    }


//for double type columns
    private void editValues(double value,String colname){
            final String DB_NAME = "testDB";
            final String TABLE_NAME = "SALE";
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

    private void editText(String text,String column){
        final String DB_NAME = "testDB";
        final String TABLE_NAME = "SALE";
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

    @FXML private void backMain(ActionEvent actionEvent) throws IOException {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        responderCol.setCellValueFactory(new PropertyValueFactory<Responders,String>("responderID"));
        offerCol.setCellValueFactory(new PropertyValueFactory<Responders,Double>("value"));
        saleTableview.setItems(responder);
        offerCol.setSortType(TableColumn.SortType.DESCENDING);
        saleTableview.getSortOrder().add(offerCol);
        saleTableview.sort();
    }
    @FXML
    public void setUsername(String user) {
        username = user;
        userLabel.setText(user);
    }

    @FXML
    public void setPostID(String p){
        postID = p;
        saleIDlabel.setText(postID);
        showData();
        showResponders();
       // System.out.println("Responder list size:"+responder.size());
        if((responder.size()!=0)||(saleStatLabel.getText().equals("CLOSED"))){
            saveButton.setDisable(true);
            saleTitleTextField.setDisable(true);
            saleDescTextfield.setDisable(true);
            saleAskTexfield.setDisable(true);
            saleMinTextfield.setDisable(true);
            uploadNewPic.setDisable(true);
        }
    }
    //Shows the details of  the Sale
    private void showData() {
        final String DB_NAME = "testDB";
        final String SALE_TABLE = "SALE";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();){
            String query = "SELECT * FROM " + SALE_TABLE+" WHERE postID ='"+postID+"'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while(resultSet.next()) {

                    String imageName = resultSet.getString("imageName");
                    Image image = new Image("file:Images/" + imageName);

                    String highest = Double.toString(resultSet.getDouble("saleHighOffer"));
                    String min = Double.toString(resultSet.getDouble("saleMinRaise"));
                    String ask = Double.toString(resultSet.getDouble("saleAskingPrice"));

                    saleTitleLabel.setText(resultSet.getString("title"));
                    saleDesLabel.setText(resultSet.getString("description"));
                    saleHighLabel.setText(highest);
                    saleMinLabel.setText(min);
                    saleAskLabel.setText(ask);
                    saleStatLabel.setText(resultSet.getString("status"));
                    salePic.setImage(image);

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
          //  System.out.println(postID);
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


    private void save() throws IOException {
        //File fileOut = new File("C:\\Users\\USERPC\\Desktop\\IMAGES\\"+filename);
        File fileOut = new File("./Images/"+filename);
        BufferedImage BI = SwingFXUtils.fromFXImage(upImage,null);
        ImageIO.write(BI,"png",fileOut);

    }


}
