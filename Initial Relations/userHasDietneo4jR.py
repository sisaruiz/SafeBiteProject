from neo4j import GraphDatabase
from pymongo import MongoClient

# Assuming you have a MongoDB connection
mongo_client = MongoClient('mongodb://localhost:27017')
mongo_database = mongo_client['SafeBite']
mongo_collection = mongo_database['Users']

# Neo4j connection details
neo4j_uri = "bolt://localhost:7687"
neo4j_user = "neo4j"
neo4j_password = "neo4jneo"

# Connect to Neo4j
neo4j_driver = GraphDatabase.driver(neo4j_uri, auth=(neo4j_user, neo4j_password))

# Cypher query to create relationships
cypher_query = """
MATCH (u:User {user_name: $user_name}), (d:Diet {type: $diet_type})
MERGE (u)-[:HAS_DIET]->(d)
"""

# Iterate through MongoDB documents
for mongo_document in mongo_collection.find():
    user_name = mongo_document['user_name']
    diet_type = mongo_document['diet_type']

    # Execute Cypher query
    with neo4j_driver.session() as session:
        session.run(cypher_query, user_name=user_name, diet_type=diet_type)

# Close Neo4j driver
neo4j_driver.close()