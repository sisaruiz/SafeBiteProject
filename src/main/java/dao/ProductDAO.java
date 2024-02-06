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
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

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
            String categories = productDoc.getString("food_groups_en") != null ? productDoc.getString("food_groups_en") : productDoc.getString("categories_tags");
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
    
 // Method to add a new product to the MongoDB dataset
    public void addProduct(Product product) {
        try {
            // Convert Product object to Document
            Document productDoc = new Document("product_name", product.getName())
                    .append("image_url", product.getImgURL())
                    .append("ingredients_tags", product.getIngredients())
                    .append("allergens", product.getAllergens())
                    .append("brands", product.getBrand())
                    .append("brand_owner", product.getBrandOwner())
                    .append("categories_tags", product.getCategories())
                    .append("countries_en", product.getCountries())
                    .append("labels_tags", product.getLabels())
                    .append("quantity", product.getQuantity())
                    .append("traces_tags", product.getTraces())
                    .append("created_datetime", product.getEntryTS())
                    .append("last_modified_datetime", product.getLastUpdateTS())
                    .append("last_modified_by", product.getLastUpdateBy());

            // Insert the document into the 'Products' collection
            productsCollection.insertOne(productDoc);

            System.out.println("Product added successfully!");
            
         // Retrieve the generated MongoDB ID and set it in the Product object
            ObjectId insertedId = productDoc.getObjectId("_id");
            product.setId(insertedId.toString());

        }catch (Exception e) {
            System.out.println("Error adding product to MongoDB:");
            e.printStackTrace();
        }
    }
    
    public void updateProduct(Product product) {
        try {
            // Convert Product object to Document
            Document updateDoc = new Document("$set", new Document("product_name", product.getName())
                    .append("image_url", product.getImgURL())
                    .append("ingredients_tags", product.getIngredients())
                    .append("allergens", product.getAllergens())
                    .append("brands", product.getBrand())
                    .append("brand_owner", product.getBrandOwner())
                    .append("categories_tags", product.getCategories())
                    .append("countries_en", product.getCountries())
                    .append("labels_tags", product.getLabels())
                    .append("quantity", product.getQuantity())
                    .append("traces_tags", product.getTraces())
                    .append("last_modified_datetime", new Date())
                    .append("last_modified_by", product.getLastUpdateBy()));

            // Construct the query to find the product by ObjectId
            Document query = new Document("_id", new ObjectId(product.getId()));

            // Update the document in the 'Products' collection
            UpdateResult updateResult = productsCollection.updateOne(query, updateDoc);

            // Check if the update was successful
            if (updateResult.getModifiedCount() > 0) {
                System.out.println("Product updated successfully!");
            } else {
                System.out.println("Product update failed. Product not found for ID: " + product.getId());
            }
        } catch (Exception e) {
            System.out.println("Error updating product in MongoDB:");
            e.printStackTrace();
        }
    }

	public void deleteProduct(String productId) {
		// TODO Auto-generated method stub
		try {
            // Convert the string representation of ObjectId to ObjectId
            ObjectId objectId = new ObjectId(productId);

            // Construct the query to find the product by ObjectId
            Document query = new Document("_id", objectId);

            // Delete the document from the 'Products' collection
            DeleteResult deleteResult = productsCollection.deleteOne(query);

            // Check if the deletion was successful
            if (deleteResult.getDeletedCount() > 0) {
                System.out.println("Product deleted successfully!");
            } else {
                System.out.println("Product deletion failed. Product not found for ID: " + productId);
            }
        } catch (Exception e) {
            System.out.println("Error deleting product from MongoDB:");
            e.printStackTrace();
        }
		
	}
}
