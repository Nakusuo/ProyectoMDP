package com.proyecto.mdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.proyecto.mdp.model.Usuario;
import com.proyecto.mdp.utils.ConexionDB;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario login(String username, String password) {
        Usuario usuario = null;
        // La consulta SQL usa la misma función de hash que tu script de BD
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
            e.printStackTrace(); // En una app real, usarías un logger
        }
        return usuario;
    }
}