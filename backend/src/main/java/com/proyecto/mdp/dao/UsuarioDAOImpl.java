package com.proyecto.mdp.dao;

import com.proyecto.mdp.model.Usuario;
import com.proyecto.mdp.utils.ConexionBD;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDAOImpl {
    private static final Logger LOGGER = Logger.getLogger(UsuarioDAOImpl.class.getName());

    public Usuario login(String username, String password) {
        Usuario usuario = null;
        String sql = """
            SELECT u.id_usuario, u.nombre, u.apellido, u.email, u.username,
                   u.avatarUrl, r.nombre AS rol_nombre
            FROM usuarios u
            JOIN roles r ON u.id_rol = r.id_rol
            WHERE u.username = ? AND u.password_hash = SHA2(?, 256) AND u.activo = TRUE
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setUsername(rs.getString("username"));

                    // Verifica si existe avatarUrl
                    try {
                        usuario.setAvatarUrl(rs.getString("avatarUrl"));
                    } catch (SQLException e) {
                        usuario.setAvatarUrl(null);
                    }

                    usuario.setRol(rs.getString("rol_nombre"));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al validar usuario: " + username, e);
        }

        return usuario;
    }
}
