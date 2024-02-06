package dao;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import static org.neo4j.driver.Values.parameters;

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
    
    public void closeNeo4jConnection() {
        neo4jSession.close();
        neo4jDriver.close();
    }
}
