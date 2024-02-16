package product;



import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import dao.Neo4jManager;


@WebServlet("/SafeBite/LikeServlet")
public class LikeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String productId = request.getParameter("productId");
        String userName = (String) request.getSession().getAttribute("uname"); // Use the correct session attribute
        String type = request.getParameter("type");

        Neo4jManager neo4jManager = new Neo4jManager();

        if ("like".equals(type)) {
            neo4jManager.createNeo4jUserProductLikeRelationship(userName, productId);
        } else {
            neo4jManager.deleteNeo4jUserProductLikeRelationship(userName, productId);
        }

        response.sendRedirect("productDetails.jsp?productId=" + productId);
    }
}

