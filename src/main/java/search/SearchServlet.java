package search;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import dao.ProductDAO;

import java.io.IOException;
import java.util.List;

public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String searchTerm = request.getParameter("searchTerm");
        System.out.println("Received searchTerm: " + searchTerm);

        List<Product> allProducts = productDAO.searchProducts(searchTerm);
        request.setAttribute("allProducts", allProducts);

        List<Product> productsForPage = allProducts;

        request.setAttribute("products", productsForPage);
        request.setAttribute("currentPage", 1);

        request.setAttribute("searchTerm", searchTerm); 
        request.getRequestDispatcher("searchResults.jsp").forward(request, response);
    }
}
