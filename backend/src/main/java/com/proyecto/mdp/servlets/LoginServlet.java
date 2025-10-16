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
import com.proyecto.mdp.dao.UsuarioDAOImpl; // <-- 1. Importar Level
import com.proyecto.mdp.model.Usuario; // <-- 2. Importar Logger

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // 3. Crear una instancia estática del Logger
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    
    private UsuarioDAO usuarioDAO;
    private Gson gson;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAOImpl();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ... (el resto del código del método doPost se mantiene igual) ...

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Usuario usuario = usuarioDAO.login(username, password);
            // ... (la lógica del if/else para el usuario se mantiene igual) ...
            
            // --- CÓDIGO SIN CAMBIOS ---
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
            // --- FIN CÓDIGO SIN CAMBIOS ---

        } catch (Exception e) {
            // 4. Reemplazar e.printStackTrace() con el Logger
            LOGGER.log(Level.SEVERE, "Error en el servlet de login para el usuario: " + username, e);
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Error interno en el servidor\"}");
        }
    }
    
    // ... (el método doOptions se mantiene igual) ...
}