from pymongo import MongoClient
from neo4j import GraphDatabase

# MongoDB connection
mongo_client = MongoClient("mongodb://localhost:27017/")
mongo_db = mongo_client["SafeBite"]
user_collection = mongo_db["Users"]

# Neo4j connection
neo4j_uri = "bolt://localhost:7687"  # Replace with your Neo4j URI
neo4j_user = "neo4j"
neo4j_password = "neo4jneo"

# Function to create Neo4j relationships
def create_neo4j_relationships(session, user_name, allergens):
    for allergen in allergens:
        # Cypher query to create relationship
        query = (
            f"MATCH (u:User {{user_name: '{user_name}'}}), (a:Allergen {{name: '{allergen}'}}) "
            "MERGE (u)-[:HAS_ALLERGEN]->(a)"
        )
        session.run(query)

# Connect to Neo4j
with GraphDatabase.driver(neo4j_uri, auth=(neo4j_user, neo4j_password)) as driver:
    # Iterate through User documents in MongoDB
    for user_doc in user_collection.find({"allergy.allergens": {"$exists": True}}):
        user_name = user_doc["user_name"]
        allergens = user_doc.get("allergy", {}).get("allergens", [])

        # Check if allergens is not empty
        if allergens:
            # Create Neo4j relationships
            with driver.session() as session:
                create_neo4j_relationships(session, user_name, allergens)