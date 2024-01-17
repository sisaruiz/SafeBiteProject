package search;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String searchTerm = request.getParameter("searchTerm");
        System.out.println("Received searchTerm: " + searchTerm);

        // Call a database or other data source to search for matching records
        List<Product> products = new ArrayList<>();
        if (searchTerm != null) {
            products.addAll(getProducts(searchTerm));
        }

        // Send the search results back to the JSP page
        request.setAttribute("products", products);
        request.getRequestDispatcher("searchResults.jsp").forward(request, response);
    }

    private List<Product> getProducts(String searchTerm) {
        // Convert Document objects to Product objects
        List<Product> products = new ArrayList<>();

        // Establish a connection to the MongoDB database
        String uri = "mongodb://localhost:27017/SafeBite";

        try (MongoClient client = MongoClients.create(new ConnectionString(uri))) {
            MongoDatabase database = client.getDatabase("SafeBite");
            MongoCollection<Document> collection = database.getCollection("Products");

            // Query the 'Products' collection for matching documents
            Pattern pattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);
            Document query = new Document("product_name", pattern);
            System.out.println("Constructed MongoDB Query: " + query.toJson());

            long resultCount = collection.countDocuments(query);
            System.out.println("Number of matching documents: " + resultCount);

            for (Document productDoc : collection.find(query)) {
                // Extract fields from the Document
                String productId = productDoc.getObjectId("_id").toString();
                String productName = productDoc.getString("product_name");
                String imageUrl = productDoc.getString("image_url");

                // Create a Product instance
                Product product = new Product(productId, productName, imageUrl);
                products.add(product);
            }
        } catch (Exception e) {
            System.out.print("Connection to DB failed");
            e.printStackTrace();  // You might want to log the exception instead
            // Handle the exception, e.g., show an error page or log it
        }

        return products;
    }
}
