package model;

import javafx.scene.image.Image;

import java.net.URISyntaxException;
import java.time.LocalDate;

public class Job extends Post {
    private double proposed;
    private double lowest;
    /*private int jobID;
    private static int jobCount = 1;*/

    public double getProposed() {
        return proposed;
    }

    public void setProposed(double proposed) {
        this.proposed = proposed;
    }

    public double getLowest() {
        return lowest;
    }

    public void setLowest(double lowest) {
        this.lowest = lowest;
    }

   /* public Job(String title, String description, String creatorID)  {
        super(title, description, creatorID);
        this.proposed = proposed;
        lowest = 0.0;
        jobID = jobCount++;
        setPostID(String.format("JOB%03d",jobID));

    }*/

    //from Database
    public Job(String id, Image photo, String title, String description, String creatorID, Status status,
                 double proposed, double lowest){
        super(id,photo,title,description,creatorID,status);
        this.proposed = proposed;
        this.lowest = lowest;
    }

    @Override
    public String toString() {
        return super.toString()+
                "proposed= " + proposed +
                ", lowest= " + lowest +
                "";
    }
}
