package com.proyecto.mdp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/mesa_partes_db";
    private static final String USER = "root"; // Cambia esto por tu usuario de BD
    private static final String PASSWORD = "root"; // Cambia esto por tu contraseña

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: Driver de MySQL no encontrado.", e);
        }
    }
}