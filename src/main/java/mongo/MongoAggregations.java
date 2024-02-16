package mongo;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Sorts.*;

import org.bson.Document;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Projections;

import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.ProductDAO;
import dao.ReviewDAO;
import dao.UserDAO;

public class MongoAggregations {

	private ProductDAO productDAO;
	private ReviewDAO reviewDAO;
	private UserDAO userDAO;
	

	/**
     * Constructor initializes ProductDAO, ReviewDAO, and UserDAO.
     */
    public MongoAggregations() {
    	
    	productDAO = new ProductDAO();
    	reviewDAO = new ReviewDAO();
    	userDAO = new UserDAO();
    }

    
    /**
     * Retrieves the count of products in each category.
     *
     * @return Aggregate result for the count of products in each category.
     */
    public AggregateIterable<Document> getProductCategoryCounts() {
        
        return productDAO.productsCollection.aggregate(Arrays.asList(
                group("$main_category", sum("count", 1)),
                sort(descending("count")),
                limit(10),
                group("$_id", sum("count", "$count")),
                sort(descending("count"))
        ));
    }
    
    /**
     * Retrieves product count by brand and country, limiting to the top 20 results.
     *
     * @return List of documents containing product count by brand and country.
     */
    public List<Document> getProductCountByBrandAndCountry() {
        Bson matchStage = match(and(exists("brands"), exists("countries_en")));
        
        Bson groupStage = group(fields(eq("brands", "$brands"), eq("countries_en", "$countries_en")), sum("productCount", 1));
        
        Bson projectStage = project(fields(
            computed("brandOwner", "$_id.brands"),
            computed("country", "$_id.countries_en"),
            include("productCount"),
            excludeId()
        ));
        
        Bson sortStage = sort(descending("productCount"));
        
        Bson limitStage = limit(20);
        
        List<Bson> pipeline = Arrays.asList(matchStage, groupStage, projectStage, sortStage, limitStage);
        
        List<Document> results = productDAO.productsCollection.aggregate(pipeline).into(new ArrayList<>());
        
        System.out.println("Aggregation Results: " + results);
        
        return results;
    }

    /**
     * Retrieves the most reviewed products based on the provided limit.
     *
     * @param limit Number of products to retrieve.
     * @return List of documents containing the most reviewed products.
     */
    public List<Document> getMostReviewedProducts(int limit) {
        
        List<Document> pipeline = Arrays.asList(
            new Document("$group", new Document("_id",
                new Document("ProductID", "$Product ID").append("ProductName", "$Product Name"))
                .append("reviewCount", new Document("$sum", 1))),
            new Document("$sort", new Document("reviewCount", -1)),
            new Document("$limit", limit),
            new Document("$project", new Document("_id", 0)
                .append("ProductID", "$_id.ProductID")
                .append("ProductName", "$_id.ProductName")
                .append("reviewCount", 1))
        );

        AggregateIterable<Document> aggregationResult = reviewDAO.reviewCollection.aggregate(pipeline);

        List<Document> mostReviewedProducts = new ArrayList<>();
        for (Document document : aggregationResult) {
            mostReviewedProducts.add(document);
        }

        return mostReviewedProducts;
    }
    
    /**
     * Retrieves top reviewers and their average ratings based on the provided limit.
     *
     * @param limit Number of top reviewers to retrieve.
     * @return List of documents containing top reviewers and their average ratings.
     */
    public List<Document> getTopReviewersAndAverageRating(int limit) {
        
        List<Document> pipeline = Arrays.asList(
                new Document("$group", new Document("_id", "$User")
                        .append("totalProductsReviewed", new Document("$sum", 1))
                        .append("averageRating", new Document("$avg", "$Review Rating"))),
                new Document("$sort", new Document("totalProductsReviewed", -1)),
                new Document("$limit", limit),
                new Document("$project", new Document("_id", 0)
                        .append("User", "$_id")
                        .append("totalProductsReviewed", 1)
                        .append("averageRating", 1))
        );

        AggregateIterable<Document> aggregationResult = reviewDAO.reviewCollection.aggregate(pipeline);

        List<Document> topReviewersAndAverageRating = new ArrayList<>();
        for (Document document : aggregationResult) {
            topReviewersAndAverageRating.add(document);
        }

        return topReviewersAndAverageRating;
}
   
    /**
     * Calculates gender percentage by diet type using aggregation.
     *
     * @return Aggregate result for gender percentage by diet type.
     */
    public AggregateIterable<Document> calculateGenderPercentageByDietType() {
        Document groupByGenderAndDietType = new Document("$group", new Document("_id",
                new Document("diet_type", "$diet_type")
                        .append("gender", "$gender"))
                .append("count", new Document("$sum", 1)));

        Document groupByDietType = new Document("$group", new Document("_id", "$_id.diet_type")
                .append("totalUsers", new Document("$sum", "$count"))
                .append("genderCounts", new Document("$push",
                        new Document("gender", "$_id.gender")
                                .append("count", "$count"))));

        Document projectResult = new Document("$project", new Document("_id", 0)
                .append("diet_type", "$_id")
                .append("percentages", new Document("$map",
                        new Document("input", "$genderCounts")
                                .append("as", "gc")
                                .append("in", new Document("gender", "$$gc.gender")
                                        .append("percentage", new Document("$multiply",
                                                Arrays.asList(new Document("$divide",
                                                        Arrays.asList("$$gc.count", "$totalUsers")),
                                                        100)))))));

        return userDAO.usersCollection.aggregate(Arrays.asList(
                groupByGenderAndDietType,
                groupByDietType,
                projectResult
        ));
    }
    

    /**
     * Retrieves user rating distribution over time for the provided username.
     *
     * @param username Username for which to retrieve the rating distribution.
     * @return List of documents containing user rating distribution over time.
     */
    public List<Document> getUserRatingDistributionOverTime(String username) {
        List<Document> userRatingDistribution = new ArrayList<>();

        List<Bson> pipeline = Arrays.asList(
                match(eq("User", username)),
                group("$Review Date",
                        Accumulators.push("ratings", "$Review Rating"), 
                        sum("count", 1)
                ),
                project(Projections.fields(
                        Projections.excludeId(), 
                        Projections.computed("Review Date", new Document("$toDate", "$_id")), 
                        Projections.include("ratings", "count")
                )),

                addFields(new Field<>("averageRating", new Document("$cond",
                        Arrays.asList(
                                new Document("$eq", Arrays.asList("$ratings", Arrays.asList((Object) null))),
                                null,
                                new Document("$arrayElemAt", Arrays.asList("$ratings", 0))
                        )
                ))),

                sort(ascending("Review Date"))
        );
        
        for (Bson stage : pipeline) {
            System.out.println("Stage: " + stage.toBsonDocument(Document.class, MongoClientSettings.getDefaultCodecRegistry()));
        }

        reviewDAO.reviewCollection.aggregate(pipeline).into(userRatingDistribution);
        
        for (Document document : userRatingDistribution) {
        	List<?> ratings = document.get("ratings", List.class);
        	System.out.println("Ratings: " + ratings);

            System.out.println("Date: " + document.getDate("Review Date"));
            System.out.println("Average Rating: " + document.get("averageRating"));
            System.out.println("Count: " + document.getInteger("count"));
        }

        return userRatingDistribution;
    }    

    /**
     * Closes connections to MongoDB.
     */
    public void closeConnection() {
        userDAO.closeConnections();
        productDAO.closeConnections();
        reviewDAO.closeConnection();
    }
}
