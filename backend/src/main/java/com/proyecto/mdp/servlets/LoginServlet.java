package com.proyecto.mdp.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar la respuesta
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Leer el cuerpo JSON del request
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        String jsonString = jsonBuilder.toString();
        Gson gson = new Gson();

        // Evita el warning genérico al usar HashMap.class
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> data = gson.fromJson(jsonString, mapType);

        String username = data.get("username");
        String password = data.get("password");

        // Crear la respuesta
        Map<String, Object> result = new HashMap<>();

        if ("admin".equals(username) && "1234".equals(password)) {
            result.put("success", true);
            result.put("message", "Login exitoso");
        } else {
            result.put("success", false);
            result.put("message", "Usuario o contraseña incorrectos");
        }

        // Enviar JSON al cliente
        String jsonResponse = gson.toJson(result);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
        }
    }
}
