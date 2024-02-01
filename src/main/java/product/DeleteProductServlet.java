package product;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;

import java.io.IOException;
import java.io.PrintWriter;

import dao.ProductDAO;

/**
 * Servlet implementation class DeleteProductServlet
 */
public class DeleteProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String productId = request.getParameter("productId");

        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(productId);

        if (product != null) {
            // Delete the product
            productDAO.deleteProduct(productId);

            // Redirect to the main page
            PrintWriter out = response.getWriter();
    		response.setContentType("text/html");
            out.println("Product deleted successfully.");
			RequestDispatcher rd = request.getRequestDispatcher("adminHome.jsp");
			rd.include(request, response);
            
        } else {
            // Handle the case where the product is not found
            response.sendRedirect("errorPage.jsp");
        }
	}

}
