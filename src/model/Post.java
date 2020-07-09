package model;

import javafx.scene.image.Image;

public abstract class Post {
    private String title;
    private String description;
   // private String status;
    private String creatorID;
    private String postID;
    private Status status;
    private Image image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

   /* public Post (Image photo,String title, String description, String creatorID) {
        this.title = title;
        this.description = description;
        this.creatorID = creatorID;
        status = Status.OPEN;
        image = photo;

    }*/
    //for New objects without an image
    /*public Post (String title, String description, String creatorID){
        this.title = title;
        this.description = description;
        this.creatorID = creatorID;
        status = Status.OPEN;
       // image = new Image("/Images/defaultImage.jpg");
        //Image image = new Image(getClass().getResource("za.jpg").toURI().toString());

        image = new Image("file:Images/defaultImage.jpg",70,70,false,false);

    }*/
    //for objects from database,
    public Post(String id, Image photo, String title, String description, String creatorID, Status status){
        this.title = title;
        this.description = description;
        this.creatorID = creatorID;
        this.status = status;
        image = photo;
        setPostID(id);
    }
    /*//FOR TESTING PURPOSES DB
    public Post(String id, String title, String description, String creatorID, Status status){
        this.title = title;
        this.description = description;
        this.creatorID = creatorID;
        this.status = status;
        setPostID(id);
    }*/

    @Override
    public String toString() {
        return "Post{" +
                " postID= '" + postID + '\'' +
                "title= '" + title + '\'' +
                ", description= '" + description + '\'' +
                ", creatorID= '" + creatorID + '\'' +
                ", status= " + status ;
    }
}
