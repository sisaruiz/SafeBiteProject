<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="dao.ProductDAO" %>
<%@ page import="model.Product" %>
<%@ page import="model.Review" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product Details</title>
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
				    <!-- Editable input fields for updating product details -->
				    <label for="productName">Product Name:</label>
				    <input type="text" id="productName" name="productName" value="<%= product.getName() != null ? product.getName() : "" %>">
				    
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
				    
				    <label for="entryTimestamp">Entry Timestamp:</label>
				    <input type="text" id="entryTimestamp" name="entryTimestamp" value="<%= product.getStringEntryTS() != null ? product.getStringEntryTS() : "" %>">
				    
				    <label for="lastUpdateBy">Last Update By:</label>
				    <input type="text" id="lastUpdateBy" name="lastUpdateBy" value="<%= product.getLastUpdateBy() != null ? product.getLastUpdateBy() : "" %>">
				    
				    <label for="lastUpdateTimestamp">Last Update Timestamp:</label>
				    <input type="text" id="lastUpdateTimestamp" name="lastUpdateTimestamp" value="<%= product.getStringLastUpdateTS() != null ? product.getStringLastUpdateTS() : "" %>">
				    
				    <!-- Submit button to save changes -->
				    <input type="submit" value="Save Changes">
				</form>

                
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
                                <!-- Edit Review Form -->
								<form action="UpdateReviewServlet?reviewId=<%= review.getReviewId() %>" method="post">
								    <!-- Editable input fields for updating review details -->
								    <label for="r_score">Rating:</label>
								    <select id="r_score" name="r_score">
								        <option value="1" <%= review.getReviewRating() == 1 ? "selected" : "" %>>1</option>
								        <option value="2" <%= review.getReviewRating() == 2 ? "selected" : "" %>>2</option>
									    <option value="3" <%= review.getReviewRating() == 3 ? "selected" : "" %>>3</option>
									    <option value="4" <%= review.getReviewRating() == 4 ? "selected" : "" %>>4</option>
									    <option value="5" <%= review.getReviewRating() == 5 ? "selected" : "" %>>5</option>
								    </select>
								
								    <label for="r_heading">Heading:</label>
								    <input type="text" id="r_heading" name="r_heading" value="<%= review.getReviewHeading() != null ? review.getReviewHeading() : "" %>">
								
								    <label for="reviewDate">Date:</label>
								    <input type="text" id="reviewDate" name="reviewDate" value="<%= review.getReviewDate() != null ? review.getReviewDate() : "" %>">
								
								    <label for="username">User:</label>
								    <input type="text" id="username" name="username" value="<%= review.getUsername() != null ? review.getUsername() : "" %>">
								
								    <label for="reviewText">Text:</label>
								    <textarea id="reviewText" name="reviewText"><%= review.getReviewText() != null ? review.getReviewText() : "" %></textarea>
								
								    <!-- Submit button to save changes -->
								    <input type="submit" value="Save Changes">
								</form>

                                <!-- Delete Review -->
                                <form action="DeleteReviewServlet?reviewId=<%review.getReviewId(); %>" method="post">
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
