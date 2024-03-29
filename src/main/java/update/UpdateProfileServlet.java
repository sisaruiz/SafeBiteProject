package update;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.UserDAO;
import model.User;

public class UpdateProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	PrintWriter out = response.getWriter();
		response.setContentType("text/html");
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("uname");

        // Retrieve user details from the form
        String diet = request.getParameter("diet");
        String[] allergensArray = request.getParameterValues("allergens");
        List<String> allergens = (allergensArray != null) ? Arrays.asList(allergensArray) : null;

        // Update the user's profile in MongoDB
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByUsername(username);

        if (user != null) {
            user.setDiet(diet);
            user.setAllergens(allergens);

            // Update the user's profile in MongoDB
            if(userDAO.updateUserProfile(user)) {
            	// Redirect to the user's profile page after updating
                response.sendRedirect("yourProfile.jsp");
            }
            else {
            	out.println("User update failed.");
            	response.sendRedirect("yourProfile.jsp");
            }
        } else {
            response.getWriter().println("User not found");
        }
        
        userDAO.closeConnections();
    }
}
