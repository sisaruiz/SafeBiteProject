package follow;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import dao.Neo4jManager;

/**
 * Servlet implementation class FollowServlet
 */
@WebServlet("/SafeBite/FollowServlet")
public class FollowServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userToFollow = request.getParameter("userToFollow");
        String currentUser = (String) request.getSession().getAttribute("uname");
        String type = request.getParameter("type");
        
        Neo4jManager neo4jManager = new Neo4jManager();
        if ("follow".equals(type)) {
            neo4jManager.createNeo4jFollowRelationship(currentUser, userToFollow);
        }
        else {
        	neo4jManager.deleteNeo4jFollowRelationship(currentUser, userToFollow);
        }
        
        // Redirect back to the user's profile page
        response.sendRedirect("userDetails.jsp?user=" + userToFollow);
	}

}
