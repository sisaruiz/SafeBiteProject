package mongo;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Sorts.*;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoAggregations {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoAggregations(String connectionString, String databaseName, String collectionName) {
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection(collectionName);
    }

    //Retrieves the aggregate result for the count of products in each category.
    public AggregateIterable<Document> getProductCategoryCounts() {
        // Perform aggregation using the MongoDB Java driver
        return collection.aggregate(Arrays.asList(
                // Group products by the 'main_category' field and calculate the count of each group
                group("$main_category", sum("count", 1)),

                sort(descending("count")),
                limit(10),
                
                // Group the limited results by the '_id' (main_category) field and recalculate the count
                group("$_id", sum("count", "$count")),

                // Sort the final results in descending order based on the count
                sort(descending("count"))
        ));
    }
    
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
        
        List<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());
        
        // Print the results for debugging
        System.out.println("Aggregation Results: " + results);
        
        return results;
    }

    
    
    public static List<Document> getMostReviewedProducts(MongoCollection<Document> reviewsCollection, int limit) {
        // Aggregation pipeline
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

        // Execute the aggregation pipeline
        AggregateIterable<Document> aggregationResult = reviewsCollection.aggregate(pipeline);

        // Convert the result to a list of documents
        List<Document> mostReviewedProducts = new ArrayList<>();
        for (Document document : aggregationResult) {
            mostReviewedProducts.add(document);
        }

        return mostReviewedProducts;
    }
    
    public List<Document> getTopReviewersAndAverageRating(int limit) {
        // Aggregation pipeline
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

        // Execute the aggregation pipeline
        AggregateIterable<Document> aggregationResult = collection.aggregate(pipeline);

        // Convert the result to a list of documents
        List<Document> topReviewersAndAverageRating = new ArrayList<>();
        for (Document document : aggregationResult) {
            topReviewersAndAverageRating.add(document);
        }

        return topReviewersAndAverageRating;
    }

    
    public MongoCollection<Document> getCollection() {
        return this.collection;
    }


    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
