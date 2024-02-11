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
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;

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

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
