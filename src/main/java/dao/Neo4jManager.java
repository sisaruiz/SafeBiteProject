package dao;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;
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
        neo4jSession.run("CREATE (:User {user_name: $user_name})", parameters("user_name", userName));
    }

    public void createNeo4jUserAllergyRelationship(String userName, String allergen) {
        neo4jSession.run("MATCH (u:User {user_name: $userName}), (a:Allergen {name: $allergen}) CREATE (u)-[:HAS_ALLERGEN]->(a)",
                parameters("userName", userName, "allergen", allergen));
    }

    public void createNeo4jUserDietRelationship(String userName, String dietType) {
        neo4jSession.run("MATCH (u:User {user_name: $user_name}), (d:Diet {type: $dietType}) CREATE (u)-[:HAS_DIET]->(d)",
                parameters("user_name", userName, "dietType", dietType));
    }
    
    public void deleteNeo4jUserDietRelationship(String userName) {
        neo4jSession.run("MATCH (u:User {user_name: $user_name})-[r:HAS_DIET]->(d:Diet) DELETE r",
                parameters("user_name", userName));
    }

    public void deleteNeo4jUserAllergenRelationships(String userName) {
        neo4jSession.run("MATCH (u:User {user_name: $user_name})-[r:HAS_ALLERGEN]->(a:Allergen) DELETE r",
                parameters("user_name", userName));
    }
    
    public void deleteNeo4jUserNodes(String userName) {
        neo4jSession.run("MATCH (u:User {user_name: $user_name}) DETACH DELETE u",
                parameters("user_name", userName));
    }
    
    public void createNeo4jRelationshipsFromUserToAllergens(String userName, String[] allergenNames) {
    	
    	for (String allergen : allergenNames) {
    	    createNeo4jUserAllergyRelationship(userName, allergen);
    	}
    }
    
    public void createNeo4jProductNode(String productId, String productName) {
        neo4jSession.run("CREATE (:Product {id: $productId, name: $productName})", parameters("productId", productId, "productName", productName));
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

    
    public void updateNeo4jProductNode(String productId, String productName) {
        try (Transaction transaction = neo4jSession.beginTransaction()) {
            transaction.run("MATCH (p:Product {id: $productId}) SET p.name = $productName",
                    parameters("productId", productId, "productName", productName));
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error updating Neo4j Product Node: " + e.getMessage());
        }
    }
    
    public void createNeo4jUserProductLikeRelationship(String userName, String productId) {
        try (Session independentSession = neo4jDriver.session()) {
            try (Transaction transaction = independentSession.beginTransaction()) {
                transaction.run("MATCH (u:User {user_name: $userName}), (p:Product {id: $productId}) CREATE (u)-[:LIKES]->(p)",
                        parameters("userName", userName, "productId", productId));
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error creating Neo4j User-Product Like Relationship: " + e.getMessage());
        }
    }

    public void deleteNeo4jUserProductLikeRelationship(String userName, String productId) {
        try (Session independentSession = neo4jDriver.session()) {
            try (Transaction transaction = independentSession.beginTransaction()) {
                transaction.run("MATCH (u:User {user_name: $userName})-[r:LIKES]->(p:Product {id: $productId}) DELETE r",
                        parameters("userName", userName, "productId", productId));
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error deleting Neo4j User-Product Like Relationship: " + e.getMessage());
        }
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
        neo4jSession.run("MATCH (p:Product {id: $productId}) DETACH DELETE p", parameters("productId", productId));
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

    
    public void closeNeo4jConnection() {
        neo4jSession.close();
        neo4jDriver.close();
    }
    
}
