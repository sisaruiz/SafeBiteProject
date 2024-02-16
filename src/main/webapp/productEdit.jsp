<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="dao.ProductDAO" %>
<%@ page import="model.Product" %>
<%@ page import="model.Review" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product Details</title>
<style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            margin: 0;
            padding: 20px;
        }

        h1 {
            text-align: center;
            color: #333;
        }

        section {
            background-color: #fff;
            border-radius: 10px;
            padding: 20px;
            margin-top: 20px;
        }

        form label {
            display: block;
            margin-bottom: 5px;
        }

        form input[type="text"] {
            width: 100%;
            padding: 8px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-sizing: border-box;
        }

        form input[type="submit"],
        form input[type="reset"] {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-right: 10px;
        }

        form input[type="submit"]:hover,
        form input[type="reset"]:hover {
            background-color: #45a049;
        }

        ul {
            list-style: none;
            padding: 0;
        }

        li {
            border-bottom: 1px solid #ccc;
            padding: 10px 0;
        }

        li:last-child {
            border-bottom: none;
        }
    </style>
</head>
<body>
    <h1>Product Details</h1>
    <% 
        String productId = request.getParameter("productId");
        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(productId);

        if (product != null) {
    %>
            <img src="<%= product.getImgURL() %>">
            <h2><%= product.getName() %></h2>
            
            <section>
                <h3>Product Details</h3>
                <!-- Edit Product Form -->
                <form action="UpdateProductServlet?productId=<%= productId %>" method="post">
				   
				    <label for="productName">Product Name:</label>
				    <input type="text" id="productName" name="productName" value="<%= product.getName() != null ? product.getName() : "" %>">
				    
				    <label for="productImg">Image URL:</label>
				    <input type="text" id="imgURL" name="imgURL" value="<%= product.getImgURL() != null ? product.getImgURL() : "" %>">
				    
				    <label for="quantity">Quantity:</label>
				    <input type="text" id="quantity" name="quantity" value="<%= product.getQuantity() != null ? product.getQuantity() : "" %>">
				    
				    <label for="ingredients">Ingredients:</label>
				    <input type="text" id="ingredients" name="ingredients" value="<%= product.getIngredients() != null ? product.getIngredients() : "" %>">
				    
				    <label for="allergens">Allergens:</label>
				    <input type="text" id="allergens" name="allergens" value="<%= product.getAllergens() != null ? product.getAllergens() : "" %>">
				    
				    <label for="traces">Traces:</label>
				    <input type="text" id="traces" name="traces" value="<%= product.getTraces() != null ? product.getTraces() : "" %>">
				    
				    <label for="labels">Labels:</label>
				    <input type="text" id="labels" name="labels" value="<%= product.getLabels() != null ? product.getLabels() : "" %>">
				    
				    <label for="categories">Categories:</label>
				    <input type="text" id="categories" name="categories" value="<%= product.getCategories() != null ? product.getCategories() : "" %>">
				    
				    <label for="brandOwner">Brand Owner:</label>
				    <input type="text" id="brandOwner" name="brandOwner" value="<%= product.getBrandOwner() != null ? product.getBrandOwner() : "" %>">
				    
				    <label for="brand">Brand:</label>
				    <input type="text" id="brand" name="brand" value="<%= product.getBrand() != null ? product.getBrand() : "" %>">
				    
				    <label for="countries">Countries:</label>
				    <input type="text" id="countries" name="countries" value="<%= product.getCountries() != null ? product.getCountries() : "" %>">
				    
				    <!-- Submit button to save changes -->
				    <input type="submit" value="Save Changes">
				</form>

                <br>
                <!-- Delete Product -->
                <form action="DeleteProductServlet?productId=<%= productId %>" method="post">
                    <input type="submit" value="Delete Product">
                </form>
            </section>

            <section>
                <h3>Reviews</h3>
                <ul>
                    <% List<Review> reviews = product.getReviews();
                    if (!reviews.isEmpty()) {
                        for (Review review : reviews) {
                    %>
                            <li>
                            
                            	<label for="r_heading">Heading:</label>
                                <span><%= review.getReviewHeading() %></span>
                                
                                <label for="r_score">Rating:</label>
                                <span><%= review.getReviewRating() %></span>
                                    
                                <label for="reviewDate">Date:</label>
                                <span><%= review.getReviewDate() %></span>
                                    
                                <label for="username">User:</label>
                                <span><%= review.getUsername() %></span>
                                    
                                <label for="reviewText">Text:</label>
                                <span><%= review.getReviewText() %></span>

                                <!-- Delete Review -->
                                <form action="DeleteReviewServlet?reviewId=<%= review.getReviewId() %>" method="post">
                                	<input type="hidden" name="productId" value="<%= productId %>">
                                    <input type="submit" value="Delete Review">
                                </form>
                            </li>
                    <%
                        }
                    } 
                    else {
                    %>
                            <p>No reviews for this product yet.</p>
                    <%
                        }
                    %>
                </ul>
            </section>
    <%
        } else {
    %>
            <p>Product not found.</p>
    <%
        }
    %>
</body>
</html>
