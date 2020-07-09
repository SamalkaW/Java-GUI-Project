package model.hsql_db;

import java.sql.Connection;
import java.sql.Statement;

public class InsertRows {

    public void Insert() {
        final String DB_NAME = "testDB";
        final String EVENT_TABLE = "EVENT";
        final String SALE_TABLE = "SALE";
        final String JOB_TABLE = "JOB";
        final String REPLY_TABLE ="REPLY";

        try (Connection con = ConnectionTest.getConnection(DB_NAME);
                Statement stmt = con.createStatement();) {
            String query1 = "INSERT INTO " + EVENT_TABLE+
                    " VALUES ('EVE001', 'PartyImage.png', 'Sunfest Party', 'Evening barbeque and DJ','s1234567','OPEN'," +
                    "'St Kilda Beach','2020-07-10',10,2)";
            String query2 = "INSERT INTO " +EVENT_TABLE+
                    " VALUES ('EVE002','ChoirImage.png','Recital','Choir and Practice Session', 's1234587','OPEN','Holy Church'," +
                    "'2020-07-11',4,2)";
            String query3 = "INSERT INTO "+SALE_TABLE+
                    " VALUES ('SAL001','ChemImage.png','Chem Textbook','Organic Chem 5th Ed','s1234765','OPEN',40.0,10.0,90.0)";
            String query4 = "INSERT INTO "+SALE_TABLE+
                    " VALUES ('SAL002','WalletImage.png','Wallet','Limited Ed Gucci croc leather','s1234654','OPEN',75.0,30.0,300.0)";
            String query5 = "INSERT INTO "+JOB_TABLE+
                    " VALUES ('JOB001','CakeImage.png','Bake','Help me bake a cake','s1234567','OPEN',75.0,70.0)";
            String query6 = "INSERT INTO "+JOB_TABLE+
                    " VALUES ('JOB002','defaultImage.jpg','Knitting','Help knit a gown','s3456788','OPEN',50.0,45.0)";


            String query7 = "INSERT INTO "+REPLY_TABLE+" VALUES ('EVE001','s7654321',1.0)";
            String query8 = "INSERT INTO "+REPLY_TABLE+" VALUES ('EVE001','s7654322',1.0)";
            String query9 = "INSERT INTO "+REPLY_TABLE+" VALUES ('EVE002','s7654328',1.0)";
            String query10 = "INSERT INTO "+REPLY_TABLE+" VALUES ('EVE002','s7654325',1.0)";
            String query11 = "INSERT INTO "+REPLY_TABLE+" VALUES ('SAL001','s7654372',25.0)";
            String query12 = "INSERT INTO "+REPLY_TABLE+" VALUES ('SAL001','s7655372',40.0)";
            String query13 = "INSERT INTO "+REPLY_TABLE+" VALUES ('SAL002','s7665379',40.0)";
            String query14 = "INSERT INTO "+REPLY_TABLE+" VALUES ('SAL002','s7655472',75.0)";
            String query15 = "INSERT INTO "+REPLY_TABLE+" VALUES ('JOB001','s8655372',72.0)";
            String query16 = "INSERT INTO "+REPLY_TABLE+" VALUES ('JOB001','s7645372',70.0)";
            String query17 = "INSERT INTO "+REPLY_TABLE+" VALUES ('JOB002','s7655375',48.0)";
            String query18 = "INSERT INTO "+REPLY_TABLE+" VALUES ('JOB002','s7655373',45.0)";

            int result = stmt.executeUpdate(query1);
            con.commit();
            System.out.println("Insert into table " + EVENT_TABLE + " executed successfully "+result);
            int result2 = stmt.executeUpdate(query2);
            con.commit();
            System.out.println("Insert into table " + EVENT_TABLE + " executed successfully "+result2);
            int result3 = stmt.executeUpdate(query3);
            con.commit();
            System.out.println("Insert into table " + SALE_TABLE + " executed successfully "+result3);
            int result4 = stmt.executeUpdate(query4);
            con.commit();
            System.out.println("Insert into table " + SALE_TABLE + " executed successfully "+result4);
            int result5 = stmt.executeUpdate(query5);
            con.commit();
            System.out.println("Insert into table " + JOB_TABLE + " executed successfully "+result5);
            int result6 = stmt.executeUpdate(query6);
            con.commit();
            System.out.println("Insert into table " + JOB_TABLE + " executed successfully "+result6);

            int result7 = stmt.executeUpdate(query7);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result7);
            int result8 = stmt.executeUpdate(query8);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result8);
            int result9 = stmt.executeUpdate(query9);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result9);
            int result10 = stmt.executeUpdate(query10);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result10);
            int result11 = stmt.executeUpdate(query11);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result11);
            int result12 = stmt.executeUpdate(query12);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result12);
            int result13 = stmt.executeUpdate(query13);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result13);
            int result14 = stmt.executeUpdate(query14);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result14);
            int result15 = stmt.executeUpdate(query15);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result15);
            int result16 = stmt.executeUpdate(query16);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result16);
            int result17 = stmt.executeUpdate(query17);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result17);
            int result18 = stmt.executeUpdate(query18);
            con.commit();
            System.out.println("Insert into table " + REPLY_TABLE + " executed successfully "+result18);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
