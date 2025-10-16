package com.proyecto.mdp.dao;

import com.proyecto.mdp.model.Usuario;

public interface UsuarioDAO {
    Usuario login(String username, String password);
}