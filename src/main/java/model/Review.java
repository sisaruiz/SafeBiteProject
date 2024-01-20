package model;



public class Review {
    private String username;
    private String reviewText;
    private String reviewDate;

    public Review(String username, String reviewText, String reviewDate) {
        this.username = username;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
    }

    // Getters and setters for each attribute

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }


}
