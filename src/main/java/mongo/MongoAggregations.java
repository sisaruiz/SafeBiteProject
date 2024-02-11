package mongo;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Sorts.descending;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
<<<<<<< Updated upstream
=======
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.*;
>>>>>>> Stashed changes



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

    public AggregateIterable<Document> getProductCategoryCounts() {
        return collection.aggregate(Arrays.asList(
                group("$main_category", sum("count", 1)),
                sort(descending("count")),
                limit(10),
                group("$_id", sum("count", "$count")),
                sort(descending("count"))
        ));
    }
    
    public List<Document> getProductCountByBrandAndCountry() {
        List<Bson> pipeline = Arrays.asList(
                // Match documents with both brands and countries_en fields
                match(and(exists("brands"), exists("countries_en"))),

                // Group by brand owner and country and count the number of products
                group(and(eq("brands", "$brands"), eq("country", "$countries_en")),
                        sum("productCount", 1)),

                // Project the results to rename the _id field and include the productCount
                project(fields(
                	    computed("brandOwner", "$_id.brands"),
                	    computed("country", "$_id.country"),
                	    include("productCount"),
                	    excludeId()))
        );

        MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator();
        List<Document> results = new ArrayList<>();
        while (cursor.hasNext()) {
            results.add(cursor.next());
        }
        return results;
    }
    

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
