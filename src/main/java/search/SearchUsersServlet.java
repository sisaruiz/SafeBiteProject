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

        List<User> allUsers = userDAO.searchUsers(searchTerm);

        request.setAttribute("numberOfResults", allUsers.size());

        int currentPage = (request.getParameter("page") != null) ? Integer.parseInt(request.getParameter("page")) : 1;

        int resultsPerPage = 10; 
        int startIndex = (currentPage - 1) * resultsPerPage;
        int endIndex = Math.min(startIndex + resultsPerPage, allUsers.size());

        List<User> usersForPage = allUsers.subList(startIndex, endIndex);

        request.setAttribute("users", usersForPage);
        request.setAttribute("currentPage", currentPage);

        request.setAttribute("searchTerm", searchTerm); 
        request.getRequestDispatcher("searchUsersResults.jsp").forward(request, response);
    }
}