package user;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import dao.UserDAO;

/**
 * Servlet implementation class DeleteUserServlet
 */
public class DeleteUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get the username from the request
        String username = request.getParameter("username");
        PrintWriter out = response.getWriter();
		response.setContentType("text/html");

        // Perform the delete operation (you should implement this in your UserDAO)
        UserDAO userDAO = new UserDAO();
        
        if (userDAO.deleteUser(username)) {
        	// Redirect back to the search results or any other appropriate page
            out.println("User removed successfully.");    		
        }
        else {
        	out.println("User deletion failed.");
        }
        RequestDispatcher rd = request.getRequestDispatcher("adminHome.jsp");
		rd.include(request, response);
        
	}

}
