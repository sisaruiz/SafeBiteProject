package model;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ReviewDAO {
    private final MongoClient mongoClient;
    private final MongoCollection<Document> reviewCollection;

    public ReviewDAO() {
        // Set up MongoDB connection and get the review collection
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.reviewCollection = mongoClient.getDatabase("SafeBite").getCollection("Reviews");
    }

    public List<Review> getLastThreeReviewsByUsername(String username) {
        List<Review> reviews = new ArrayList<>();

        // Fetch all reviews for the given username
        List<Document> userReviews = new ArrayList<>();
        try (MongoCursor<Document> cursor = reviewCollection.find(Filters.eq("User", username))
                .iterator()) {
            while (cursor.hasNext()) {
                userReviews.add(cursor.next());
            }
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

        // Retrieve the last 3 reviews
        for (int i = 0; i < Math.min(userReviews.size(), 3); i++) {
            Document reviewDoc = userReviews.get(i);
            Review review = documentToReview(reviewDoc);
            reviews.add(review);
        }

        return reviews;
    }

    private Review documentToReview(Document document) {
        String username = document.getString("User");
        String reviewText = document.getString("Review Text");
        String reviewDateString = document.getString("Review Date");

        return new Review(username, reviewText, reviewDateString);
    }
}
