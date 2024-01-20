package dao;

import model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ProductDAO {
	
	private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> productsCollection;

    public ProductDAO() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("SafeBite");
        productsCollection = database.getCollection("Products");
    }
    
    public List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();

        // Ensure productsCollection is not null
        if (productsCollection != null) {
            try {
                // Query the 'Products' collection for matching documents
                Pattern pattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);
                Document query = new Document("product_name", pattern);
                System.out.println("Constructed MongoDB Query: " + query.toJson());

                for (Document productDoc : productsCollection.find(query)) {
                    // Extract fields from the Document
                    String productId = productDoc.getObjectId("_id").toString();
                    String productName = productDoc.getString("product_name");
                    String imageUrl = productDoc.getString("image_url");

                    // Create a Product instance
                    Product product = new Product(productId, productName, imageUrl);
                    products.add(product);
                }
            } catch (Exception e) {
                System.out.println("Error executing search query:");
                e.printStackTrace();
            }
        } else {
            System.out.println("productsCollection is null. Check MongoDB connection.");
        }

        return products;
    }

}
