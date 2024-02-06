package product;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Product;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import dao.ProductDAO;
import dao.Neo4jManager;

/**
 * Servlet implementation class UpdateProductServlet
 */
public class UpdateProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	 	String productId = request.getParameter("productId");
        String productName = request.getParameter("productName");
        String imgURL = request.getParameter("imgURL");
        String quantity = request.getParameter("quantity");
        String ingredients = request.getParameter("ingredients");
        String allergens = request.getParameter("allergens");
        String traces = request.getParameter("traces");
        String labels = request.getParameter("labels");
        String categories = request.getParameter("categories");
        String brandOwner = request.getParameter("brandOwner");
        String brand = request.getParameter("brand");
        String countries = request.getParameter("countries");
        
        HttpSession hs = request.getSession();
		String admin = (String)hs.getAttribute("uname");
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
        
        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(productId);

        if (product != null) {
            // Update the product attributes
            product.setName(productName);
            product.setQuantity(quantity);
            product.setIngredients(ingredients);
            product.setAllergens(allergens);
            product.setTraces(traces);
            product.setLabels(labels);
            product.setCategories(categories);
            product.setBrandOwner(brandOwner);
            product.setBrand(brand);
            product.setCountries(countries); 
            product.setImgURL(imgURL);
            product.setLUB(admin);
            product.setLUTS(new Date());

            // Save the updated product
            productDAO.updateProduct(product);
            
         // Update the product in Neo4j
            Neo4jManager neo4jManager = new Neo4jManager();
            neo4jManager.updateNeo4jProductNode(productId, productName);
            

            // Redirect to the product details page
            out.println("Product updated successfully.");
			RequestDispatcher rd = request.getRequestDispatcher("productEdit.jsp?productId=" + productId);
			rd.include(request, response);
        } else {
            // Handle the case where the product is not found
            response.sendRedirect("errorPage.jsp");
        }
	}

}
