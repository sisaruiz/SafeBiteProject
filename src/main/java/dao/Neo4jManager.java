package dao;

import org.neo4j.driver.AuthTokens;

import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import static org.neo4j.driver.Values.parameters;

import model.Product;
import java.util.ArrayList;
import java.util.List;

public class Neo4jManager {
	
    private Driver neo4jDriver;
    private Session neo4jSession;

    
    public Neo4jManager() {
        neo4jDriver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4jneo"));
        neo4jSession = neo4jDriver.session();
    }
    
    public void createNeo4jUserNode(String userName) {
        Transaction transaction = neo4jSession.beginTransaction();
        transaction.run("CREATE (:User {user_name: $user_name})", parameters("user_name", userName));
        transaction.commit();
    }

    public void createNeo4jUserAllergyRelationship(String userName, String allergen) {
        Transaction transaction = neo4jSession.beginTransaction();
        transaction.run("MATCH (u:User {user_name: $userName}), (a:Allergen {name: $allergen}) CREATE (u)-[:HAS_ALLERGEN]->(a)",
                    parameters("userName", userName, "allergen", allergen));
        transaction.commit();
    }

    public void createNeo4jUserDietRelationship(String userName, String dietType) {
        Transaction transaction = neo4jSession.beginTransaction();
        transaction.run("MATCH (u:User {user_name: $user_name}), (d:Diet {type: $dietType}) CREATE (u)-[:HAS_DIET]->(d)",
                    parameters("user_name", userName, "dietType", dietType));
        transaction.commit();
    }
    
    public void deleteNeo4jUserDietRelationship(String userName) {
        Transaction transaction = neo4jSession.beginTransaction();
        transaction.run("MATCH (u:User {user_name: $user_name})-[r:HAS_DIET]->(d:Diet) DELETE r",
                    parameters("user_name", userName));
        transaction.commit();
    }

    public void deleteNeo4jUserAllergenRelationships(String userName) {
        Transaction transaction = neo4jSession.beginTransaction();
        transaction.run("MATCH (u:User {user_name: $user_name})-[r:HAS_ALLERGEN]->(a:Allergen) DELETE r",
                    parameters("user_name", userName));
        transaction.commit();
    }
    
    public void deleteNeo4jUserNode(String userName) {
        Transaction transaction = neo4jSession.beginTransaction();
        transaction.run("MATCH (u:User {user_name: $user_name}) DETACH DELETE u",
                    parameters("user_name", userName));
        transaction.commit();
    }
    
    public void createNeo4jProductNode(String productId, String productName) {
        Transaction transaction = neo4jSession.beginTransaction();
        transaction.run("CREATE (:Product {id: $productId, name: $productName})", parameters("productId", productId, "productName", productName));
        transaction.commit();
    }

    
    public void createNeo4jFollowRelationship(String userFrom, String userTo) {
        neo4jSession.run(
                "MATCH (from:User {user_name: $userFrom}), (to:User {user_name: $userTo}) " +
                        "MERGE (from)-[:FOLLOWS]->(to)",
                parameters("userFrom", userFrom, "userTo", userTo)
        );
    }
    
    public void deleteNeo4jFollowRelationship(String userFrom, String userTo) {
        neo4jSession.run(
                "MATCH (from:User {user_name: $userFrom})-[r:FOLLOWS]->(to:User {user_name: $userTo}) " +
                        "DELETE r",
                parameters("userFrom", userFrom, "userTo", userTo)
        );
    }

    
    public void updateNeo4jProductNode(Product product) {
        Transaction transaction = neo4jSession.beginTransaction();
        
        // Delete all previous relationships of the node
        transaction.run("MATCH (p:Product {id: $productId})-[r]-() DELETE r", parameters("productId", product.getId()));

        // Update product name
        transaction.run("MATCH (p:Product {id: $productId}) SET p.name = $productName",
                    parameters("productId", product.getId(), "productName", product.getName()));

        transaction.commit();
        
        // Create new relationships
        createNeo4jProductAllergensRelationship(product.getId(), product.getAllergens());
        createDietCompatibilityRelationships(product.getId(), product.getIngredients());

    }
    
