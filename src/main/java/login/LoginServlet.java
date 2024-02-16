package login;

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
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String n = request.getParameter("t1");
		String pwd = request.getParameter("t2");
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		//Create connection string
		UserDAO userDAO = new UserDAO();
		
        // Perform authentication logic
        Document user = userDAO.find(n, pwd);

        if (user != null) {
            // Authentication successful
        	HttpSession hs=request.getSession();
			hs.setAttribute("uname", n);
			
			// Check if user or admin
			Boolean admin = user.getBoolean("admin");
			hs.setAttribute("admin", admin);
			if (admin) {
				response.sendRedirect("adminHome.jsp");
			}
			else {
				response.sendRedirect("home.jsp");
			}
			
        } else {
            // Authentication failed
        	out.println("Sorry! username or password incorrect ... Try again!");
			RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
			rd.include(request, response);
        }
        
        userDAO.closeConnections();
	}

}
