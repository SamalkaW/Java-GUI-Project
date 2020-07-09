package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
import model.hsql_db.ConnectionTest;
import model.hsql_db.ReadDatabase;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML private AnchorPane anchorPaneID;
    ReadDatabase r = new ReadDatabase();
    @FXML
    private ComboBox typeCombo;
    @FXML
    private ComboBox statusCombo;
    @FXML
    private ComboBox creatorCombo;
    @FXML
    private Label displayIDLabel;

    private String username;
    @FXML
    private ListView list;
    private File file;
    private String type;
    private String creator;
    private String status;

    ///// Observable Lists for mainview, one with only the IDs and the other is for entire post
    ObservableList<Post> postObsInMain = FXCollections.observableArrayList();
    ObservableList<String> postObsIDMain = FXCollections.observableArrayList();
    //This is for responders
    ObservableList<String> res = FXCollections.observableArrayList();
    //Filtered list
    FilteredList<String> filteredListID = new FilteredList<>(postObsIDMain, s -> true);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

       // typeCombo.getItems().addAll("All", "Event", "Sale", "Job");
       // typeCombo.setValue("All");
        typeCombo.setItems(PostTypes());
        typeCombo.getSelectionModel().select(0);
        statusCombo.getItems().addAll("All", "Closed", "Open");
        statusCombo.setValue("All");
        creatorCombo.getItems().addAll("All", "My Posts");
        creatorCombo.setValue("All");
        LoadEvent();
        LoadSale();
        LoadJob();
        filterrr();
        showItems();

    }
    //For Type Combo box
    private static ObservableList<String>PostTypes(){
        ObservableList<String>types=FXCollections.observableArrayList();
        types.addAll("All", "Event", "Sale", "Job");
        return types;
    }


//Fitering Posts by Type
    private void filterrr(){
        typeCombo.getSelectionModel().selectedItemProperty().addListener((obs,o,n)->{
            type = typeCombo.getValue().toString();
            if(type==null){

            }
            else if(type.equals("Event")){
                filteredListID.setPredicate(s->s.contains("EVE"));
            }
             else if(type.equals("All")){
                filteredListID.setPredicate(s->true);
            }
            else if(type.equals("Job")){
                filteredListID.setPredicate(s->s.contains("JOB"));
            }
            else if(type.equals("Sale")){
                filteredListID.setPredicate(s->s.contains("SAL"));
            }
        });

    }
//When to filter by type of post
    @FXML private void typeComboPressed(){
        filterrr();

    }
    @FXML private void statusComboPressed(ActionEvent actionEvent) {

    }

    @FXML private void creatorComboPressed(ActionEvent actionEvent) {
    }

    //Show Developer Information in an information dialog box
    @FXML
    private void developerInfo(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Developer Information");
        alert.setContentText("NAME: SAMALKA WEDARATNE NANAYAKKARA TALPE MERENCHIGE\nSTUDENT ID: s3758710");
        alert.showAndWait();
    }


    //Quit the Apllication
    @FXML
    private void quitUnilink(ActionEvent event) throws IOException {
        Platform.exit();
        System.exit(0);
    }

    //Go back to the Login Window
    @FXML
    private void logoutButtonPushed(ActionEvent event) throws IOException {

        Parent Login = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        Scene login = new Scene(Login);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Login Window");
        window.setScene(login);
        window.centerOnScreen();
        window.show();
    }

    //This method is called in the Login Window if a successful login happens.
    @FXML
    public void setUsername(String user) {
        username = user;
        displayIDLabel.setText("Hello User " + user + "!!");
    }

//When new event button is pressed
    @FXML
    private void newEventButtonPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/NewEvent.fxml"));
        Parent newEvent = loader.load();

        Scene newEventscene = new Scene(newEvent);
        //access the controller
        NewEventController newEventController = loader.getController();
        //Passing the username to the New Event Window
        newEventController.setUsername(username);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("New Event Window");
        window.setScene(newEventscene);
        window.centerOnScreen();
        window.show();

    }
