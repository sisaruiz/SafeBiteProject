package signup;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import org.bson.Document;
import dao.UserDAO;

/**
 * Servlet implementation class SignupServlet
 */
public class SignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		
		//Get parameters
		String username = request.getParameter("name");
		String email = request.getParameter("email");
		String dateOfBirth = request.getParameter("birthday");
		String country = request.getParameter("country");
		String psw = request.getParameter("psw");
		String gender = request.getParameter("gender");
		
		//Check if username already exists
        UserDAO userdao = new UserDAO();
        if (userdao.checkUsernameExists(username) && !userdao.verifyDummyUser(username)) {
            // It exists
            out.println("Sorry! Username already exists. Please, choose a different one.");
            RequestDispatcher rd = request.getRequestDispatcher("signup.jsp");
            rd.include(request, response);
            userdao.closeConnections();
            return; 
        }
        
        if (userdao.verifyDummyUser(username)) {
        	if (!userdao.deleteUser(username)) {
        		out.println("Sorry! There's a problem signing you up right now. Try later.");
    			RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
    			rd.include(request, response);
    			userdao.closeConnections();
    			return;
        	}
        }
        		
        Document newUser = new Document("user_name", username)
                		.append("email", email)
                		.append("password", psw)
                		.append("country", country)
                		.append("date_of_birth", dateOfBirth)
                		.append("gender", gender)
                		.append("admin", false);
                        		
        if (userdao.insertUser(newUser)) {
        	//Save name to associate subsequent diet setup
            HttpSession hs = request.getSession();
    		hs.setAttribute("uname", username);
                
            //Redirect to profile setting page
            response.sendRedirect("profile_setup.jsp");
        }	
        else {
        	out.println("Sorry! There's a problem signing you up right now. Try later.");
			RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
			rd.include(request, response);
        }
        
        userdao.closeConnections();
  
	}

}
