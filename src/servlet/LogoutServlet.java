package servlet;

import acc.Acc;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name="Logout", urlPatterns={"/Logout"})
public class LogoutServlet extends HttpServlet {

    private Auth auth;
    private Acc authUser;
    private Logger logger;

    @Override
    public void init() {
        auth =  new Authenticator();
        logger.setLevel(Level.FINE);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            authUser = auth.checkAuthenticatedRequest(request, response);
            logger.log(Level.INFO, "User '" + authUser.getAccountName() + "' is trying to logout");
            request.getRequestDispatcher("/WEB-INF/logout.jsp").forward(request, response);
        }
        catch (AuthenticationError e) {
            logger.log(Level.WARNING, "Invalid username or password");
            request.setAttribute("errorMessage", "Invalid username or password");
            request.getRequestDispatcher("/WEB-INF/authenticateUser.jsp").forward(request, response);
        }
        catch (AccountIsLocked e) {
            logger.log(Level.WARNING, "Account is locked");
            request.setAttribute("errorMessage", "Your account is locked");
            request.getRequestDispatcher("/WEB-INF/manageUsers.jsp").forward(request, response);
        }
        catch (EncryptionDontWork e) {
            logger.log(Level.SEVERE, "Problems with encryption");
            request.setAttribute("errorMessage", "Problems with encryption");
            request.getRequestDispatcher("/WEB-INF/manageUsers.jsp").forward(request, response);
        }
        catch (AccountDoesNotExist e) {
            logger.log(Level.WARNING, "The account does not exist");
            request.setAttribute("errorMessage", "The account does not exist");
            request.getRequestDispatcher("/WEB-INF/authenticateUser.jsp").forward(request, response);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        auth.logout(authUser);
        logger.log(Level.INFO, "User '" + authUser.getAccountName() + "' has logged out");
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Redirect to login page after successful logout
        response.sendRedirect(request.getContextPath() + "/AuthenticateUser");
    }

}