    public void createNeo4jUserProductLikeRelationship(String userName, String productId) {
        Session independentSession = neo4jDriver.session();
        Transaction transaction = independentSession.beginTransaction();
        transaction.run("MATCH (u:User {user_name: $userName}), (p:Product {id: $productId}) CREATE (u)-[:LIKES]->(p)",
        		parameters("userName", userName, "productId", productId));
        transaction.commit();
    }

    public void deleteNeo4jUserProductLikeRelationship(String userName, String productId) {
        Session independentSession = neo4jDriver.session();
        Transaction transaction = independentSession.beginTransaction();
        transaction.run("MATCH (u:User {user_name: $userName})-[r:LIKES]->(p:Product {id: $productId}) DELETE r",
                        parameters("userName", userName, "productId", productId));
        transaction.commit();
    }

    
    public boolean checkNeo4jFollowRelationship(String userFrom, String userTo) {
    	try {
            Result result = neo4jSession.run(
                    "MATCH (:User {user_name: $user1})-[:FOLLOWS]->(:User {user_name: $user2}) " +
                            "RETURN EXISTS((:User {user_name: $user1})-[:FOLLOWS]->(:User {user_name: $user2})) AS relationshipExists",
                    parameters("user1", userFrom, "user2", userTo)
            );

            return result.single().get("relationshipExists").asBoolean();
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return false;
        }
    }
    
    public List<String> findPotentialFriends(String userName) {
        List<String> potentialFriends = new ArrayList<>();

        try {
            Result result = neo4jSession.run(
                "MATCH (user:User {user_name: $userName})-[:FOLLOWS]->(friend)-[:FOLLOWS]->(potentialFriend) " +
                "WHERE NOT (user)-[:FOLLOWS]->(potentialFriend) " +
                "RETURN DISTINCT potentialFriend.user_name AS potentialFriendName",
                Values.parameters("userName", userName)
            );

            while (result.hasNext()) {
                Record record = result.next();
                String potentialFriendName = record.get("potentialFriendName").asString();
                potentialFriends.add(potentialFriendName);
            }

            // Check if the list is empty and return null in such cases
            return potentialFriends.isEmpty() ? null : potentialFriends;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error finding potential friends: " + e.getMessage());
            return null;
        }
    }
    
    public boolean checkNeo4jUserProductLikeRelationship(String userName, String productId) {
        try {
            String cypherQuery = "MATCH (:User {user_name: $userName})-[:LIKES]->(:Product {id: $productId}) " +
                    "RETURN EXISTS((:User {user_name: $userName})-[:LIKES]->(:Product {id: $productId})) AS relationshipExists";

            Result result = neo4jSession.run(cypherQuery, parameters("userName", userName, "productId", productId));

            // Print the actual Cypher query and parameters
            System.out.println("Cypher Query: " + cypherQuery);
            System.out.println("Parameters: " + parameters("userName", userName, "productId", productId));

            if (result.hasNext()) {
                Record record = result.single();

                // Print the result of the Cypher query
                System.out.println("Relationship Exists: " + record.get("relationshipExists").asBoolean());

                return record.get("relationshipExists").asBoolean();
            } else {
                // No record found, relationship does not exist
                System.out.println("Relationship Does Not Exist");
                return false;
            }
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return false;
        }
    }
    

    public void deleteNeo4jProductNode(String productId) {
        Transaction transaction = neo4jSession.beginTransaction();
        transaction.run("MATCH (p:Product {id: $productId}) DETACH DELETE p", parameters("productId", productId));
        transaction.commit();
    }
    
    public List<String> getUserFollowers(String userName) {
        List<String> followers = new ArrayList<>();

        try {
            Result result = neo4jSession.run(
                    "MATCH (follower:User)-[:FOLLOWS]->(:User {user_name: $userName}) RETURN follower.user_name AS followerName",
                    Values.parameters("userName", userName)
            );

            while (result.hasNext()) {
                Record record = result.next();
                followers.add(record.get("followerName").asString());
            }

            return followers;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting followers: " + e.getMessage());
            return null;
        }
    }

    public List<String> getUserFollowing(String userName) {
        List<String> following = new ArrayList<>();

        try {
            Result result = neo4jSession.run(
                    "MATCH (:User {user_name: $userName})-[:FOLLOWS]->(followee:User) RETURN followee.user_name AS followeeName",
                    Values.parameters("userName", userName)
            );

            while (result.hasNext()) {
                Record record = result.next();
                following.add(record.get("followeeName").asString());
            }

            return following;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting following: " + e.getMessage());
            return null;
        }
    }
    
