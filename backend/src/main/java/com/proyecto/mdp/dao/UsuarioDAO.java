package com.proyecto.mdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.proyecto.mdp.model.Usuario;
import com.proyecto.mdp.utils.ConexionBD;

public class UsuarioDAO {

    public Usuario login(String username, String password) {
        Usuario usuario = null;

        String sql = """
            SELECT u.ID_usuario, u.nombre, u.apellido, u.email, u.username,
                   u.avatar_url AS avatarUrl, r.rol_nombre
            FROM usuarios u
            INNER JOIN roles r ON u.rol_id = r.ID_rol
            WHERE u.username = ? AND u.password_hash = SHA2(?, 256)
        """;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("ID_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setEmail(rs.getString("email"));
                usuario.setUsername(rs.getString("username"));
                usuario.setAvatarUrl(rs.getString("avatarUrl"));
                usuario.setRol(rs.getString("rol_nombre"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuario;
    }
}
