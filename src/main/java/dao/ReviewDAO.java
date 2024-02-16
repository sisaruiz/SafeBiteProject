package dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import model.Review;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewDAO {
    private final MongoClient mongoClient;
    public final MongoCollection<Document> reviewCollection;

    /**
     * Constructor to initialize MongoDB connection and review collection.
     */
    public ReviewDAO() {
        this.mongoClient = MongoClients.create("mongodb://10.1.1.20:27017,10.1.1.21:27017,10.1.1.22:27017/" +
        										"?w=1&readPreferences=nearest&timeout=5000");
        this.reviewCollection = mongoClient.getDatabase("SafeBite").getCollection("Reviews");
    }
    
    /**
     * Retrieve reviews for a given product ID from MongoDB.
     *
     * @param productId Product ID to filter reviews.
     * @return List of Review objects for the specified product ID.
     */
    public List<Review> getReviewsByProductId(String productId) {
        List<Review> reviews = new ArrayList<>();

        try (MongoCursor<Document> cursor = reviewCollection.find(Filters.eq("Product ID", new ObjectId(productId))).iterator()) {
            while (cursor.hasNext()) {
                Document reviewDoc = cursor.next();
                Review review = documentToReview(reviewDoc);
                reviews.add(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reviews;
    }

    /**
     * Retrieve the last three reviews along with their dates for a given username.
     *
     * @param username User for whom reviews are retrieved.
     * @return Map containing reviews and their corresponding dates.
     */
    public Object getLastThreeReviewsWithDates(String username) {
        List<Document> userReviews = new ArrayList<>();
        List<Review> reviews = new ArrayList<>();
        List<Date> reviewDates = new ArrayList<>(); 

        try (MongoCursor<Document> cursor = reviewCollection.find(Filters.eq("User", username)).iterator()) {
            while (cursor.hasNext()) {
                userReviews.add(cursor.next());
            }
        }

        if (userReviews.isEmpty()) {
            return Collections.emptyMap(); 
        }

        userReviews.sort(Comparator.comparing(document -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                Date date = dateFormat.parse((String) ((Document) document).get("Review Date"));
                return date;
            } catch (ParseException e) {
                e.printStackTrace();
                return new Date(0); 
            }
        }).reversed());

        for (int i = 0; i < Math.min(userReviews.size(), 3); i++) {
            Document reviewDoc = userReviews.get(i);
            Review review = documentToReview(reviewDoc);
            reviews.add(review);

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                Date date = dateFormat.parse((String) reviewDoc.get("Review Date"));
                reviewDates.add(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("reviews", reviews);
        result.put("reviewDates", reviewDates);

        return result;
    }        

    /**
     * Convert MongoDB document to a Review object.
     *
     * @param document MongoDB document representing a review.
     * @return Review object created from the document.
     */
    private Review documentToReview(Document document) {
    	ObjectId revID = document.getObjectId("_id");
        String username = document.getString("User");
        String reviewText = document.getString("Review Text");
        String reviewDateString = document.getString("Review Date");
        String reviewHeading = document.getString("Review Heading");
        Integer reviewRating = document.getInteger("Review Rating");
        String productName = document.getString("Product Name");
        ObjectId productID = document.getObjectId("Product ID");

        return new Review(revID, username, productName, productID, reviewText, reviewDateString,
        		reviewHeading, reviewRating);
    }
    
    /**
     * Delete a review from MongoDB using its ID.
     *
     * @param reviewId ID of the review to be deleted.
     */
    public void deleteReviewById(String reviewId) {
        try {
            ObjectId objectId = new ObjectId(reviewId);

            reviewCollection.deleteOne(Filters.eq("_id", objectId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Save a new review to MongoDB.
     *
     * @param review Review object to be saved.
     */
    public void saveReviewToMongoDB(Review review) {

            Document reviewDocument = new Document("Review Rating", review.getReviewRating())
                    .append("Review Heading", review.getReviewHeading())
                    .append("Review Text", review.getReviewText())
                    .append("Review Date", review.getReviewDate())
                    .append("Product Name", review.getProductName())
                    .append("User", review.getUsername())
                    .append("Product ID", review.getProductID());                    ;

            reviewCollection.insertOne(reviewDocument);
    }
    
    /**
     * Close the MongoDB connection.
     */
    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Mongo connection closed successfully.");
        }
    }
}
