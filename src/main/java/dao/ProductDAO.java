package dao;

import model.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;

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
    
    // Add a method to retrieve a product by its ID
    public Product getProductById(String productId) {

    	// Convert the string representation of ObjectId to ObjectId
        ObjectId objectId;
        try {
            objectId = new ObjectId(productId);
        } catch (IllegalArgumentException e) {
            // Handle invalid ObjectId format
            System.out.println("Invalid ObjectId format for product ID: " + productId);
            return null;
        }

        // Construct the query to find the product by ObjectId
        Document query = new Document("_id", objectId);

        // Execute the query and retrieve the product document
        Document productDoc = productsCollection.find(query).first();

        // Check if the product document is found
        if (productDoc != null) {
            // Extract fields from the document
            String productName = productDoc.getString("product_name");
            String imageUrl = productDoc.getString("image_url");
            String allergens = productDoc.getString("allergens");
            String brandOwner = productDoc.getString("brand_owner");
            String brands = productDoc.getString("brands");
            String countries = productDoc.getString("countries_en");
            Date entryTS = productDoc.getDate("created_datetime");
            String categories = productDoc.getString("food_groups_en");
            String ingredients = productDoc.getString("ingredients_tags");
            String labels = productDoc.getString("labels_tags");
            Date updateTS = productDoc.getDate("last_modified_datetime");
            String lastUpdator = productDoc.getString("last_modified_by");
            String quantity = productDoc.getString("quantity");
            String traces = productDoc.getString("traces_tags");
            
            // Create and return a Product instance
            return new Product(productId, productName, imageUrl, quantity, categories, ingredients,
            		allergens, traces, labels, brandOwner, brands, countries, entryTS, lastUpdator,
            		updateTS);
        } else {
            // Product not found
            System.out.println("Product not found for ID: " + productId);
            return null;
        }
    }
}
