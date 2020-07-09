package model;

public class Responders {
    private String postID;
    private String responderID;
    private double value;

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getResponderID() {
        return responderID;
    }

    public void setResponderID(String responderID) {
        this.responderID = responderID;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Responders(String postID, String responderID, double val){

        this.postID = postID;
        this.responderID = responderID;
        value = val;
    }
}
