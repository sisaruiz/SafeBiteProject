package friend;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import dao.UserDAO;

/**
 * Servlet implementation class FriendServlet
 */
public class FriendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userToAdd = request.getParameter("userToAdd");
        String currentUser = (String) request.getSession().getAttribute("uname");

        // Update the database to add both users to each other's friends list
        UserDAO userDAO = new UserDAO();
        userDAO.addFriend(currentUser, userToAdd);
        userDAO.addFriend(userToAdd, currentUser);

        // Redirect back to the user's profile page
        response.sendRedirect("userDetails.jsp?user=" + userToAdd);
	}

}
