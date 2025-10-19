package com.proyecto.mdp.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.proyecto.mdp.dao.UsuarioDAO;
import com.proyecto.mdp.model.Usuario;

public class AuthController extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line);
        }

        Gson gson = new Gson();
        LoginRequest loginRequest = gson.fromJson(sb.toString(), LoginRequest.class);

        Usuario usuario = usuarioDAO.login(loginRequest.username, loginRequest.password);
        if (usuario != null) {
            resp.getWriter().write(gson.toJson(new LoginResponse(true, usuario)));
        } else {
            resp.getWriter().write(gson.toJson(new LoginResponse(false, null)));
        }
    }

    static class LoginRequest {
        String username;
        String password;
    }

    static class LoginResponse {
        boolean success;
        Usuario usuario;

        public LoginResponse(boolean success, Usuario usuario) {
            this.success = success;
            this.usuario = usuario;
        }
    }
}