    public void createNeo4jProductAllergensRelationship(String productId, String allergensContained) {
        // Split the allergens string into an array of allergen names
        String[] allergenNames = {"Celery", "Wheat", "Rye", "Barley", "Oats", "Spelt", "Kamut", "Cabbage", 
                                  "Broccoli", "Cauliflower", "Kale", "Brussel Sprouts", "Collard Greens", 
                                  "Crustaceans", "Eggs", "Fish", "Milk", "Lupin", "Molluscs", "Mustard", 
                                  "Nuts", "Peanuts", "Sesame", "Soy", "Apple", "Tomato", "Peaches"};

        // Iterate over each allergen name and check if it is contained in the input string
        for (String allergenName : allergenNames) {
            if (allergensContained.toLowerCase().contains(allergenName.toLowerCase())) {
                // If contained, create a relationship between the Product and Allergen nodes
                Transaction transaction = neo4jSession.beginTransaction();
                transaction.run("MATCH (p:Product {id: $productId}), (a:Allergen {name: $allergenName}) " +
                                "CREATE (p)-[:CONTAINS]->(a)",
                                parameters("productId", productId, "allergenName", allergenName.toLowerCase()));
                transaction.commit();
            }
        }
    }

    
    public List<String> findRecommendedProducts(String userName) {
        List<String> recommendedProducts = new ArrayList<>();

        try {
        	Result result = neo4jSession.run(
        		    "MATCH (user:User {user_name: $userName})-[:HAS_DIET]->(diet:Diet) " +
        		    "OPTIONAL MATCH (user)-[:HAS_ALLERGEN]->(allergen:Allergen) " +
        		    "MATCH (product:Product)" +
        		    "WHERE " +
        		    "((diet.type = 'none' AND NOT (allergen IS NOT NULL AND (product)-[:CONTAINS]->(allergen)) AND NOT (user)-[:LIKES]->(product)) " +
        		    "OR " +
        		    "(diet.type <> 'none' AND (product)-[:IS_COMPATIBLE_WITH]->(diet) AND NOT (allergen IS NOT NULL AND (product)-[:CONTAINS]->(allergen)) AND NOT (user)-[:LIKES]->(product))) " +
        		    "RETURN DISTINCT product.id AS productId, product.name AS productName " +
        		    "LIMIT 10",
        		    Values.parameters("userName", userName)
        		);


        	while (result.hasNext()) {
                Record record = result.next();
                String productId = record.get("productId").asString();
                String productName = record.get("productName").asString();
                String combined = productId + "-" + productName;
                recommendedProducts.add(combined);
            }

            return recommendedProducts;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error finding recommended products: " + e.getMessage());
            return null;
        }
    }
    

    public List<Product> getLikedProductsForUser(String userName) {
        List<Product> likedProducts = new ArrayList<>();

        try {
            Result result = neo4jSession.run(
                "MATCH (:User {user_name: $userName})-[:LIKES]->(product:Product) RETURN product.id AS productId, product.name AS productName",
                Values.parameters("userName", userName)
            );

            while (result.hasNext()) {
                Record record = result.next();
                String productId = record.get("productId").asString();
                String productName = record.get("productName").asString();
                
                // Create a simplified Product instance with 'id' and 'name'
                Product product = new Product(productId, productName, null);
                likedProducts.add(product);
            }

            return likedProducts;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting liked products for user: " + e.getMessage());
            return null;
        }
    }

    
    // Function to get the most popular users with followers count
    public List<String[]> getMostPopularUsersWithFollowersCount() {
        List<String[]> popularUsersWithFollowers = new ArrayList<>();

        try (Session session = neo4jDriver.session()) {
            String cypherQuery = "MATCH (u:User)-[:FOLLOWS]->(follower:User) " +
                                 "RETURN u.user_name AS username, COUNT(follower) AS followers " +
                                 "ORDER BY followers DESC LIMIT 5";

            List<Record> result = session.run(cypherQuery).list();

            for (Record record : result) {
                String username = record.get("username").asString();
                long followersCount = record.get("followers").asLong();
                popularUsersWithFollowers.add(new String[]{username, String.valueOf(followersCount)});
            }
        }

        return popularUsersWithFollowers;
    }
    
    
 // Function to get the most followed diets and the count of users for each diet
    public List<String[]> getMostFollowedDietsWithUserCount() {
        List<String[]> mostFollowedDietsWithUserCount = new ArrayList<>();

        try (Session session = neo4jDriver.session()) {
            String cypherQuery = "MATCH (u:User)-[:HAS_DIET]->(diet:Diet) " +
                                 "RETURN diet.type AS dietType, COUNT(u) AS userCount " +
                                 "ORDER BY userCount DESC";

            List<Record> result = session.run(cypherQuery).list();

            for (Record record : result) {
                String dietType = record.get("dietType").asString();
                long userCount = record.get("userCount").asLong();
                mostFollowedDietsWithUserCount.add(new String[]{dietType, String.valueOf(userCount)});
            }
        }

        return mostFollowedDietsWithUserCount;
    }
    
