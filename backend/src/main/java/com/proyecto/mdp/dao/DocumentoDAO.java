package com.proyecto.mdp.dao;

import java.util.List;

import com.proyecto.mdp.model.Documento;

public interface DocumentoDAO {
    List<Documento> obtenerTodos();
    void registrarDocumento(Documento documento);
}