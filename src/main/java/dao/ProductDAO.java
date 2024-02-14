package dao;

import model.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.neo4j.driver.exceptions.Neo4jException;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ProductDAO {
	
	private MongoClient mongoClient;
    private MongoDatabase database;
    public MongoCollection<Document> productsCollection;
    private Neo4jManager neo4jManager;
    private Map<String, Product> deletedProducts = new HashMap<>();

    public ProductDAO() {
        mongoClient = MongoClients.create("mongodb://10.1.1.20:27017,10.1.1.21:27017,10.1.1.22:27017/" + 
        								"?w=1&readPreferences=nearest&timeout=5000");
        database = mongoClient.getDatabase("SafeBite");
        productsCollection = database.getCollection("Products");
        neo4jManager = new Neo4jManager();
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
    
    
    // ADD PRODUCT
    
    public Boolean addProduct(Product product) {
    	try {
    		addProductMongoDB(product);
    		
            neo4jManager.createNeo4jProductNode(product.getId(), product.getName());
            neo4jManager.createNeo4jProductAllergensRelationship(product.getId(), product.getAllergens());
            neo4jManager.createDietCompatibilityRelationships(product.getId(), product.getIngredients());
            
    	} catch (MongoException e) {
            // Handle MongoDB exception
            e.printStackTrace();
            System.out.println("Error creating Product doc in MongoDB: " + e.getMessage());
            return false;
            
        } catch (Neo4jException e) {
            // Handle Neo4j exception
            e.printStackTrace();
            System.out.println("Error creating Product node in Neo4j: " + e.getMessage());
            neo4jManager.deleteNeo4jProductNode(product.getId());
            deleteProductMongoDB(product.getId());
            return false;
            
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();   
            System.out.println("Error creating Product: " + e.getMessage());  
            return false;
        }
    	return true;
    	
    }

	private void addProductMongoDB(Product product) {
		// TODO Auto-generated method stub
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

        System.out.println("Product successfully added to MongoDB.");
        
        // Retrieve the generated MongoDB ID and set it in the Product object
        ObjectId insertedId = productDoc.getObjectId("_id");
        product.setId(insertedId.toString());
	}
    
	
	// UPDATE PRODUCT
	
    public Boolean updateProduct(Product product) {
    	
    	try {
    		updateProductMongoDB(product);
    		// Update the product in Neo4j
            neo4jManager.updateNeo4jProductNode(product);
    	}
    	catch(MongoException e) {
    		e.printStackTrace();
            System.out.println("Error updating Product doc in MongoDB: " + e.getMessage());
            return false;
    	}
    	catch(Neo4jException e) {
    		revertMongoDBProductUpdate(product.getId(), getProductById(product.getId()));
    		e.printStackTrace();
            System.out.println("Error updating Product node in Neo4j: " + e.getMessage());
            return false;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
            System.out.println("Error updating Product: " + e.getMessage());
            return false;
    	}
    	return true;
    }
    
    public void updateProductMongoDB(Product product) {
        // Convert Product object to Document
        Document toUpdate = createProductDocument(product);

        // Construct the query to find the product by ObjectId
        Document query = new Document("_id", new ObjectId(product.getId()));
        
        Document updateDoc = new Document("$set", toUpdate);

        // Update the document in the 'Products' collection
        productsCollection.updateOne(query, updateDoc);
    }
    
    public void revertMongoDBProductUpdate(String productId, Product existingProductData) {
        if (existingProductData != null) {
            // Create an update document with the existing user information
            Document update = new Document("$set", new Document()
            		.append("product_name", existingProductData.getName())
                    .append("image_url", existingProductData.getImgURL())
                    .append("ingredients_tags", existingProductData.getIngredients())
                    .append("allergens", existingProductData.getAllergens())
                    .append("brands", existingProductData.getBrand())
                    .append("brand_owner", existingProductData.getBrandOwner())
                    .append("categories_tags", existingProductData.getCategories())
                    .append("countries_en", existingProductData.getCountries())
                    .append("labels_tags", existingProductData.getLabels())
                    .append("quantity", existingProductData.getQuantity())
                    .append("traces_tags", existingProductData.getTraces()));

            // Update the document in the MongoDB collection with the existing data
            productsCollection.updateOne(new Document("_id", new ObjectId(productId)), update);
        }
    }

    
    // DELETE PRODUCT
    
    public Boolean deleteProduct(Product product) {
    	try {
    		// Store the user information before deleting
            Product deletedProductData = getExistingProductData(product.getId());
            deletedProducts.put(product.getId(), deletedProductData);
    		deleteProductMongoDB(product.getId());
    		neo4jManager.deleteNeo4jProductNode(product.getId());
    	}
    	catch(MongoException e) {
    		e.printStackTrace();
            System.out.println("Error deleting Product doc in MongoDB: " + e.getMessage());
    		return false;
    	}
    	catch (Neo4jException e) {
    		e.printStackTrace();
            System.out.println("Error deleting Product node in Neo4j: " + e.getMessage());
            revertMongoDBProductDelete(product.getId());
    		return false;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
            System.out.println("Error deleting Product: " + e.getMessage());
    		return false;
    	}
    	return true;
    }
    
	private Product getExistingProductData(String id) {
		Document productDocument = productsCollection.find(new Document("_id", new ObjectId(id))).first();
        if (productDocument != null) {
           
            return new Product(id, productDocument.getString("product_name"), productDocument.getString("image_url"),
                    productDocument.getString("quantity"), productDocument.getString("categories_tags"),
                    productDocument.getString("ingredients_tags"), productDocument.getString("allergens"),
                    productDocument.getString("traces"), productDocument.getString("labels"),
                    productDocument.getString("brand_owner"), productDocument.getString("brands"),
                    productDocument.getString("countries_en"), productDocument.getDate("created_datetime"),
                    productDocument.getString("last_modified_by"), productDocument.getDate("last_modified_datetime"));
        }
        return null;
	}

	private void revertMongoDBProductDelete(String productId) {
		// TODO Auto-generated method stub
		if (deletedProducts.containsKey(productId)) {
            Product deletedProductData = deletedProducts.get(productId);
            // Insert the deleted user data back into the MongoDB collection
            productsCollection.insertOne(createProductDocument(deletedProductData));
            // Remove the user from the temporary storage
            deletedProducts.remove(productId);
        }	
	}
	
	// Helper method to convert User object to MongoDB Document
    private Document createProductDocument(Product product) {
        return new Document("product_name", product.getName())
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
                .append("last_modified_datetime", product.getLastUpdateTS())
                .append("creation_datetime", product.getEntryTS())
                .append("last_modified_by", product.getLastUpdateBy());
    }

	public void deleteProductMongoDB(String productId) {
		// TODO Auto-generated method stub
        ObjectId objectId = new ObjectId(productId);
        Document query = new Document("_id", objectId);

        // Delete the document from the 'Products' collection
        productsCollection.deleteOne(query);
	}
	
	public void closeConnections() {
        if (mongoClient != null) {
            mongoClient.close();
            neo4jManager.closeNeo4jConnection();
            System.out.println("DB connections closed successfully.");
        }
    }
}