    public List<String[]> getAllergensWithUserCount() {
        List<String[]> allergensWithUserCount = new ArrayList<>();

        try (Session session = neo4jDriver.session()) {
            String cypherQuery = "MATCH (u:User)-[:HAS_ALLERGEN]->(allergen:Allergen) " +
                                 "RETURN allergen.name AS allergenName, COUNT(u) AS userCount " +
                                 "ORDER BY userCount DESC LIMIT 10";

            List<Record> result = session.run(cypherQuery).list();

            for (Record record : result) {
                String allergenName = record.get("allergenName").asString();
                long userCount = record.get("userCount").asLong();
                allergensWithUserCount.add(new String[]{allergenName, String.valueOf(userCount)});
            }
        }

        return allergensWithUserCount;
    }
    
    
    public boolean verifyDummyUser(String userName) {
    	try {
            Result result = neo4jSession.run(
                "MATCH (u:User {user_name: $userName})-[]->() RETURN COUNT(*) = 0 AS hasNoRelationships",
                parameters("userName", userName)
            );

            return result.single().get("hasNoRelationships").asBoolean();
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return false;
        }
    }

    public void createDietCompatibilityRelationships(String productId, String ingredients) {
        // Define the diet types
        String[] dietTypes = {"vegan", "vegetarian", "halal", "pescatarian"};

        // Iterate over each diet type and check if it's compatible with the ingredients
        for (String dietType : dietTypes) {
            // Check if the ingredients string contains any non-compatible ingredient for the current diet type
            boolean isCompatible = checkCompatibility(ingredients, dietType);

            if (isCompatible) {
                // If compatible, create a relationship between the Product and Diet nodes
                Transaction transaction = neo4jSession.beginTransaction();
                transaction.run("MATCH (p:Product {id: $productId}), (d:Diet {type: $dietType}) " +
                                "CREATE (p)-[:IS_COMPATIBLE_WITH]->(d)",
                                parameters("productId", productId, "dietType", dietType));
                transaction.commit();
            }
        }
    }

    // Helper method to check compatibility with diet type based on ingredients
    private boolean checkCompatibility(String ingredients, String dietType) {
        // Define non-compatible ingredients for each diet type
        String[] nonCompatibleIngredients = getNonCompatibleIngredients(dietType);

        // Check if any non-compatible ingredient is present in the ingredients string
        for (String ingredient : nonCompatibleIngredients) {
            if (ingredients.toLowerCase().contains(ingredient.toLowerCase())) {
                return false; // Not compatible
            }
        }

        return true; // Compatible
    }

    // Helper method to get non-compatible ingredients for a specific diet type
    private String[] getNonCompatibleIngredients(String dietType) {
        switch (dietType) {
            case "vegan":
                return new String[]{"meat", "fish", "tuna", "crustaceans", "cow", "pork", "chicken", "dairy", "milk", "eggs", "honey"};
            case "vegetarian":
                return new String[]{"meat", "cow", "pork", "chicken", "fish", "tuna", "crustaceans"};
            case "halal":
                return new String[]{"pork", "alcohol", "wine"};
            case "pescatarian":
                return new String[]{"meat", "chicken", "pork"};
            default:
                return new String[0]; // Default to an empty array
        }
    }

    
    public void closeNeo4jConnection() {
        neo4jSession.close();
        neo4jDriver.close();
    }
    
}
