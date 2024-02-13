package signup;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.UserDAO;

/**
 * Servlet implementation class DietServlet
 */
public class DietServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		UserDAO userdao = new UserDAO();
	    
	    //Get parameter values
	    HttpSession session = request.getSession(false);
	    if (session == null || session.getAttribute("uname") == null) {
	        // Redirect to login page
	        response.sendRedirect("login.jsp");
	        return; // Stop further execution
	    }
	    String username = (String) session.getAttribute("uname");
	    String diet = request.getParameter("diet");
	    String[] array = request.getParameterValues("allergens");
	    List<String> allergies = null;
	    if (array != null) {
	        allergies = Arrays.asList(array);
	    } else {
	        allergies = new ArrayList<>();
	    }

	    // Update related doc
	    User updatedUser = new User(username, null, null, null, null, null, null, diet, allergies);
	    if (userdao.updateUserProfile(updatedUser)) {
	    	// Redirect to success page
		    response.sendRedirect("success.jsp");
	    }
	    else {
	    	
	    	// Remove user node from both databases
	    	userdao.deleteUser(username);
            
            out.println("Sorry! There's a problem signing you up right now. Try later.");
			RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
			rd.include(request, response);
	    }
	    
	    userdao.closeConnections();
	}

}

