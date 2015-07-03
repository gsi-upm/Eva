package com.google.glassware;

import com.google.glassware.mongodb.dao.MongoDBUserDAO;
import com.google.glassware.mongodb.model.User;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by JesúsManuel on 16/04/2015.
 */
@WebServlet("/adduser")
public class DeleteUserServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(AuthServlet.class.getSimpleName());
    private static final long serialVersionUID = -7060758261496829905L;
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        if ((id == null || id.equals(""))) {

            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/nodatos.jsp");
            rd.forward(request, response);
        } else {
            User p = new User();
            p.setId(id);


           // MongoClient mongo = (MongoClient) request.getSession().getServletContext().getAttribute("MONGO_CLIENT");
            MongoDBUserDAO userDAO = new MongoDBUserDAO();
            userDAO.deleteUser(p);
            System.out.println("User Deleted Successfully with id="+p.getId());
            LOG.info("User Deleted Successfully with id="+p.getId());

           // List<User> users = userDAO.readAllPerson();
            //request.setAttribute("users", users);
            userDAO.getClient().close();
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/index.jsp");
            rd.forward(request, response);
        }

    }

}
