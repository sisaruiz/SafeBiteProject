package dao;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.Document;
import org.neo4j.driver.exceptions.Neo4jException;



public class UserDAO {
    private MongoClient mongoClient;
    private MongoDatabase database;
    public MongoCollection<Document> usersCollection;

    private Neo4jManager neo4jManager;
    // Temporary storage for deleted users
    private Map<String, User> deletedUsers = new HashMap<>();

    /**
     * Constructor to initialize MongoDB and Neo4j connections.
     */
    public UserDAO() {
        mongoClient = MongoClients.create("mongodb://10.1.1.20:27017,10.1.1.21:27017,10.1.1.22:27017/" 
        									+ "?w=1&readPreferences=nearest&timeout=5000");
        database = mongoClient.getDatabase("SafeBite");
        usersCollection = database.getCollection("Users");       
        neo4jManager = new Neo4jManager();
    }
    
    /**
     * Verify if a user is a dummy user using Neo4jManager.
     *
     * @param user Username to verify.
     * @return True if the user is a dummy user, false otherwise.
     */
    public Boolean verifyDummyUser(String user) {
    	return neo4jManager.verifyDummyUser(user);
    }
    
    /**
     * Find a user in MongoDB by username and password.
     *
     * @param un Username.
     * @param psw Password.
     * @return MongoDB Document representing the user.
     */
    public Document find(String un, String psw) {
    	return usersCollection.find(new Document("user_name", un).append("password", psw)).first();
    }

    /**
     * Get user details by username from MongoDB and construct a User object.
     *
     * @param username Username of the user.
     * @return User object with user details.
     */
    public User getUserByUsername(String username) {
        Document userDocument = usersCollection.find(new Document("user_name", username)).first();
        if (userDocument != null) {
            Document allergyDocument = userDocument.get("allergy", Document.class);
            
            List<String> allergens = null;
            if (allergyDocument != null) {
                Object allergensObject = allergyDocument.get("allergens");
                if (allergensObject instanceof List<?>) {
                    allergens = (List<String>) allergensObject;
                } else if (allergensObject instanceof String) {
                    // Handle the case where allergens is a single string, not a list
                    allergens = Collections.singletonList((String) allergensObject);
                }
            }
            
            User user = new User(username, userDocument.getString("email"), userDocument.getString("password"),
                    "https://i.ibb.co/0MCmLLM/360-F-353110097-nbpmfn9i-Hlxef4-EDIh-XB1td-TD0lc-Wh-G9.jpg",
                    userDocument.getString("country"), userDocument.getString("date_of_birth"), userDocument.getString("gender"),
                    userDocument.getString("diet_type"),
                    allergens);

            return user;
        }
        return null;
    }
    
    /**
     * Search users in MongoDB based on a search term.
     *
     * @param searchTerm Search term for username.
     * @return List of User objects matching the search term.
     */
    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();

