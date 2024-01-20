<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.ProductDAO" %>
<%@ page import="model.Product" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Results</title>
    <style>
        /* Set a fixed width and height for the product images */
        img.product-image {
            width: 80px; /* Set your preferred width */
            height: 80px; /* Set your preferred height */
            object-fit: cover; /* Optional: Maintain aspect ratio and cover entire container */
        }

        /* Create a grid layout with 5 columns */
        .product-grid {
            display: grid;
            grid-template-columns: repeat(5, 1fr); /* Adjust the number of columns as needed */
            gap: 10px; /* Adjust the gap between products */
        }

        /* Style for individual product container */
        .product-container {
            text-align: center;
        }

        /* Style for active pagination link */
        .active-page {
            font-weight: bold;
        }
    </style>
</head>
<body>
    <h1>Search Results</h1>
    <% 
    	String searchTerm = (String) request.getAttribute("searchTerm");
        ProductDAO productDAO = new ProductDAO();
        List<Product> allProducts = productDAO.searchProducts(request.getParameter("searchTerm"));
        int numberOfResults = allProducts.size();
        int resultsPerPage = 100; // Set the number of results per page

        // Calculate the total number of pages
        int totalPages = (int) Math.ceil((double) numberOfResults / resultsPerPage);

        // Get the current page from the request, default to 1 if not present or invalid
        int currentPage;
        try {
            currentPage = Integer.parseInt(request.getParameter("page"));
            if (currentPage < 1 || currentPage > totalPages) {
                currentPage = 1; // Invalid page, default to 1
            }
        } catch (NumberFormatException e) {
            currentPage = 1; // Default to 1 if page parameter is not a valid integer
        }

        // Calculate the starting and ending indexes for the current page
        int startIndex = (currentPage - 1) * resultsPerPage;
        int endIndex = Math.min(startIndex + resultsPerPage, numberOfResults);
    %>

    <p>Number of results: <%= numberOfResults %></p>

    <div class="product-grid">
        <% 
            if (numberOfResults > 0) {
                for (int i = startIndex; i < endIndex; i++) {
                    Product product = allProducts.get(i);
        %>
            <div class="product-container">
                <img class="product-image" src="<%= product.getImgURL() %>">
                <p>
                    <a href="productDetails.jsp?productId=<%= product.getId() %>"><%= product.getName() %></a>
                </p>
            </div>
        <%
                }
            } else {
        %>
            <p>No products found matching your search term.</p>
        <%
            }
        %>
    </div>

    <!-- Pagination links -->
    <div>
        <p>Page <%= currentPage %> of <%= totalPages %></p>
        <%
            for (int i = 1; i <= totalPages; i++) {
        %>
            <a href="searchResults.jsp?page=<%= i %>&searchTerm=<%= request.getParameter("searchTerm") %>" class="<%= (i == currentPage) ? "active-page" : "" %>"><%= i %></a>
        <%
            }
        %>
    </div>
</body>
</html>