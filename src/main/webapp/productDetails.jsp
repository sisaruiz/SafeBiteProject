<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="dao.ProductDAO" %>
<%@ page import="model.Product" %>
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
            <!-- Quantity -->
			<p>Quantity: <%= product.getQuantity() != null ? product.getQuantity() : "not available" %></p>
			
			<!-- Nutrition information -->
			<p>Ingredients: <%= product.getIngredients() != null ? product.getIngredients() : "not available" %></p>
			<p>Allergens: <%= product.getAllergens() != null ? product.getAllergens() : "not available" %></p>
			<p>Traces: <%= product.getTraces() != null ? product.getTraces() : "not available" %></p>
			<p>Labels: <%= product.getLabels() != null ? product.getLabels() : "not available" %></p>
			<p>Categories: <%= product.getCategories() != null ? product.getCategories() : "not available" %></p>
			
			<!-- Production scope -->
			<p>Brand Owner: <%= product.getBrandOwner() != null ? product.getBrandOwner() : "not available" %></p>
			<p>Brand: <%= product.getBrand() != null ? product.getBrand() : "not available" %></p>
			<p>Countries: <%= product.getCountries() != null ? product.getCountries() : "not available" %></p>
			
			<!-- Entry log -->
			<p>Entry Timestamp: <%= product.getEntryTS() != null ? product.getEntryTS() : "not available" %></p>
			
			<!-- Last update -->
			<p>Last Update By: <%= product.getLastUpdateBy() != null ? product.getLastUpdateBy() : "not available" %></p>
			<p>Last Update Timestamp: <%= product.getLastUpdateTS() != null ? product.getLastUpdateTS() : "not available" %></p>
			
			<section>
			<h3>Reviews</h3>
			<!-- Review insertion -->
			<h4>Insert review:</h4>
			<form action="ReviewServlet?productId=<%= productId %>&productName=<%= product.getName() %>" method="post">
<pre>
			Rating:		<select id="r_score" name="r_score">
						  <option value="1">1</option>
						  <option value="2">2</option>
						  <option value="3">3</option>
						  <option value="4">4</option>
						  <option value="5">5</option>
						</select>
			Heading:	<input type="text" name="r_heading">
			Text:		<input type="text" name="r_text">
				<input type=submit value=Insert>		<input type=reset>
</pre>
</form>
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