        // Ensure usersCollection is not null
        if (usersCollection != null) {
            try {
                // Query the 'Products' collection for matching documents
                Pattern pattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);
                
                // Add a condition to exclude users with 'admin' set to true
                Document query = new Document("user_name", pattern)
                                        .append("admin", false);

                System.out.println("Constructed MongoDB Query: " + query.toJson());

                for (Document userDoc : usersCollection.find(query)) {
                    String userName = userDoc.getString("user_name");
                    // Create a Product instance
                    User user = new User(userName);
                    users.add(user);
                }
            } catch (Exception e) {
                System.out.println("Error executing search query:");
                e.printStackTrace();
            }
        } else {
            System.out.println("usersCollection is null. Check MongoDB connection.");
        }

        return users;
    }
    
    
    /**
     * Insert a new user into MongoDB and create a corresponding node in Neo4j.
     *
     * @param newUser MongoDB Document representing the new user.
     * @return True if the operation is successful, false otherwise.
     */
    public Boolean insertUser(Document newUser) {
        try {
            // MongoDB
        	usersCollection.insertOne(newUser);

            // Neo4j
            Neo4jManager neo4jManager = new Neo4jManager();
            neo4jManager.createNeo4jUserNode(newUser.getString("user_name"));

        } catch (MongoException mongoException) {
            // Handle MongoDB exception
            mongoException.printStackTrace();
            System.out.println("Error creating User in MongoDB: " + mongoException.getMessage());
            return false;
            
        } catch (Neo4jException neo4jException) {
            
            // Revert MongoDB operation (delete user)
            Document query = new Document("user_name", newUser.getString("user_name"));
    		// Delete the document from the MongoDB collection
            usersCollection.deleteOne(query);
            
            // Handle Neo4j exception
            neo4jException.printStackTrace();
            System.out.println("Error creating User: " + neo4jException.getMessage());
            return false;

        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
            System.out.println("Error creating User: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Check if a username already exists in MongoDB.
     *
     * @param username Username to check.
     * @return True if the username exists, false otherwise.
     */
    public boolean checkUsernameExists(String username) {
        // Check if username already exists in MongoDB collection
        Document existingUser = usersCollection.find(Filters.eq("user_name", username)).first();
        return existingUser != null;
    }
    
	
    /**
     * Update user profile in both MongoDB and Neo4j.
     *
     * @param user User object with updated information.
     * @return True if the operation is successful, false otherwise.
     */
	public Boolean updateUserProfile(User user) {
    	
        try {
            // MongoDB
            updateUserMongoDB(user);
            // Neo4j
            updateUserNeo4j(user);

        } catch (MongoException e) {
            // Handle MongoDB exception
            e.printStackTrace();
            System.out.println("Error updating user profile in MongoDB: " + e.getMessage());
            return false;

        } catch (Neo4jException e) {
            // Handle Neo4j exception
            e.printStackTrace();
            System.out.println("Error updating user profile in Neo4j: " + e.getMessage());
            revertMongoDBUserUpdate(user.getName(), getExistingUserData(user.getName()));
            return false;

        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
            System.out.println("Error updating user profile: " + e.getMessage());         
            return false;
        }
        
        return true;
    }
    
	/**
     * Retrieve existing user data from MongoDB.
     *
     * @param username Username of the user.
     * @return User object representing the existing user data.
     */
    public User getExistingUserData(String username) {
        Document userDocument = usersCollection.find(new Document("user_name", username)).first();
        if (userDocument != null) {
            Document allergyDocument = userDocument.get("allergy", Document.class);

            List<String> allergens = (allergyDocument != null) ? allergyDocument.getList("allergens", String.class) : null;

            return new User(username, userDocument.getString("email"), userDocument.getString("password"),
                    "https://i.ibb.co/0MCmLLM/360-F-353110097-nbpmfn9i-Hlxef4-EDIh-XB1td-TD0lc-Wh-G9.jpg",
                    userDocument.getString("country"), userDocument.getString("date_of_birth"),
                    userDocument.getString("gender"), userDocument.getString("diet_type"),
                    allergens);
        }
        return null;
    }
    
    /**
     * Revert the MongoDB user update operation using existing user data.
     *
     * @param username        Username of the user.
     * @param existingUserData User object representing the existing user data.
     */
    public void revertMongoDBUserUpdate(String username, User existingUserData) {
        if (existingUserData != null) {
            // Create an update document with the existing user information
            Document update = new Document("$set", new Document()
                    .append("diet_type", existingUserData.getDiet())
                    .append("allergy", new Document("allergens", existingUserData.getListAllergens())));

            // Update the document in the MongoDB collection with the existing data
            usersCollection.updateOne(new Document("user_name", username), update);
        }
    }
	
    /**
     * Update user profile in MongoDB based on the provided User object.
     *
     * @param user User object with updated information.
     */
	private void updateUserMongoDB(User user) {
		Document query = new Document("user_name", user.getName());
	    Document update = new Document("$set", new Document()
	                .append("diet_type", user.getDiet())
	                .append("allergy", new Document("allergens", user.getListAllergens())));
	        usersCollection.updateOne(query, update);
	}
	   
	/**
     * Update user profile in Neo4j based on the provided User object.
     *
     * @param user User object with updated information.
     */
	private void updateUserNeo4j(User user) {

	    neo4jManager.deleteNeo4jUserDietRelationship(user.getName());
	    neo4jManager.deleteNeo4jUserAllergenRelationships(user.getName());
	    neo4jManager.createNeo4jUserDietRelationship(user.getName(), user.getDiet());

	    if (user.getListAllergens() != null && !user.getListAllergens().isEmpty()) {
	        for (String allergen : user.getListAllergens()) {
	            neo4jManager.createNeo4jUserAllergyRelationship(user.getName(), allergen);
	        }
	    }
	}    
	
	/**
     * Delete a user from MongoDB and its corresponding node in Neo4j.
     *
     * @param username Username of the user to be deleted.
     * @return True if the operation is successful, false otherwise.
     */
    public Boolean deleteUser(String username) {
    	try {
            // MongoDB
            Document query = new Document("user_name", username);
            
            // Store the user information before deleting
            User deletedUserData = getExistingUserData(username);
            deletedUsers.put(username, deletedUserData);
            
    		// Delete the document from the MongoDB collection
            usersCollection.deleteOne(query);
            
            // Neo4j
            neo4jManager.deleteNeo4jUserNode(username);

        } catch (MongoException e) {
            e.printStackTrace();
            System.out.println("Error deleting user profile in MongoDB: " + e.getMessage());
            return false;

        } catch (Neo4jException e) {
            e.printStackTrace();
            System.out.println("Error deleting user profile in Neo4j: " + e.getMessage());
            revertMongoDBUserDelete(username);
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error deleting user profile: " + e.getMessage());  
            return false;
        }
    	return true;
    }
    
    /**
     * Revert the MongoDB user delete operation using stored user data.
     *
     * @param username Username of the user to be reverted.
     */
    public void revertMongoDBUserDelete(String username) {
        if (deletedUsers.containsKey(username)) {
            User deletedUserData = deletedUsers.get(username);
            usersCollection.insertOne(createUserDocument(deletedUserData));
            deletedUsers.remove(username);
        }
    }
    
    /**
     * Helper method to convert User object to MongoDB Document.
     *
     * @param user User object to be converted.
     * @return MongoDB Document representing the user.
     */
    private Document createUserDocument(User user) {
        return new Document("user_name", user.getName())
                .append("email", user.getEmail())
                .append("password", user.getPassword())
                .append("country", user.getCountry())
                .append("date_of_birth", user.getDOB())
                .append("gender", user.getGender())
                .append("diet_type", user.getDiet())
                .append("allergy", new Document("allergens", user.getListAllergens()));
    }
    
    /**
     * Close MongoDB and Neo4j connections.
     */
    public void closeConnections() {
        if (mongoClient != null) {
            mongoClient.close();
            neo4jManager.closeNeo4jConnection();
            System.out.println("DB connections closed successfully.");
        }
    }
}