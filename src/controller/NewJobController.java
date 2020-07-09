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
import java.util.Optional;
import java.util.ResourceBundle;

public class NewJobController implements Initializable {
    @FXML private ImageView uploadPhoto;
    @FXML private TextField jobNameTextField;
    @FXML private TextField JobDescriptionTextField;
    @FXML private TextField JobProposedTextField;
    private String username;
    private FileChooser imageChoose;
    private File filePath;
    private String filename;
    private Image upImage;


    public void setUsername(String username) {
        this.username = username;
    }


    @FXML private void uploadImagClicked(MouseEvent mouseEvent) {
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
    }

    @FXML private void cancelButtonPressed(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Job Registration Cancellation");
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

    //Do this when the submit button is pressed, Handle possible exceptions, if not make the Job object
    @FXML private void submitButtonPressed(ActionEvent actionEvent) throws NegativeNumberException {
        try {
            if (jobNameTextField.getText().isEmpty()) {
                throw new NullPointerException("Job Name shouldn't be Empty");
            } else if (JobDescriptionTextField.getText().isEmpty()) {
                throw new NullPointerException("Item Description shouldn't be Empty");
            } else if (JobProposedTextField.getText().isEmpty()) {
                throw new NullPointerException("Job Proposed Price shouldn't be Empty");
            } else if (!doubleParse(JobProposedTextField.getText())) {
                throw new NumberFormatException("Job Proposed price should be a number");
            } else if (!negativeParse(JobProposedTextField.getText())) {
                throw new NegativeNumberException("Job proposed Price should be Positive");
            }
            else {
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
        alert.setTitle("New Job Confirmation");
        alert.setHeaderText("Create the Job Post?");
        Optional<ButtonType>result = alert.showAndWait();
        //If user confirms to make  new Sale
        if(result.get()==ButtonType.OK) {
            addJobToDB();
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
    private void addJobToDB() throws IOException {
        String JobID = newJobID();
        final String DB_NAME = "testDB";
        final String JOB_TABLE = "JOB";
        String imageN;
        if(filename!=null){
            imageN = filename;
            //System.out.println("if: "+imageN);
            save();

        }
        else{
            imageN = "defaultImage.jpg";
            //System.out.println("else: "+imageN);
        }
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "INSERT INTO "+JOB_TABLE+" VALUES ('"+JobID+"','"+imageN+"','"+jobNameTextField.getText()+
                    "','"+JobDescriptionTextField.getText()+"','"+username+"','OPEN',"
                    +Double.parseDouble(JobProposedTextField.getText())+",0.0)";

            int result = stmt.executeUpdate(query);
            con.commit();
            System.out.println("Insert into table " + JOB_TABLE + " executed successfully "+result);

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
    //JOB ID passing to get number
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uploadPhoto.setImage(new Image("file:Images/uploadPhoto.png"));
    }

    public void save() throws IOException {
        //File fileOut = new File("C:\\Users\\USERPC\\Desktop\\IMAGES\\"+filename);
        File fileOut = new File("./Images/"+filename);
        BufferedImage BI = SwingFXUtils.fromFXImage(upImage,null);
        ImageIO.write(BI,"png",fileOut);

    }
    private String newJobID(){
        String LastID ="";
        final String DB_NAME = "testDB";
        final String JOB_TABLE = "JOB";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();) {
            String query = "SELECT * FROM " + JOB_TABLE+"  ORDER BY postID DESC LIMIT 1;";
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
        String newJobid = "";
        String LastDig = LastID.substring(3);
        if(intPass(LastDig)){
            last = Integer.parseInt(LastDig);
            newIDLastDigi = last+1;
            newJobid=String.format("JOB%03d",newIDLastDigi);
            System.out.println(newJobid);
        }
        return newJobid;
    }
}
