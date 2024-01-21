package edit;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class EditProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ConnectionString uri = new ConnectionString("mongodb://localhost:27017");
        MongoClient myClient = MongoClients.create(uri);
        MongoDatabase database = myClient.getDatabase("SafeBite");
        MongoCollection<Document> usersCollection = database.getCollection("Users");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("uname") == null) {
            response.sendRedirect("login.jsp");
            return;
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

        usersCollection.updateOne(new Document("user_name", username), new Document("$set",
                new Document("diet_type", diet).append("allergy", new Document("allergens", allergies))));

        response.sendRedirect("successEdit.jsp");

        myClient.close();
    }
}
