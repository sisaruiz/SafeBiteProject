from pymongo import MongoClient
from neo4j import GraphDatabase

# MongoDB connection details
mongo_uri = "mongodb://localhost:27017"
mongo_db_name = "SafeBite"
mongo_collection_name = "Products"

# Neo4j connection details
neo4j_uri = "bolt://localhost:7687"
neo4j_username = "neo4j"
neo4j_password = "neo4jneo"

# Connect to MongoDB
client = MongoClient(mongo_uri)
db = client[mongo_db_name]
collection = db[mongo_collection_name]

# Connect to Neo4j
driver = GraphDatabase.driver(neo4j_uri, auth=(neo4j_username, neo4j_password))

def create_relationship(tx, product_id, diet_type):
    query = (
        f"MATCH (p:Product {{id: $product_id}}), (d:Diet {{type: $diet_type}}) "
        "MERGE (p)-[:IS_COMPATIBLE_WITH]->(d)"
    )
    tx.run(query, product_id=product_id, diet_type=diet_type)

def process_halal_products():
    # Check for absence of "pork," "wine," or "alcohol" substrings in ingredients_tags
    products = collection.find({
        "$nor": [
            {"ingredients_tags": {"$regex": "pork"}},
            {"ingredients_tags": {"$regex": "wine"}},
            {"ingredients_tags": {"$regex": "alcohol"}}
        ]
    })

    with driver.session() as session:
        for product in products:
            product_id = str(product["_id"])
            diet_type = "halal"
            session.write_transaction(create_relationship, product_id, diet_type)


if __name__ == "__main__":
    process_halal_products()

# Close connections
client.close()
driver.close()
