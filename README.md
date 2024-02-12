# TO DO:

a. Admin
- ~admin home interface~
- ~products catalogue: add/remove/update product, remove review~.
- ~users catalogue: delete user~

b. ~upload data onto Neo4j~

c. User
- ~revamp "add friend" functionality~
- ~like product~
- ~follows diet~
- ~is allergic to~
- ~is_compatible_with~ 
- ~update user results so that if i look up myself i will open yourProfile page and NOT userDetails~
- ~display like products in yourProfile page~

d. implement queries for:
- ~product recommendation~
- ~user recommendation~
  
e. implement queries on mongoDB:
- ~Count the number of products created by each brand owner in a specific country (products collection)~
- ~Count the number of products in each category and sort them (products collection)~
- ~List the top users who have reviewed the most products and their average rating (Reviews collection)~
- ~Get the most reviewed product along with the count of reviews (Reviews collection)~
- ~Analyze the distribution of ratings given by users over time.(Reviews Collection)~
- ~Calculate the Percentage of Male and Female Users for each diet_type (users collection)~

f. implement queries on neo4j:
- ~Which are the most popular users (highest number of users following him)?~
- ~What are the most followed diets among users, and how many users follow each diet?~
- ~Which allergens are most prevalent among users, and how many users are allergic to each allergen?~
- modify the code so that someone who follows diet=none gets recommendations. Don't create relationships "IS_COMPATIBLE_WITH"

g. ~display results of those queries~

h. make it beautiful

i. upload dataset onto virtual machines and make it work
