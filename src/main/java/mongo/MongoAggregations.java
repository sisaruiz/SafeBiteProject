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
    	List<Bson> pipeline = Arrays.asList(
    		    // Match documents with both brands and countries_en fields
    		    match(and(exists("brands"), exists("countries_en"))),

    		    // Group by brand owner and country and count the number of products
    		    group(and(eq("brands", "$brands"), eq("countries_en", "$countries_en")),
    		            sum("productCount", 1)),

    		    // Project the results to rename the _id field and include the productCount
    		    project(fields(
    		        computed("brandOwner", "$_id.brands"),
    		        computed("country", "$_id.countries_en"),
    		        include("productCount"),
    		        excludeId())),

    		    // Sort the results if needed
    		    sort(ascending("brandOwner", "country"))
    		);

        MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator();
        List<Document> results = new ArrayList<>();
        while (cursor.hasNext()) {
            results.add(cursor.next());
        }
        return results;
    }
    
    
    public static Document getMostReviewedProduct(MongoCollection<Document> reviewsCollection) {
        // Aggregation pipeline
        List<Document> pipeline = Arrays.asList(
            new Document("$group", new Document("_id",
                new Document("ProductID", "$Product ID").append("ProductName", "$Product Name"))
                .append("reviewCount", new Document("$sum", 1))),
            new Document("$sort", new Document("reviewCount", -1)),
            new Document("$limit", 1),
            new Document("$project", new Document("_id", 0)
                .append("ProductID", "$_id.ProductID")
                .append("ProductName", "$_id.ProductName")
                .append("reviewCount", 1))
        );

        // Execute the aggregation pipeline
        AggregateIterable<Document> aggregationResult = reviewsCollection.aggregate(pipeline);

        // Get the result as a Document
        return aggregationResult.first();
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
