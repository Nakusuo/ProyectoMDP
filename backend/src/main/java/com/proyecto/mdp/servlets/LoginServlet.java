package com.proyecto.mdp.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.proyecto.mdp.dao.UsuarioDAO;
import com.proyecto.mdp.dao.UsuarioDAOImpl;
import com.proyecto.mdp.model.Usuario;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    private UsuarioDAO usuarioDAO;
    private Gson gson;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAOImpl();
        gson = new Gson();
    }
    
    // Método para configurar CORS y no repetir código
    private void setupCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setupCORS(response); // Habilitar CORS para la petición
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Usuario usuario = usuarioDAO.login(username, password);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            if (usuario != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", usuario);

                String usuarioJson = gson.toJson(usuario);
                out.print("{\"status\": \"success\", \"user\": " + usuarioJson + "}");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                out.print("{\"status\": \"error\", \"message\": \"Credenciales incorrectas\"}");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            out.flush();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en el servlet de login para el usuario: " + username, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Error interno en el servidor\"}");
        }
    }
    
    // El método doOptions es necesario para que CORS funcione correctamente
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setupCORS(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}