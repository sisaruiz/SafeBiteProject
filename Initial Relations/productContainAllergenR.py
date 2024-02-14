from pymongo import MongoClient
from py2neo import Graph, Node, Relationship

# Connect to MongoDB
mongo_client = MongoClient('mongodb://localhost:27017')
mongodb = mongo_client['SafeBite']
products_collection = mongodb['Products']

# Connect to Neo4j
neo4j_graph = Graph("bolt://localhost:7687", auth=("neo4j", "neo4jneo"))

# List of Allergen names
allergen_names = [
    'celery', 'wheat', 'rye', 'barley', 'oats', 'spelt', 'kamut',
    'cabbage', 'broccoli', 'cauliflower', 'kale', 'brussel sprouts', 'collard greens',
    'crustaceans', 'eggs', 'fish', 'milk', 'lupin', 'molluscs', 'mustard',
    'nuts', 'peanuts', 'sesame', 'soy', 'apple', 'tomato', 'peaches'
]

# Iterate through Products collection in MongoDB
for product_doc in products_collection.find():
    # Use MongoDB _id as a unique identifier
    product_id = str(product_doc['_id'])

    # Create or retrieve Product node in Neo4j based on the MongoDB _id
    product_node = neo4j_graph.nodes.match("Product", id=product_id).first() or Node("Product", id=product_id)

    # Check each field for allergens
    for allergen_name in allergen_names:
        for field in ['ingredients_tags', 'allergens', 'traces_tags']:
            if field in product_doc and allergen_name in product_doc[field]:
                # Create a relationship CONTAINS between Product and Allergen
                allergen_node = neo4j_graph.nodes.match("Allergen", name=allergen_name).first() or Node("Allergen",
                                                                                                        name=allergen_name)
                contains_relationship = Relationship(product_node, "CONTAINS", allergen_node)
                neo4j_graph.create(contains_relationship)
                break  # No need to check other fields if the allergen is found in one field

# Close connections
mongo_client.close()
