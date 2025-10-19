package com.proyecto.mdp.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.proyecto.mdp.dao.UsuarioDAOImpl;
import com.proyecto.mdp.model.Usuario;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private void setupCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setupCORS(resp);
        resp.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");

        BufferedReader reader = req.getReader();
        Gson gson = new Gson();
        Usuario datos = gson.fromJson(reader, Usuario.class);

        UsuarioDAOImpl dao = new UsuarioDAOImpl();
        Usuario usuario = dao.login(datos.getUsername(), datos.getPassword());


        PrintWriter out = resp.getWriter();
        if (usuario != null) {
            // ✅ Crear sesión
            HttpSession session = req.getSession(true);
            session.setAttribute("usuario", usuario);

            resp.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(usuario));
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"Usuario o contraseña incorrectos\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        setupCORS(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
