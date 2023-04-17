package servlet;

import auth.Auth;
import auth.Authenticator;
import exc.AccountDoesNotExist;
import exc.AccountIsLocked;
import exc.AuthenticationError;
import exc.EncryptionDontWork;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="ManageUsers", urlPatterns={"/ManageUsers"})
public class ManageUsersServlet extends HttpServlet {

    private Auth auth;

    @Override
    public void init() {
        auth =  new Authenticator();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            auth.checkAuthenticatedRequest(request, response);
            request.getRequestDispatcher("/WEB-INF/manageUsers.jsp").forward(request, response);
        }
        catch (AuthenticationError e) {
            request.getRequestDispatcher("/WEB-INF/authenticateUser.jsp").forward(request, response);
        }
        catch (AccountIsLocked e) {
            request.getRequestDispatcher("/WEB-INF/authenticateUser.jsp").forward(request, response);
        }
        catch (EncryptionDontWork e) {
            request.getRequestDispatcher("/WEB-INF/authenticateUser.jsp").forward(request, response);
        }
        catch (AccountDoesNotExist e) {
            request.getRequestDispatcher("/WEB-INF/authenticateUser.jsp").forward(request, response);
        }
    }
}