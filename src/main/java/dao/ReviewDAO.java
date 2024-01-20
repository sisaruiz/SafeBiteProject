package dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import model.Review;

import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewDAO {
    private final MongoClient mongoClient;
    private final MongoCollection<Document> reviewCollection;

    public ReviewDAO() {
        // Set up MongoDB connection and get the review collection
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.reviewCollection = mongoClient.getDatabase("SafeBite").getCollection("Reviews");
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
            return "You don't have any reviews yet.";
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
        String username = document.getString("User");
        String reviewText = document.getString("Review Text");
        String reviewDateString = document.getString("Review Date");

        return new Review(username, reviewText, reviewDateString);
    }
}
