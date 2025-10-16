package com.proyecto.mdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.proyecto.mdp.model.Usuario;
import com.proyecto.mdp.utils.ConexionDB;

public class UsuarioDAOImpl implements UsuarioDAO {

    // 1. Se crea una instancia del Logger para esta clase.
    private static final Logger LOGGER = Logger.getLogger(UsuarioDAOImpl.class.getName());

    @Override
    public Usuario login(String username, String password) {
        Usuario usuario = null;
        String sql = "SELECT ID_usuario, nombre, apellido, email, username, avatarUrl " +
                     "FROM usuarios WHERE username = ? AND password_hash = SHA2(?, 256) AND activo = TRUE";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("ID_usuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setAvatarUrl(rs.getString("avatarUrl"));
                }
            }
        } catch (SQLException e) {
            // 2. Se reemplaza e.printStackTrace() con el Logger.
            // Esto registra el error de forma estructurada sin ensuciar la consola.
            LOGGER.log(Level.SEVERE, "Error al intentar validar el usuario: " + username, e);
        }
        return usuario;
    }
}