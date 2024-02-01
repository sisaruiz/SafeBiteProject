package product;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Product;

import java.io.IOException;
import java.util.Date;

import dao.ProductDAO;

/**
 * Servlet implementation class InsertProductServlet
 */
public class InsertProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Retrieve data from the form
        String productName = request.getParameter("pname");
        String imageUrl = request.getParameter("imgURL");
        String ingredients = request.getParameter("ingredients");
        String allergens = request.getParameter("allergens");
        String brands = request.getParameter("brands");
        String brandOwner = request.getParameter("bowner");
        String categories = request.getParameter("categories");
        String countries = request.getParameter("countries");
        String labels = request.getParameter("labels");
        String quantity = request.getParameter("quantity");
        String traces = request.getParameter("traces");
        
        // Get parameter values
	    HttpSession session = request.getSession(false);
	    if (session == null || session.getAttribute("uname") == null) {
	        // Redirect to login page
	        response.sendRedirect("login.jsp");
	        return; // Stop further execution
	    }
	    String username = (String) session.getAttribute("uname");

        // Create a new Product instance
        Product newProduct = new Product(
            null,  // The ID will be generated by MongoDB
            productName,
            imageUrl,
            quantity,
            categories,
            ingredients,
            allergens,
            traces,
            labels,
            brandOwner,
            brands,
            countries,
            new Date(),  // Entry timestamp is set to the current date
            username,  
            new Date()  // Last update timestamp is set to the current date
        );

        // Add the new product to the MongoDB dataset
        ProductDAO productDAO = new ProductDAO();
        productDAO.addProduct(newProduct);

        // Redirect to a success page or display a success message
        response.sendRedirect("success.jsp");
	}

}
