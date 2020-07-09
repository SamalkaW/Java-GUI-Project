package model;

import javafx.scene.image.Image;

import java.net.URISyntaxException;
import java.time.LocalDate;

public class Event extends Post{

    private String venue;
    private LocalDate date;
    private int capacity;
    private int attendeeCount;
    /*private int eventID;
    private static int eventCount;*/

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAttendeeCount() {
        return attendeeCount;
    }

    public void setAttendeeCount(int attendeeCount) {
        this.attendeeCount = attendeeCount;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    /*//new objects not from db with image NO NEED THIS
    public Event(int rows, Image image, String title, String description, String creatorID, String venue, LocalDate date, int capacity) {
        super(image, title, description, creatorID);
        this.venue = venue;
        this.date = date;
        this.capacity = capacity;
        attendeeCount = 0;
        //IF CONDITION TO CHECK WHAT IS LARGER THE NO OF ROWS OR THE EVENTCOUNT
            if (rows>eventCount){
                eventCount = rows;
                eventID = ++eventCount;
            }
            else{
                eventID = ++eventCount;
            }
        super.setPostID(String.format("EVE%03d",eventID));

    }
    //new objects not from db without image NO NEED THIS
    public Event(int rows, String title, String description, String creatorID, String venue, LocalDate date, int capacity)  {
        super(title,description,creatorID);
        this.venue = venue;
        this.date = date;
        this.capacity = capacity;
        attendeeCount = 0;
        //IF CONDITION TO CHECK WHAT IS LARGER THE NO OF ROWS OR THE EVENTCOUNT
        if (rows>eventCount){
            eventCount = rows;
            eventID = ++eventCount;
        }
        else{
            eventID = ++eventCount;
        }
        super.setPostID(String.format("EVE%03d",eventID));

    }*/
    //from the database already exsisting events
    public Event(String id, Image photo, String title, String description, String creatorID, Status status,
                 String venue, LocalDate date, int capacity,int attendeeCount){
        super(id,photo,title,description,creatorID,status);
        this.venue = venue;
        this.date = date;
        this.capacity = capacity;
        this.attendeeCount = attendeeCount;

    }

    //FOR TESTING PURPOSES
 /*   public Event(String id, String title, String description, String creatorID, Status status,
                 String venue, LocalDate date, int capacity,int attendeeCount){
        super(id,title,description,creatorID,status);
        this.venue = venue;
        this.date = date;
        this.capacity = capacity;
        this.attendeeCount = attendeeCount;

    }*/

    @Override
    public String toString() {
        return super.toString()+
                "venue= '" + venue + '\'' +
                ", date= " + date +
                ", capacity= " + capacity +
                ", attendeeCount= " + attendeeCount +
                "";
    }
}
