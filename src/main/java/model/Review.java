package model;

import org.bson.types.ObjectId;

public class Review {
    private String username;
    private String reviewText;
    private String reviewHeading;
    private String reviewDate;
    private Integer reviewRating;
    private String productName;
    private ObjectId productID;
    
    
    public Review(String username, String reviewText, String reviewDate) {
    	
        this.username = username;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
    }

    public Review(String username, String productName, ObjectId productID, String reviewText, String reviewDate, 
    		String reviewHeading, Integer reviewRating) {
    	
        this.username = username;
        this.productID = productID;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
        this.productName = productName;
        this.reviewHeading = reviewHeading;
        this.reviewRating = reviewRating;  
    }

    
 // Getter and Setter for 'username'
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for 'reviewText'
    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    // Getter and Setter for 'reviewHeading'
    public String getReviewHeading() {
        return reviewHeading;
    }

    public void setReviewHeading(String reviewHeading) {
        this.reviewHeading = reviewHeading;
    }

    // Getter and Setter for 'reviewDate'
    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    // Getter and Setter for 'reviewRating'
    public Integer getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(Integer reviewRating) {
        this.reviewRating = reviewRating;
    }

    // Getter and Setter for 'productName'
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public ObjectId getProductID() {
        return productID;
    }


}
