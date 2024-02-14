package review;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import dao.ReviewDAO;

/**
 * Servlet implementation class DeleteReviewServlet
 */
public class DeleteReviewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get the review ID from the request parameter
		String productId = request.getParameter("productId");
        String reviewId = request.getParameter("reviewId");

        // Check if the review ID is not null
        if (reviewId != null && !reviewId.isEmpty()) {
            // Create an instance of ReviewDAO
            ReviewDAO reviewDAO = new ReviewDAO();

            // Delete the review using the ReviewDAO
            reviewDAO.deleteReviewById(reviewId);

            // Redirect to the product edit page or any other appropriate page
            response.sendRedirect("productEdit.jsp?productId=" + productId);
            reviewDAO.closeConnection();
        } else {
            // Handle the case where reviewId is null or empty
            response.getWriter().println("Invalid review ID");
        }
	}

}
