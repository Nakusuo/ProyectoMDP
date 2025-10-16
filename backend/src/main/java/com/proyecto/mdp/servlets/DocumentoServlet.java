package com.proyecto.mdp.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proyecto.mdp.dao.DocumentoDAO;
import com.proyecto.mdp.dao.DocumentoDAOImpl;
import com.proyecto.mdp.model.Documento;
import com.proyecto.mdp.model.Usuario;
import com.proyecto.mdp.utils.LocalDateTimeAdapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/documentos")
public class DocumentoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DocumentoServlet.class.getName());

    private DocumentoDAO documentoDAO;
    private Gson gson;

    @Override
    public void init() {
        documentoDAO = new DocumentoDAOImpl();
        // GsonBuilder para manejar correctamente el tipo LocalDateTime
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
    
    // Habilitar CORS para permitir peticiones desde el frontend
    private void setupCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    // GET /api/documentos -> Devuelve todos los documentos
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setupCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            List<Documento> documentos = documentoDAO.obtenerTodos();
            String jsonResponse = gson.toJson(documentos);
            response.getWriter().write(jsonResponse);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en doGet de DocumentoServlet", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Error al obtener documentos\"}");
        }
    }

    // POST /api/documentos -> Registra un nuevo documento
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setupCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false); // No crear nueva sesión si no existe
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"No autorizado. Inicie sesión.\"}");
            return;
        }

        try {
            Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");

            Documento nuevoDocumento = new Documento();
            nuevoDocumento.setCodigo(request.getParameter("codigo"));
            nuevoDocumento.setTitulo(request.getParameter("titulo"));
            nuevoDocumento.setDescripcion(request.getParameter("descripcion"));
            nuevoDocumento.setRemitente(request.getParameter("remitente"));
            nuevoDocumento.setFechaIngreso(LocalDateTime.now());
            nuevoDocumento.setIdUsuarioRegistro(usuarioLogueado.getId());

            documentoDAO.registrarDocumento(nuevoDocumento);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"status\":\"success\", \"message\":\"Documento registrado correctamente\"}");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en doPost de DocumentoServlet", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Error al registrar el documento\"}");
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setupCORS(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}