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

        String username = request.getParameter("username");
        PrintWriter out = response.getWriter();
		response.setContentType("text/html");

        UserDAO userDAO = new UserDAO();
        
        if (userDAO.deleteUser(username)) {
            out.println("User removed successfully.");    		
        }
        else {
        	out.println("User deletion failed.");
        }
        userDAO.closeConnections();
        RequestDispatcher rd = request.getRequestDispatcher("adminHome.jsp");
		rd.include(request, response);
        
	}

}
