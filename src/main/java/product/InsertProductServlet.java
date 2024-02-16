package product;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Product;

import java.io.IOException;
import java.io.PrintWriter;
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
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

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

	    HttpSession session = request.getSession(false);
	    if (session == null || session.getAttribute("uname") == null) {
	        response.sendRedirect("login.jsp");
	        return;
	    }
	    String username = (String) session.getAttribute("uname");

        Product newProduct = new Product(
            null, 
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
            new Date(),  
            username,  
            new Date() 
        );

        ProductDAO productDAO = new ProductDAO();
        if (productDAO.addProduct(newProduct)) {
        	out.println("Product added correctly.");
        }
        else {
        	out.println("Product insertion failed.");
        }
        
        productDAO.closeConnections();
        
        RequestDispatcher rd = request.getRequestDispatcher("adminHome.jsp");
        rd.include(request, response);
	}

}
