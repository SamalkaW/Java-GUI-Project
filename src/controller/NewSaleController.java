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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

public class NewSaleController implements Initializable {


    @FXML private ImageView uploadPhoto;
    @FXML private TextField saleNameTextField;
    @FXML private TextField saleDescriptionTextField;
    @FXML private TextField saleAskingTextField;
    @FXML private TextField saleMinRaiseTextField;

    private String username;
    private FileChooser imageChoose;
    private File filePath;
    private Image upImage;
    private String filename;

    public void setUsername(String username) {
        this.username = username;
    }
    @FXML private void submitButtonPressed(ActionEvent actionEvent) throws NegativeNumberException{
        String title = saleNameTextField.getText();
        String description = saleDescriptionTextField.getText();


        try {
            if (saleNameTextField.getText().isEmpty()) {
                throw new NullPointerException("Item Name shouldn't be Empty");
            } else if (saleDescriptionTextField.getText().isEmpty()) {
                throw new NullPointerException("Item Description shouldn't be Empty");
            } else if (saleAskingTextField.getText().isEmpty()) {
                throw new NullPointerException("Asking Price shouldn't be Empty");
            } else if (saleMinRaiseTextField.getText().isEmpty()) {
                throw new NullPointerException("Minimum Raise shouldn't be Empty");
            } else if (!doubleParse(saleAskingTextField.getText())) {
                throw new NumberFormatException("Asking Price should be a whole number");
            } else if (!negativeParse(saleAskingTextField.getText())) {
                throw new NegativeNumberException("Asking Price should be Positive");
            } else if (!doubleParse(saleMinRaiseTextField.getText())) {
                throw new NumberFormatException("Minimum Raise should be a number");
            } else if (!negativeParse(saleMinRaiseTextField.getText())) {
                throw new NegativeNumberException("Minimum Raise should be Positive");
            }else{
                //method to add the event to the database
                Submit(actionEvent);
            }

            }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void Submit(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("New Sale Confirmation");
        alert.setHeaderText("Create the Sale?");
        Optional<ButtonType>result = alert.showAndWait();
        //If user confirms to make  new Sale
        if(result.get()==ButtonType.OK) {
            addSaleToDB();
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
    private String newSaleID(){

        String LastID ="";
        final String DB_NAME = "testDB";
        final String SALE_TABLE = "SALE";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "SELECT * FROM " + SALE_TABLE+"  ORDER BY postID DESC LIMIT 1;";
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
        String newSaleID = "";
        String LastDig = LastID.substring(3);
        if(intPass(LastDig)){
            last = Integer.parseInt(LastDig);
            newIDLastDigi = last+1;
            newSaleID=String.format("SAL%03d",newIDLastDigi);
            System.out.println(newSaleID);
        }
        return newSaleID;
    }

    private void addSaleToDB() throws IOException {
        String SaleID = newSaleID();
        final String DB_NAME = "testDB";
        final String SALE_TABLE = "SALE";
        String imageN;
        if(filename!=null){
            imageN = filename;
           // System.out.println("if: "+imageN);
            save();

        }
        else{
            imageN = "defaultImage.jpg";
            //System.out.println("else: "+imageN);
        }
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {

            String query = "INSERT INTO "+SALE_TABLE+" VALUES ('"+SaleID+"','"+imageN+"','"+saleNameTextField.getText()+
                    "','"+saleDescriptionTextField.getText()+"','"+username+"','OPEN',0.0,'"
                    +Double.parseDouble(saleMinRaiseTextField.getText())+"',"+Double.parseDouble(saleAskingTextField.getText())+")";

            int result = stmt.executeUpdate(query);
            con.commit();
            System.out.println("Insert into table " + SALE_TABLE + " executed successfully "+result);

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

    @FXML private void cancelButtonPressed(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sale Registration Cancellation");
        alert.setHeaderText("Absolutely Sure you want to Cancel Registration & Go to Main?");
        Optional<ButtonType> result = alert.showAndWait();
        //If user confirms to cancel making new Event
        if(result.get()==ButtonType.OK) {
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

    @FXML private void uploadPhotoClicked(MouseEvent mouseEvent) {
        Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        imageChoose = new FileChooser();
        imageChoose.setTitle("Choose a Pic");
        this.filePath = imageChoose.showOpenDialog(stage);

        try{
            BufferedImage bImage = ImageIO.read(filePath);
            upImage = SwingFXUtils.toFXImage(bImage, null);
            uploadPhoto.setImage(upImage);
            filename = filePath.getName();

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException ee){
            //System.out.println("Do nothing again");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uploadPhoto.setImage(new Image("file:Images/uploadPhoto.png"));
    }

    public void save() throws IOException {
        //File fileOut = new File("C:\\Users\\USERPC\\Desktop\\IMAGESSHIT\\"+filename);
        File fileOut = new File("./Images/"+filename);
        BufferedImage BI = SwingFXUtils.fromFXImage(upImage,null);
        ImageIO.write(BI,"png",fileOut);
        //System.out.println("HERE IS THE PROBLEM: ");

    }
}
