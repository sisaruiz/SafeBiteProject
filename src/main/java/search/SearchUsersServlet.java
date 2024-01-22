package search;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;
import java.util.List;

import dao.UserDAO;

/**
 * Servlet implementation class SearchUsersServlet
 */
public class SearchUsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String searchTerm = request.getParameter("searchTerm");
        System.out.println("Received searchTerm: " + searchTerm);

        // Call the DAO to search for matching records
        List<User> allUsers = userDAO.searchUsers(searchTerm);

        // Set the total number of results in the request
        request.setAttribute("numberOfResults", allUsers.size());

        // Get the requested page from the parameter, default to 1 if not present
        int currentPage = (request.getParameter("page") != null) ? Integer.parseInt(request.getParameter("page")) : 1;

        // Calculate the starting and ending indexes for the current page
        int resultsPerPage = 10; // Adjust this based on your preference
        int startIndex = (currentPage - 1) * resultsPerPage;
        int endIndex = Math.min(startIndex + resultsPerPage, allUsers.size());

        // Extract the subset of products for the current page
        List<User> usersForPage = allUsers.subList(startIndex, endIndex);

        // Add the list of products for the current page to the request
        request.setAttribute("users", usersForPage);
        request.setAttribute("currentPage", currentPage);

        // Forward the request to the JSP page for rendering
        request.setAttribute("searchTerm", searchTerm); 
        request.getRequestDispatcher("searchUsersResults.jsp").forward(request, response);
    }
}