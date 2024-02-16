package dao;

import model.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.neo4j.driver.exceptions.Neo4jException;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ProductDAO {
	
	private static final int MINIMUM_SEARCH_TERM_LENGTH = 3;
	private MongoClient mongoClient;
    private MongoDatabase database;
    public MongoCollection<Document> productsCollection;
    private Neo4jManager neo4jManager;
    private Map<String, Product> deletedProducts = new HashMap<>();
    
    /**
     * Constructor to initialize MongoDB and Neo4j connections.
     */
    public ProductDAO() {
        mongoClient = MongoClients.create("mongodb://10.1.1.20:27017,10.1.1.21:27017,10.1.1.22:27017/" + 
        								"?w=1&readPreferences=nearest&timeout=5000");
        database = mongoClient.getDatabase("SafeBite");
        productsCollection = database.getCollection("Products");
        neo4jManager = new Neo4jManager();
    }
    
    /**
     * Searches for products based on the given search term.
     *
     * @param searchTerm The term to search for in product names.
     * @return List of products matching the search term.
     */
    public List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();

        if (productsCollection != null) {
            try {
                long startTime = System.currentTimeMillis();

                if (searchTerm != null && searchTerm.length() >= MINIMUM_SEARCH_TERM_LENGTH) {
                    
                    Document textSearchQuery = new Document("$text", new Document("$search", searchTerm));

                    Document queryExplain = productsCollection.find(textSearchQuery).explain();
                    System.out.println("Query Execution Plan: " + queryExplain.toJson());

                    Document projection = new Document("product_name", 1)
                                           .append("image_url", 1);

                    for (Document productDoc : productsCollection.find(textSearchQuery).projection(projection)) {
                        
                        String productId = productDoc.getObjectId("_id").toString();
                        String productName = productDoc.getString("product_name");
                        String imageUrl = productDoc.getString("image_url");

                        Product product = new Product(productId, productName, imageUrl);
                        products.add(product);
                    }

                    long endTime = System.currentTimeMillis();

                    long totalTime = endTime - startTime;
                    System.out.println("MongoDB query took " + totalTime + " milliseconds.");
                } else {
                    System.out.println("Invalid search term. Check the length and try again.");
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

    /**
     * Retrieves product details by the given product ID.
     *
     * @param productId The unique identifier for the product.
     * @return Product object with details or null if not found.
     */
    public Product getProductById(String productId) {

        ObjectId objectId;
        try {
            objectId = new ObjectId(productId);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid ObjectId format for product ID: " + productId);
            return null;
        }

        Document query = new Document("_id", objectId);

        Document productDoc = productsCollection.find(query).first();

        if (productDoc != null) {
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
            
            return new Product(productId, productName, imageUrl, quantity, categories, ingredients,
            		allergens, traces, labels, brandOwner, brands, countries, entryTS, lastUpdator,
            		updateTS);
        } else {
            System.out.println("Product not found for ID: " + productId);
            return null;
        }
    }

    /**
     * Adds a new product to MongoDB and creates corresponding nodes and relationships in Neo4j.
     *
     * @param product The product to be added.
     * @return True if the product is added successfully, false otherwise.
     */
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

    /**
     * Adds a new product to MongoDB.
     *
     * @param product The product to be added to the database.
     */
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

        productsCollection.insertOne(productDoc);

        System.out.println("Product successfully added to MongoDB.");
        
        ObjectId insertedId = productDoc.getObjectId("_id");
        product.setId(insertedId.toString());
	}
    
	/**
     * Updates an existing product in MongoDB and updates corresponding nodes in Neo4j.
     *
     * @param product The product with updated information.
     * @return True if the update is successful, false otherwise.
     */
    public Boolean updateProduct(Product product) {
    	
    	try {
    		updateProductMongoDB(product);
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
    
    /**
     * Updates an existing product in MongoDB.
     *
     * @param product The product with updated information.
     */
    public void updateProductMongoDB(Product product) {
        Document toUpdate = createProductDocument(product);

        Document query = new Document("_id", new ObjectId(product.getId()));
        
        Document updateDoc = new Document("$set", toUpdate);

        productsCollection.updateOne(query, updateDoc);
    }
    
    /**
     * Reverts the update operation in MongoDB by restoring the original product data.
     *
     * @param productId The ID of the product to be reverted.
     * @param existingProductData The original data of the product before the update.
     */
    public void revertMongoDBProductUpdate(String productId, Product existingProductData) {
        if (existingProductData != null) {

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

            productsCollection.updateOne(new Document("_id", new ObjectId(productId)), update);
        }
    }

    /**
     * Deletes a product from MongoDB and its corresponding nodes in Neo4j.
     *
     * @param product The product to be deleted.
     * @return True if the deletion is successful, false otherwise.
     */
    public Boolean deleteProduct(Product product) {
    	try {
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
    
    
    /**
     * Retrieves existing product data from MongoDB based on the product ID.
     *
     * @param id The ID of the product to retrieve data for.
     * @return Product object with existing data or null if not found.
     */
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

	/**
     * Reverts the deletion operation in MongoDB by restoring the deleted product.
     *
     * @param productId The ID of the product to be reverted.
     */
	private void revertMongoDBProductDelete(String productId) {
		// TODO Auto-generated method stub
		if (deletedProducts.containsKey(productId)) {
            Product deletedProductData = deletedProducts.get(productId);
            productsCollection.insertOne(createProductDocument(deletedProductData));
            deletedProducts.remove(productId);
        }	
	}
	
	/**
     * Creates a MongoDB document from a Product object.
     *
     * @param product The Product object to create a MongoDB document for.
     * @return MongoDB Document representing the product.
     */
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

    /**
     * Deletes a product from MongoDB based on the product ID.
     *
     * @param productId The ID of the product to be deleted.
     */
	public void deleteProductMongoDB(String productId) {
		// TODO Auto-generated method stub
        ObjectId objectId = new ObjectId(productId);
        Document query = new Document("_id", objectId);

        productsCollection.deleteOne(query);
	}
	
	/**
     * Closes MongoDB and Neo4j connections.
     */
	public void closeConnections() {
        if (mongoClient != null) {
            mongoClient.close();
            neo4jManager.closeNeo4jConnection();
            System.out.println("DB connections closed successfully.");
        }
    }
}
