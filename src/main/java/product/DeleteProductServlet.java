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
		// Redirect to the main page
        PrintWriter out = response.getWriter();
		response.setContentType("text/html");

        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(productId);

        if (productDAO.deleteProduct(product)) {
            out.println("Product deleted successfully.");
        }
        else {
        	out.println("Product deletion failed.");
        }
        
        productDAO.closeConnections();
        
        RequestDispatcher rd = request.getRequestDispatcher("adminHome.jsp");
		rd.include(request, response);
	}

}
