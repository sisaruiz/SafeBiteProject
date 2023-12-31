package resources.login;
@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB database = mongoClient.getDB("SafeBite");
        DBCollection users = database.getCollection("Users");

        DBObject userObject = users.findOne(new BasicDBObject("user_name", username));

        if (userObject == null) {
            response.sendRedirect("login.jsp?error=invalid_credentials");
            return;
        }

        if (!password.equals(userObject.getString("password"))) {
            response.sendRedirect("login.jsp?error=invalid_credentials");
            return;
        }

        session.setAttribute("username", username);
        response.sendRedirect("home.jsp");
    }
}