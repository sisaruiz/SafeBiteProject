package review;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Review;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

/**
 * Servlet implementation class ReviewServlet
 */
public class ReviewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		 try {
	            // Retrieve parameters from the form
			 	String productId = request.getParameter("productId");
			 	String productName = request.getParameter("productName");
	            int rating = Integer.parseInt(request.getParameter("r_score"));
	            String heading = request.getParameter("r_heading");
	            String text = request.getParameter("r_text");

	            // Retrieve timestamp
	            Date currentDate = new Date();

	            // Define the desired output format
	            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy");

	            // Format the current date
	            String formattedDate = outputFormat.format(currentDate);;

	            // Retrieve username from the session
	            HttpSession session = request.getSession();
	            String username = (String) session.getAttribute("uname");

	            // Create a Review object (assuming you have a Review class)
	            Review review = new Review(username, productName, new ObjectId(productId), text, formattedDate, heading, rating);

	            // Save the review to MongoDB
	            saveReviewToMongoDB(review);

	            // Redirect back to the product details page after inserting the review
	            response.sendRedirect("productDetails.jsp?productId=" + productId);
	        } catch (Exception e) {
	            // Handle exceptions appropriately (e.g., log the error, show an error page)
	            e.printStackTrace();
	            response.sendRedirect("error.jsp"); // Redirect to an error page
	        }
	}
	
	private void saveReviewToMongoDB(Review review) {
		
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            // Get or create a collection for reviews
            MongoCollection<Document> collection = mongoClient.getDatabase("SafeBite").getCollection("Reviews");

            // Create a document from the Review object
            Document reviewDocument = new Document("Review Rating", review.getReviewRating())
                    .append("Review Heading", review.getReviewHeading())
                    .append("Review Text", review.getReviewText())
                    .append("Review Date", review.getReviewDate())
                    .append("Product Name", review.getProductName())
                    .append("User", review.getUsername())
                    .append("Product ID", review.getProductID());                    ;

            // Insert the document into the MongoDB collection
            collection.insertOne(reviewDocument);
        }
    }
	
	

}
