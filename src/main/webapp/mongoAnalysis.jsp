<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mongodb.client.AggregateIterable" %>
<%@ page import="org.bson.Document" %>
<%@ page import="mongo.MongoAggregations" %>
<%@ page import="org.bson.types.ObjectId" %> 

<html>

<%!  // Declaration of the method outside the scriptlet block
    String formatCategory(Object category) {
        if (category == null) {
            return "unspecified";
        } else if (category instanceof String) {
            // Remove the "en:" prefix
            return ((String) category).replace("en:", "");
        } else {
            return category.toString();
        }
    }

double getPercentage(Document result, String gender) {
    List<Document> percentages = result.getList("percentages", Document.class);
    for (Document percentage : percentages) {
        if (percentage.getString("gender").equals(gender)) {
            return percentage.getDouble("percentage");
        }
    }
    return 0.0;
}

String formatPercentage(double percentage) {
    return String.format("%.2f", percentage);
}

%>

<head>
    <title>MongoDB Aggregation Results</title>
</head>
<body>

<%
    String connectionString = "mongodb://localhost:27017";
    String databaseName = "SafeBite";
    String productsCollectionName = "Products";
    String reviewsCollectionName = "Reviews";
    String usersCollectionName = "Users";

    MongoAggregations productsAggregations = new MongoAggregations(connectionString, databaseName, productsCollectionName);
    MongoAggregations reviewsAggregations = new MongoAggregations(connectionString, databaseName, reviewsCollectionName);
    MongoAggregations usersAggregations = new MongoAggregations(connectionString, databaseName, usersCollectionName);
    

    AggregateIterable<Document> productCategoryCounts = productsAggregations.getProductCategoryCounts();
    List<Document> productCountByBrandAndCountry = productsAggregations.getProductCountByBrandAndCountry();
    List<Document> mostReviewedProducts = reviewsAggregations.getMostReviewedProducts(reviewsAggregations.getCollection(), 10);
    List<Document> topReviewersAndAverageRating = reviewsAggregations.getTopReviewersAndAverageRating(10);

    AggregateIterable<Document> genderPercentageByDietType = usersAggregations.calculateGenderPercentageByDietType();

%>

<h1>Most Reviewed Product</h1>
<table border="1">
    <tr>
        <th>Product ID</th>
        <th>Product Name</th>
        <th>Review Count</th>
    </tr>
    <% for (Document product : mostReviewedProducts) { %>
        <tr>
            <td>
                <% 
                    Object productIdObj = product.get("ProductID");
                    if (productIdObj instanceof ObjectId) {
                        out.print(((ObjectId) productIdObj).toString());
                    }
                %>
            </td>
            <td><%= product.getString("ProductName") %></td>
            <td><%= product.getInteger("reviewCount") %></td>
        </tr>
    <% } %>
</table>

<h1>Product Count by Brand and Country</h1>
<table border="1">
    <tr>
        <th>Brand Owner</th>
        <th>Country</th>
        <th>Product Count</th>
    </tr>
    <% for (Document doc : productCountByBrandAndCountry) { %>
        <tr>
            <td><%= doc.getString("brandOwner") %></td>
            <td><%= doc.getString("country") %></td>
            <td><%= doc.getInteger("productCount") %></td>
        </tr>
    <% } %>
</table>

<br>


<h1>Gender Percentage by Diet Type</h1>
<table border="1">
    <tr>
        <th>Diet Type</th>
        <th>Male %</th>
        <th>Female %</th>
        <th>Other %</th>
    </tr>
    <% for (Document result : genderPercentageByDietType) { %>
        <tr>
            <td><%= result.getString("diet_type") %></td>
            <td><%= formatPercentage(getPercentage(result, "Male")) %></td>
            <td><%= formatPercentage(getPercentage(result, "Female")) %></td>
            <td><%= formatPercentage(getPercentage(result, "Other")) %></td>
        </tr>
    <% } %>
</table>

<br>

<h1>Top 10 users with most reviewed products and average rating</h1>
<table border="1">
        <thead>
            <tr>
                <th>User</th>
                <th>Total Products Reviewed</th>
                <th>Average Rating</th>
            </tr>
        </thead>
        <tbody>
            <% 
                for (Document reviewer : topReviewersAndAverageRating) {
                	double averageRating = reviewer.getDouble("averageRating");
            %>
            <tr>
                <td><%= reviewer.getString("User") %></td>
                <td><%= reviewer.getInteger("totalProductsReviewed") %></td>
                <td><%= String.format("%.3f", averageRating) %></td>
            </tr>
            <% } %>
        </tbody>
    </table>

<br>

<h1>Product Category Counts</h1>
<table border="1">
    <tr>
        <th>Category</th>
        <th>Count</th>
    </tr>
    <% for (Document document : productCategoryCounts) { %>
        <tr>
            <td><%= formatCategory(document.get("_id")) %></td>
            <td><%= document.get("count") %></td>
        </tr>
    <% } %>
</table>

<br>


<%
    // Close the MongoDB connections after using the results
    productsAggregations.closeConnection();
    reviewsAggregations.closeConnection();
    usersAggregations.closeConnection();
    
%>
</body>
</html>
