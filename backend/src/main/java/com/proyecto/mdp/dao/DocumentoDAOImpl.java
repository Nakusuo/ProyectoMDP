package com.proyecto.mdp.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.proyecto.mdp.model.Documento;
import com.proyecto.mdp.utils.ConexionBD;

public class DocumentoDAOImpl implements DocumentoDAO {
    private static final Logger LOGGER = Logger.getLogger(DocumentoDAOImpl.class.getName());

    @Override
    public List<Documento> obtenerTodos() {
        List<Documento> documentos = new ArrayList<>();
        String sql = """
            SELECT ID_documento, codigo, titulo, descripcion, estado, remitente, fecha_ingreso
            FROM documentos ORDER BY fecha_ingreso DESC
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Documento doc = new Documento();
                doc.setId(rs.getInt("ID_documento"));
                doc.setCodigo(rs.getString("codigo"));
                doc.setTitulo(rs.getString("titulo"));
                doc.setDescripcion(rs.getString("descripcion"));
                doc.setEstado(rs.getString("estado"));
                doc.setRemitente(rs.getString("remitente"));
                doc.setFechaIngreso(rs.getTimestamp("fecha_ingreso").toLocalDateTime());
                documentos.add(doc);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todos los documentos", e);
        }
        return documentos;
    }

    @Override
    public void registrarDocumento(Documento documento) {
        String sql = """
            INSERT INTO documentos (codigo, titulo, descripcion, remitente, fecha_ingreso, ID_usuario_registro, estado)
            VALUES (?, ?, ?, ?, ?, ?, 'Registrado')
        """;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, documento.getCodigo());
            pstmt.setString(2, documento.getTitulo());
            pstmt.setString(3, documento.getDescripcion());
            pstmt.setString(4, documento.getRemitente());
            pstmt.setTimestamp(5, Timestamp.valueOf(documento.getFechaIngreso()));
            pstmt.setInt(6, documento.getIdUsuarioRegistro());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar el documento", e);
        }
    }
}
