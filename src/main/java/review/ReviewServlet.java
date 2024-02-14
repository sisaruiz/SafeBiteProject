package review;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Review;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.types.ObjectId;

import dao.ReviewDAO;

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

	            ReviewDAO reviewDAO = new ReviewDAO();
	            // Save the review to MongoDB
	            reviewDAO.saveReviewToMongoDB(review);
	            
	            
	            // Redirect back to the product details page after inserting the review
	            response.sendRedirect("productDetails.jsp?productId=" + productId);
	        } catch (Exception e) {
	            // Handle exceptions appropriately (e.g., log the error, show an error page)        	
	            e.printStackTrace();
	        	PrintWriter out = response.getWriter();
	    		response.setContentType("text/html");
	    		
	    		
	        	out.println("Review insertion failed.");

	            RequestDispatcher rd = request.getRequestDispatcher("productDetails.jsp?productId=" + request.getParameter("productId"));
	            rd.include(request, response);
	        }
	}
	
	
	
	

}
