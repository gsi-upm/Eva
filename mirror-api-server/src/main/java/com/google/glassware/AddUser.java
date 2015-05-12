package com.google.glassware;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.glassware.mongodb.dao.MongoDBUserDAO;
import com.google.glassware.mongodb.model.User;
import com.mongodb.MongoClient;



/**
 * Created by JesúsManuel on 14/04/2015.
 */
@WebServlet("/adduser")
public class AddUser extends HttpServlet{
    private static final Logger LOG = Logger.getLogger(AuthServlet.class.getSimpleName());
    private static final long serialVersionUID = -7060758261496829905L;
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        if ((name == null || name.equals(""))
                || (email == null || email.equals(""))) {
            request.setAttribute("error", "Mandatory Parameters Missing");
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/nodatos.jsp");
            rd.forward(request, response);
        } else {
            User p = new User();
            p.setEmail(email);
            p.setName(name);
            String userId = AuthUtil.getUserId(request);
            p.setUserId(userId);
           // MongoClient mongo = (MongoClient) request.getSession().getServletContext().getAttribute("MONGO_CLIENT");
            MongoDBUserDAO userDAO = new MongoDBUserDAO();
            userDAO.createUser(p);
            System.out.println("User Added Successfully with id="+p.getId());
            LOG.info("User Added Successfully with id="+p.getId());
            request.setAttribute("success", "Person Added Successfully");
          //  List<User> users = userDAO.readAllPerson();
           // request.setAttribute("users", users);
            userDAO.getClient().close();
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    "/index.jsp");
            rd.forward(request, response);
        }

    }

}