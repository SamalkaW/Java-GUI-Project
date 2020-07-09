package model;

import javafx.scene.image.Image;

import java.net.URISyntaxException;
import java.time.LocalDate;

public class Sale extends Post{
    private double minRaise;
    private double askingPrice;
    private double highestOffer;


    public double getMinRaise() {
        return minRaise;
    }

    public void setMinRaise(double minRaise) {
        this.minRaise = minRaise;
    }

    public double getAskingPrice() {
        return askingPrice;
    }

    public void setAskingPrice(double askingPrice) {
        this.askingPrice = askingPrice;
    }

    public double getHighestOffer() {
        return highestOffer;
    }

    public void setHighestOffer(double highestOffer) {
        this.highestOffer = highestOffer;
    }


    @Override
    public String toString() {
        return super.toString()+
                "minRaise= " + minRaise +
                ", askingPrice= " + askingPrice +
                ", highestOffer= " + highestOffer +
                "";
    }

    //from Database
    public Sale(String id, Image photo, String title, String description, String creatorID, Status status, double highest,
                 double minRaise, double askingPrice){
        super(id,photo,title,description,creatorID,status);
        this.askingPrice = askingPrice;
        this.minRaise = minRaise;
        this.highestOffer = highest;
    }

}
