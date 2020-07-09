package model.hsql_db;

import java.sql.Connection;
import java.sql.Statement;

public class CreateTable {


public void createTables(){
    final String DB_NAME = "testDB";
    final String EVENT_TABLE = "EVENT";
    final String SALE_TABLE = "SALE";
    final String JOB_TABLE = "JOB";
    final String REPLY_TABLE = "REPLY";
    //use try-with-resources Statement
    try (Connection con = ConnectionTest.getConnection(DB_NAME);
         Statement stmt = con.createStatement();
    ) {
        int result = stmt.executeUpdate("CREATE TABLE event ("
                + "postID VARCHAR(6) NOT NULL,"
                + "imageName VARCHAR(30) NOT NULL,"
                + "title VARCHAR(40) NOT NULL,"
                + "description VARCHAR(50) NOT NULL,"
                + "creatorID VARCHAR(8) NOT NULL,"
                + "status VARCHAR(8) NOT NULL,"
                + "eventVenue VARCHAR(20),"
                + "eventDate VARCHAR(10),"
                + "eventCapacity INT,"
                + "eventAttendCount INT,"
                + "PRIMARY KEY (postID))");

        int result1 = stmt.executeUpdate("CREATE TABLE sale ("
                + "postID VARCHAR(6) NOT NULL,"
                + "imageName VARCHAR(30) NOT NULL,"
                + "title VARCHAR(40) NOT NULL,"
                + "description VARCHAR(50) NOT NULL,"
                + "creatorID VARCHAR(8) NOT NULL,"
                + "status VARCHAR(8) NOT NULL,"
                + "saleHighOffer DOUBLE,"
                + "saleMinRaise DOUBLE,"
                + "saleAskingPrice DOUBLE,"
                + "PRIMARY KEY (postID))");

        int result2 = stmt.executeUpdate("CREATE TABLE job ("
                + "postID VARCHAR(6) NOT NULL,"
                + "imageName VARCHAR(30) NOT NULL,"
                + "title VARCHAR(40) NOT NULL,"
                + "description VARCHAR(50) NOT NULL,"
                + "creatorID VARCHAR(8) NOT NULL,"
                + "status VARCHAR(8) NOT NULL,"
                + "jobProposed DOUBLE,"
                + "jobLowest DOUBLE,"
                + "PRIMARY KEY (postID))");

        int result3 = stmt.executeUpdate("CREATE TABLE reply ("
                + "postID VARCHAR(6) NOT NULL,"
                + "responderID VARCHAR(8) NOT NULL,"
                + "value DOUBLE ,"
                + "PRIMARY KEY (postID,responderID))");

        if(result == 0&&result1==0&& result2==0 && result3==0) {
            System.out.println("Table " + EVENT_TABLE + " , "+SALE_TABLE+" ,"+JOB_TABLE+" ,"+ REPLY_TABLE+" has been created successfully");
        } else {
            System.out.println("Tables not created");
        }
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
}
}
