
//NAME: SAMALKA WEDARATNE S3758710
//ASSIGNMENT 2 ADVANCED PROGRAMMING

//NOTE: Some methods are not called or commented but are usually used for testing purposes

package main;
import controller.LoginController;
import controller.MainWindowController;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Event;
import model.Post;
import model.Status;
import model.hsql_db.*;
import javafx.scene.image.Image;


import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UnilinkGUI extends Application {


    public int x;
    ReadDatabase r = new ReadDatabase();
    private Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws URISyntaxException {
        //DELETE ALL DATABASE FILES THEN uncomment below ONLY IF WANT to create database from scratch
        //But it has already being done mainly written for testing purposes.

     /*   ConnectionTest test = new ConnectionTest();
        test.connectionDB();
        CreateTable tables = new CreateTable();
        tables.createTables();
        InsertRows rw= new InsertRows();
        rw.Insert();*/

        UnilinkGUI App = new UnilinkGUI ();

       //ReadDatabase r = new ReadDatabase();
       //r.readEvent();
       // r.readSale();
       // r.readJob();
       // r.readReply();


       launch(args);
    }
    public UnilinkGUI(){

    }

    //Creating the stage for the scenes, starting from login window
    @Override
    public void start(Stage stage) throws Exception {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Scene scene1 = new Scene(root,400,200);
            stage.setTitle("Login Window");
            stage.setScene(scene1);
            stage.centerOnScreen();
            stage.show();
            //System.out.println("Success");

        }
        catch(IOException e){
            //System.out.println("Nope");

        }
    }





}
