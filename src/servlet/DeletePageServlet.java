package servlet;

import acc.Acc;
import accCtrl.AccessController;
import accCtrl.AccessControllerClass;
import accCtrl.Capability;
import accCtrl.operations.OperationClass;
import accCtrl.operations.OperationValues;
import accCtrl.resources.ResourceClass;
import auth.Auth;
import auth.Authenticator;
import exc.AccessControlError;
import exc.AuthenticationError;
import socialNetwork.PageObject;
import socialNetwork.SN;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "DeletePage", urlPatterns = {"/DeletePage"})
public class DeletePageServlet extends HttpServlet {

    private Auth auth;
    private AccessController accessController;
    private Logger logger;

    @Override
    public void init() {
        auth = Authenticator.getInstance();
        accessController = AccessControllerClass.getInstance();

        logger = Logger.getLogger(CreateAccServlet.class.getName());
        logger.setLevel(Level.FINE);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            auth.checkAuthenticatedRequest(request, response);
            request.getRequestDispatcher("/WEB-INF/createPage.jsp").forward(request, response);
            logger.log(Level.INFO, "INFO");
        } catch (AuthenticationError e) {
            logger.log(Level.WARNING, "Invalid username or password");
            request.setAttribute("errorMessage", "Invalid username and/or password");
            request.getRequestDispatcher("/WEB-INF/createAcc.jsp").forward(request, response);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Acc userAcc = auth.checkAuthenticatedRequest(request, response);

            List<Capability> capabilities = (List<Capability>) request.getSession().getAttribute("Capability");

            SN socialNetwork = SN.getInstance();
            accessController.checkPermission(capabilities,  new ResourceClass("page"), new OperationClass(OperationValues.DELETE_PAGE.getOperation()));

            int pageId = Integer.parseInt(request.getParameter("pageid"));

            PageObject page = socialNetwork.getPage(pageId);
            socialNetwork.deletePage(page);

            logger.log(Level.INFO, "Deleted successfully the page of user: "+page.getUserId()+", by the user: "+userAcc.getAccountName()+".");

            // Redirect to home page after successful page deletion
            response.sendRedirect(request.getContextPath() + "/ManageUsers");
        } catch (AuthenticationError e) {
            logger.log(Level.WARNING, "Invalid username or password");
            request.setAttribute("errorMessage", "Invalid username and/or password");
            request.getRequestDispatcher("/WEB-INF/createAcc.jsp").forward(request, response);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQL Exception on creating page");
            request.setAttribute("errorMessage", "Problems regarding the creation of the page. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/createPage.jsp").forward(request, response);
        } catch (AccessControlError e) {
            logger.log(Level.WARNING, "Invalid permissions for this operation");
            request.setAttribute("errorMessage", "Invalid permissions for this operation");
            request.getRequestDispatcher("/WEB-INF/createPage.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Problems regarding the social network. Please try again later.");
            request.setAttribute("errorMessage", "Problems regarding the social network. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/createPage.jsp").forward(request, response);
        }
    }
}
