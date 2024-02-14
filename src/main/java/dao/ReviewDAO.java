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

    public ReviewDAO() {
        // Set up MongoDB connection and get the review collection
        this.mongoClient = MongoClients.create("mongodb://10.1.1.20:27017,10.1.1.21:27017,10.1.1.22:27017/" +
        										"?w=1&readPreferences=nearest&timeout=5000");
        this.reviewCollection = mongoClient.getDatabase("SafeBite").getCollection("Reviews");
    }
    
    public List<Review> getReviewsByProductId(String productId) {
        List<Review> reviews = new ArrayList<>();

        try (MongoCursor<Document> cursor = reviewCollection.find(Filters.eq("Product ID", new ObjectId(productId))).iterator()) {
            while (cursor.hasNext()) {
                Document reviewDoc = cursor.next();
                Review review = documentToReview(reviewDoc);
                reviews.add(review);
            }
        } catch (Exception e) {
            // Handle exceptions appropriately (e.g., log the error, throw an exception)
            e.printStackTrace();
            // You might want to throw an exception here or return an empty list
        }

        return reviews;
    }

    public Object getLastThreeReviewsWithDates(String username) {
        List<Document> userReviews = new ArrayList<>();
        List<Review> reviews = new ArrayList<>();
        List<Date> reviewDates = new ArrayList<>();  // List to store review dates

        try (MongoCursor<Document> cursor = reviewCollection.find(Filters.eq("User", username)).iterator()) {
            while (cursor.hasNext()) {
                userReviews.add(cursor.next());
            }
        }

        // Check if there are no reviews
        if (userReviews.isEmpty()) {
            return Collections.emptyMap(); 
        }

        // Sort the reviews by date in descending order
        userReviews.sort(Comparator.comparing(document -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                Date date = dateFormat.parse((String) ((Document) document).get("Review Date"));
                return date;
            } catch (ParseException e) {
                e.printStackTrace();
                return new Date(0); // Use a default date if parsing fails
            }
        }).reversed());

        // Retrieve the last 3 reviews and their dates
        for (int i = 0; i < Math.min(userReviews.size(), 3); i++) {
            Document reviewDoc = userReviews.get(i);
            Review review = documentToReview(reviewDoc);
            reviews.add(review);

            // Get and add the date to the list
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                Date date = dateFormat.parse((String) reviewDoc.get("Review Date"));
                reviewDates.add(date);
            } catch (ParseException e) {
                e.printStackTrace();
                // Handle the exception if needed
            }
        }

        // Create a Map to store both reviews and dates
        Map<String, Object> result = new HashMap<>();
        result.put("reviews", reviews);
        result.put("reviewDates", reviewDates);

        return result;
    }        

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
    
    public void deleteReviewById(String reviewId) {
        try {
            // Convert reviewId to ObjectId
            ObjectId objectId = new ObjectId(reviewId);

            // Delete the review by its ID
            reviewCollection.deleteOne(Filters.eq("_id", objectId));
        } catch (Exception e) {
            // Handle exceptions appropriately (e.g., log the error, throw an exception)
            e.printStackTrace();
        }
    }
    
    public void saveReviewToMongoDB(Review review) {
		
            // Create a document from the Review object
            Document reviewDocument = new Document("Review Rating", review.getReviewRating())
                    .append("Review Heading", review.getReviewHeading())
                    .append("Review Text", review.getReviewText())
                    .append("Review Date", review.getReviewDate())
                    .append("Product Name", review.getProductName())
                    .append("User", review.getUsername())
                    .append("Product ID", review.getProductID());                    ;

            // Insert the document into the MongoDB collection
            reviewCollection.insertOne(reviewDocument);
    }
    
    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Mongo connection closed successfully.");
        }
    }
}