//WhEN New Sale button is pressed
    @FXML
    private void newSaleButtonPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/NewSale.fxml"));
        Parent newSale = loader.load();

        Scene newSaleScene = new Scene(newSale);
        //access the controller
        NewSaleController newSaleController = loader.getController();
        //Passing the username to the New Event Window
        newSaleController.setUsername(username);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("New Sale Window");
        window.setScene(newSaleScene);
        window.centerOnScreen();
        window.show();
    }
//When new JobButton is pressed
    @FXML
    private void newJobButtonPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/NewJob.fxml"));
        Parent newJob = loader.load();

        Scene newJobScene = new Scene(newJob);
        //access the controller
        NewJobController newJobController = loader.getController();
        //Passing the username to the New Event Window
        newJobController.setUsername(username);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("New Job Window");
        window.setScene(newJobScene);
        window.centerOnScreen();
        window.show();
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
                    postObsInMain.add(evOb);
                    postObsIDMain.add(resultSet.getString("postID"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
       // System.out.println("PostObsMain Observable list size: " + postObsInMain.size());

    }

    //Load Sale Posts from Database
    private void LoadSale() {
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
                    postObsInMain.add(salOb);
                    postObsIDMain.add(resultSet.getString("postID"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
       // System.out.println("PostObsMain Observable list size: " + postObsInMain.size());
    }

    //Load Job Posts from Database
    private void LoadJob() {
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
                    // Adding it to the list
                    postObsInMain.add(jobOb);
                    postObsIDMain.add(resultSet.getString("postID"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    //   System.out.println("PostObsMain Observable list size: " + postObsInMain.size());
    }

    //Show the list items
    private void showItems() {
        list.setItems(filteredListID);
      //  list.setItems(postObsIDMain);
        list.setCellFactory(params -> new Cell());
    }
//Export the data to a textfile in a directory of your choice.
    @FXML private void ExportClicked(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage)anchorPaneID.getScene().getWindow();
        file = directoryChooser.showDialog(stage);
        String fileAbsolute = file.getAbsolutePath();
        String fileAbsEdited = fileAbsolute.replace("\\", "\\\\");
        FileWriter writer = null;
       // Export ex = new Export();
        //ex.ExportItems(writer, fileAbsEdited);
       try {
            writer = new FileWriter(""+fileAbsEdited+"\\export_data.txt");

            for(Post p: postObsInMain){
                if(p instanceof Event){
                    String Name = findImageName(p.getPostID(),"EVENT");
                    writer.write(p.toString()+", Image Name = "+Name+" }\n");
                }
                if(p instanceof Sale){
                    String Name = findImageName(p.getPostID(),"SALE");
                    writer.write(p.toString()+", Image Name = "+Name+" }\n");
                }
                if(p instanceof Job){
                    String Name = findImageName(p.getPostID(),"JOB");
                    writer.write(p.toString()+", Image Name = "+Name+" }\n");
                }
            }
            writer.close();// flushes the stream.
           Alert info = new Alert(Alert.AlertType.INFORMATION);
           info.setHeaderText("Export!");
           info.setTitle("File Export");
           info.setContentText("File Successfully Exported");
           info.showAndWait();

        } catch (IOException e) {
            System.err.println("File cannot be created, or cannot be opened");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error!!");
            alert.setTitle("File Error");
            alert.setContentText("Cannot be created");
            alert.showAndWait();
        }
    }
//Find the Image name of the Post (Couldn't add the image name to the constructor earlier so have to fetch from database)
    private String findImageName(String postid, String tableName){
      String ImageNam="";
        final String DB_NAME = "testDB";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();){
            String query = "SELECT * FROM " + tableName+" WHERE postID ='"+postid+"'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while(resultSet.next()) {
                    ImageNam = resultSet.getString("imageName");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ImageNam;
    }
//Method to Import data not properly implemented
    @FXML private void ImportClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage)anchorPaneID.getScene().getWindow();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt"));
        File file2 = fileChooser.showOpenDialog(stage);
        String fileAbsolute2 = file2.getAbsolutePath();
        //System.out.println(fileAbsolute2);
        try {
            BufferedReader input = new BufferedReader(new FileReader(fileAbsolute2));
            String next = input.readLine();
            while (next != null) {
                System.out.println(next);
                next = input.readLine();
            }
            input.close();
        } catch (IOException e) {
            System.err.println("File Reading Error!");
            System.exit(0);
        }

    }


    //This class deals with everything related to the Listview Cell.
//There are lots of elements inside one List Item like (Buttons, labels, Hbox Vbox)
    class Cell extends ListCell<String> {
        HBox hBox = new HBox(30);
        VBox vBox = new VBox();
        Button replyButton = new Button(" Reply ");
        Button detailButton = new Button("Details");
        Label label = new Label();
        Pane pane = new Pane();
        private static final String color = "derive(palegreen, 10%)";
        private static final String color2 = "derive(pink, 10%)";
        private static final String color3 = "derive(lightblue, 10%)";
        private static final String baseColor = "#FFFFFF";
        ImageView img = new ImageView();
//When the Reply button is clicked on a Sale Post in the Listview
        EventHandler<MouseEvent> ReplClickedSale = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    replySale(mouseEvent,getItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
//When the Reply button is clicked on a Job Post
        EventHandler<MouseEvent> ReplClickedJob = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    replyJob(mouseEvent,getItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        EventHandler<MouseEvent>JobDetails = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    jobDetails(mouseEvent,getItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

//When the Join button is clicked in an Event Post
        EventHandler<MouseEvent> JoinClicked = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    joinEvent(mouseEvent,getItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
//Event Details Clicked
        EventHandler<MouseEvent> eventDetailsClicked = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    eventDetails(mouseEvent,getItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        EventHandler<MouseEvent> saleDetailsClicked = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    saleDetails(mouseEvent,getItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        public Cell() {
            super();
            detailButton.setDisable(true);
            replyButton.setDisable(true);
            vBox.getChildren().addAll(replyButton, detailButton);
            vBox.setSpacing(10);
            hBox.getChildren().addAll(img, vBox, label);
            hBox.setHgrow(pane, Priority.ALWAYS);
            label.setFont(new Font(14));
        }
        //This Method deals with UPDATING each list Cell. Item in this case means the post ID
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
                setStyle("-fx-control-inner-background: " + baseColor + ";");
            } else {
                for (Post p : postObsInMain) {
                    if (item.equals(p.getPostID())) {
                        String status = p.getStatus().toString();
                        LocalDate date;
                        String ddate;

                        String commonPost = "Post ID: " + p.getPostID() + "  Post Title: " + p.getTitle() + "   Description: " + p.getDescription()
                                + "\nCreator: " + p.getCreatorID() + "   Status: " + status;
                        String eventPost = ""; String jobPost = ""; String salePost = "";
                        if (p instanceof Event) {
                            replyButton.setText("  Join  ");
                            setStyle("-fx-control-inner-background: " + color + ";");
                            if(p.getCreatorID().equals(username)){
                                replyButton.setDisable(true);
                                detailButton.setDisable(false);
                                detailButton.setOnMouseClicked(eventDetailsClicked);
                            }
                            if(!p.getCreatorID().equals(username)){
                                detailButton.setDisable(true);
                                replyButton.setDisable(false);
                                replyButton.setOnMouseClicked(JoinClicked);
                            }
                            if(status.equals("CLOSED")){ replyButton.setDisable(true); }
                            date = ((Event) p).getDate();
                            ddate = date.toString();
                            eventPost = "\nVenue: " + ((Event) p).getVenue() + "   Date: " + ddate + "   Capacity: " + ((Event) p).getCapacity() + "   Attendee Count: " + ((Event) p).getAttendeeCount();
                        }
                        if (p instanceof Sale) {
                            replyButton.setText(" Reply ");
                            setStyle("-fx-control-inner-background: " + color2 + ";");
                            if (p.getCreatorID().equals(username)) {
                                replyButton.setDisable(true);
                                detailButton.setDisable(false);
                                detailButton.setOnMouseClicked(saleDetailsClicked);
                                salePost = "\nAsking Price: $" + ((Sale) p).getAskingPrice() + "   Highest Offer: $" + ((Sale) p).getHighestOffer() + "    Min Raise: $" + ((Sale) p).getMinRaise();
                            } if(!p.getCreatorID().equals(username)) {
                                detailButton.setDisable(true);
                                replyButton.setDisable(false);
                                replyButton.setOnMouseClicked(ReplClickedSale);
                                salePost = "\nHighest Offer: $"+((Sale) p).getHighestOffer()+"    Min Raise: $"+((Sale) p).getMinRaise();
                            } if(status.equals("CLOSED")){replyButton.setDisable(true);}
                        }
                        if (p instanceof Job) {
                            replyButton.setText(" Reply ");
                            setStyle("-fx-control-inner-background: " + color3 + ";");
                            if(p.getCreatorID().equals(username)){
                                replyButton.setDisable(true);
                                detailButton.setDisable(false);
                                detailButton.setOnMouseClicked(JobDetails);
                            }
                            if(!p.getCreatorID().equals(username)){
                                detailButton.setDisable(true);
                                replyButton.setDisable(false);
                                replyButton.setOnMouseClicked(ReplClickedJob);
                            }
                            if(status.equals("CLOSED")){replyButton.setDisable(true);}
                            jobPost = "\nProposed Price: $" + ((Job) p).getProposed() + "    Lowest Offer: $" + ((Job) p).getLowest();
                        }
                        String full = commonPost + eventPost + salePost + jobPost;
                        label.setText(full);
                        img.setImage(p.getImage());
                        setGraphic(hBox);
                    } else {
                    }
                }
            }
        }
    }

    //Reply to a sale post
    private void replySale(MouseEvent mouseEvent, String postid) throws IOException {
        responders(postid);
        boolean found = false;
        for(String s: res){
            if(s.equals(username)){
                found = true;
            }
        }
        if(found ==false){
            String postID = postid;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/replySale.fxml"));
            Parent replySale = loader.load();
            Scene replySaleScene = new Scene(replySale);
            //access the controller
            ReplySaleController replySaleController = loader.getController();
            //Passing the username and Post ID to the Sale reply Window
            replySaleController.setDetails(postID,username);
            Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            window.setTitle("Reply to Sale");
            window.setScene(replySaleScene);
            window.centerOnScreen();
            window.show();
        }
        if(found==true){
            Alert er = new Alert(Alert.AlertType.ERROR);
            er.setTitle("Reply to Sale Error");
            er.setHeaderText("You have Replied once!");
            er.showAndWait();
        }

    }
    //Reply to a job post, will be taken to another window
    private void replyJob(MouseEvent mouseEvent,String postid) throws IOException {
        responders(postid);
        boolean found = false;
        for(String s: res){
            if(s.equals(username)){
                found = true;
            }
        }
        if(found ==false){
            String postID = postid;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/ReplyJob.fxml"));
            Parent replyJob = loader.load();
            Scene replyJobb = new Scene(replyJob);
            //access the controller
            ReplyJobController replyJobController = loader.getController();
            //Passing the username and Post ID to the Job reply Window
            replyJobController.setDetails(postID,username);
            Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            window.setTitle("Reply to Job");
            window.setScene(replyJobb);
            window.centerOnScreen();
            window.show();
        }
        if(found==true){
            Alert er = new Alert(Alert.AlertType.ERROR);
            er.setTitle("Reply to Job Error");
            er.setHeaderText("You have Replied once!");
            er.showAndWait();
        }

    }
//When Someone wants to join an Event
    private void joinEvent(MouseEvent mouseEvent,String postid) throws IOException {
        boolean found = false;
        boolean cap = false;
            responders(postid);
            for(String s: res){
                if(s.equals(username)){
                    found = true;
                }
            }
            //FIND WHETHER THE CAPACITY EXCEEDS
            cap = capExceed(postid);
            if(cap==true){
                Alert er = new Alert(Alert.AlertType.ERROR);
                er.setTitle("Join Event Error");
                er.setHeaderText("Capacity Exceeds");
                er.showAndWait();
            }

            if(found==false&&cap==false){
                String postID = postid;
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/ReplyEvent.fxml"));
                Parent replyEvent = loader.load();
                Scene replyeve = new Scene(replyEvent);
                //access the controller
                ReplyEventController replyEventController = loader.getController();
                //Passing the username and Post ID to the Event reply Window
                replyEventController.setDetails(postID,username);
                Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                window.setTitle("Reply to Event");
                window.setScene(replyeve);
                window.centerOnScreen();
                window.show();
            }
            if(found==true){
            Alert er = new Alert(Alert.AlertType.ERROR);
            er.setTitle("Reply to Event Error");
            er.setHeaderText("You have Joined once!");
            er.showAndWait();
            }
            else {}
    }

    //Method finds whether capacity exceeds
    private boolean capExceed(String postid){
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
            return true;
        }
        return false;
    }

//This method get the responders who have responded to the current post
    private void responders(String postID) {
        res.clear();
        final String DB_NAME = "testDB";
        final String REPLY_TABLE = "REPLY";
        try (Connection con = ConnectionTest.getConnection(DB_NAME);
             Statement stmt = con.createStatement();){
            String query = "SELECT * FROM " + REPLY_TABLE+" WHERE postID ='"+postID+"'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while(resultSet.next()) {

                    res.add(resultSet.getString("responderID"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

//To check the event details
    private void eventDetails(MouseEvent mouseEvent, String PID) throws IOException {
        String postID = PID;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/EventDetails.fxml"));
        Parent eventDet = loader.load();

        Scene eventDetailsScene = new Scene(eventDet);
        //access the controller
        EventDetailsController eventDetails = loader.getController();
        //Passing the username and Post ID to the Details Event Window
        eventDetails.setUsername(username);
        eventDetails.setPostID(postID);
        Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        window.setTitle("Your Event Details");
        window.setScene(eventDetailsScene);
        window.centerOnScreen();
        window.show();
    }
//Check the Sale details
    private void saleDetails(MouseEvent mouseEvent,String PID) throws IOException {
        String postID = PID;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/SaleDetails.fxml"));
        Parent SaleDet = loader.load();

        Scene eventDetailsScene = new Scene(SaleDet);
        //access the controller
        SaleDetailsController saleDetails = loader.getController();
        //Passing the username and Post ID to the Sale detailed Window
        saleDetails.setUsername(username);
        saleDetails.setPostID(postID);
        Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        window.setTitle("Your Sale Post Details");
        window.setScene(eventDetailsScene);
        window.centerOnScreen();
        window.show();
    }
//Check the job details
    private void jobDetails(MouseEvent mouseEvent, String PID) throws IOException {
        String postID = PID;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/JobDetails.fxml"));
        Parent Job = loader.load();
        Scene JobDet = new Scene(Job);
        //access the controller
        JobDetailsController jobDetails = loader.getController();
        //Passing the username and Post ID to the JOB detailed Window
        jobDetails.setJob(username,postID);
        Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        window.setTitle("Your Job Post Details");
        window.setScene(JobDet);
        window.centerOnScreen();
        window.show();
    }
}