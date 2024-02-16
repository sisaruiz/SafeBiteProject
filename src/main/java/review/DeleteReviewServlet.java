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
		
		String productId = request.getParameter("productId");
        String reviewId = request.getParameter("reviewId");

        if (reviewId != null && !reviewId.isEmpty()) {
            
            ReviewDAO reviewDAO = new ReviewDAO();

            reviewDAO.deleteReviewById(reviewId);

            response.sendRedirect("productEdit.jsp?productId=" + productId);
            reviewDAO.closeConnection();
        } else {
            response.getWriter().println("Invalid review ID");
        }
	}

}
