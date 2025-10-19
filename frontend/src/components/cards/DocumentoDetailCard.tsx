import React, { useState } from 'react';
import { Documento, User } from '@/utils/types';
import { useAuth } from '@/components/auth/AuthContext';
import { getApiUrl } from '@/utils/api';
import { Loader2, Send, CheckCircle2, AlertTriangle, UserPlus, XCircle } from 'lucide-react';

interface AsignacionFormProps {
  documento: Documento;
  onUpdate: () => void; // Función para recargar los datos
}

// URL del Servlet de Documentos
const DOCUMENTOS_API_URL = getApiUrl('/api/documentos');

const AsignacionForm: React.FC<AsignacionFormProps> = ({ documento, onUpdate }) => {
  const { user } = useAuth();
  const [nuevoEstado, setNuevoEstado] = useState(documento.estado);
  const [usuarioAsignadoId, setUsuarioAsignadoId] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error', text: string } | null>(null);

  // NOTA: En un sistema real, harías un GET /api/usuarios/areas para obtener esta lista.
  // Aquí usamos un mock temporal:
  const mockUsuarios: User[] = [
    { id: 2, nombre: 'Edwin', apellido: 'Cisneros', username: 'ecisneros', rol: 'Trabajador', email: 'e@pnp.gob.pe', avatarUrl: '' },
    { id: 5, nombre: 'Gersson', apellido: 'Huamán', username: 'ghuaman', rol: 'Jefatura', email: 'g@pnp.gob.pe', avatarUrl: '' },
    { id: 10, nombre: 'Ana', apellido: 'López', username: 'alopez', rol: 'Trabajador', email: 'a@pnp.gob.pe', avatarUrl: '' },
  ];

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage(null);
    setLoading(true);

    if (!user) {
      setMessage({ type: 'error', text: 'No autorizado. Inicie sesión.' });
      setLoading(false);
      return;
    }

    try {
      // 1. Crear el cuerpo de la actualización
      const updateData = {
        estado: nuevoEstado,
        // En un sistema real, enviarías el ID del usuario al que se asigna
        usuarioAsignadoId: usuarioAsignadoId, 
        // ID del usuario que realiza la acción
        usuarioAccionId: user.id 
      };

      // 2. Realizar la petición PUT al Servlet
      const response = await fetch(`${DOCUMENTOS_API_URL}/${documento.id}`, {
        method: 'PUT', // Usamos PUT para actualizar el recurso
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updateData),
      });

      const data = await response.json();

      if (response.ok && data.status === 'success') {
        setMessage({ type: 'success', text: `Trámite actualizado: ${data.message}` });
        onUpdate(); // Recargar datos del padre
      } else {
        setMessage({ type: 'error', text: data.message || 'Error al actualizar el trámite.' });
      }

    } catch (err) {
      console.error('Error al actualizar:', err);
      setMessage({ type: 'error', text: 'Error de conexión con el servidor de Java.' });
    } finally {
      setLoading(false);
    }
  };

  const isMesaDePartes = user?.rol === 'Mesa de Partes' || user?.rol === 'Administrador';

  return (
    <div className="p-6 bg-white shadow-lg rounded-2xl border border-gray-100">
      <h3 className="text-xl font-bold text-gray-800 border-b pb-3 mb-5 flex items-center">
        <Send className="w-5 h-5 mr-2 text-emerald-700" />
        Gestión y Derivación del Trámite
      </h3>

      <form onSubmit={handleSubmit} className="space-y-6">

        {message && (
          <div className={`p-4 rounded-lg flex items-center ${message.type === 'success' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
            {message.type === 'success' ? <CheckCircle2 className="w-5 h-5 mr-2" /> : <XCircle className="w-5 h-5 mr-2" />}
            <span className="font-medium">{message.text}</span>
          </div>
        )}
        
        {/* Campo de Estado */}
        <div>
          <label htmlFor="estado" className="block text-sm font-medium text-gray-700 mb-1">
            Cambiar Estado Actual
          </label>
          <select
            id="estado"
            name="estado"
            value={nuevoEstado}
            onChange={(e) => setNuevoEstado(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-lg shadow-sm focus:border-emerald-500 focus:ring focus:ring-emerald-500 focus:ring-opacity-50 transition"
            disabled={loading}
          >
            <option value="Registrado">Registrado (Mesa de Partes)</option>
            <option value="En Proceso">En Proceso (Trabajador)</option>
            <option value="Observado">Observado (Requiere corrección)</option>
            {isMesaDePartes && <option value="Finalizado">Finalizado (Mesa de Partes - Interno)</option>}
            {isMesaDePartes && <option value="Salida">Registrar Salida (Mesa de Partes - Cierre)</option>}
          </select>
        </div>

        {/* Campo de Reasignación (Visible si el estado no es Finalizado/Salida) */}
        {(nuevoEstado !== 'Finalizado' && nuevoEstado !== 'Salida') && (
            <div>
              <label htmlFor="asignado" className="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                <UserPlus className="w-4 h-4 mr-1 text-indigo-500" />
                Reasignar a Personal / Área
              </label>
              <select
                id="asignado"
                name="asignado"
                value={usuarioAsignadoId || ''}
                onChange={(e) => setUsuarioAsignadoId(parseInt(e.target.value))}
                className="w-full p-3 border border-gray-300 rounded-lg shadow-sm focus:border-indigo-500 focus:ring focus:ring-indigo-500 focus:ring-opacity-50 transition"
                disabled={loading}
              >
                <option value="">Mantener asignación actual</option>
                {mockUsuarios.map(u => (
                  <option key={u.id} value={u.id}>{`${u.nombre} ${u.apellido} (${u.rol})`}</option>
                ))}
              </select>
            </div>
        )}

        {/* NOTA: En un sistema real, aquí iría un campo para registrar notas o anexos */}

        <button
          type="submit"
          disabled={loading}
          className={`w-full flex items-center justify-center py-3 px-4 rounded-xl font-bold text-white shadow-md transition transform ${
            loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-emerald-700 hover:bg-emerald-800 active:scale-[0.98]'
          }`}
        >
          {loading ? (
            <><Loader2 className="w-5 h-5 mr-2 animate-spin" /> Procesando...</>
          ) : (
            <><CheckCircle2 className="w-5 h-5 mr-2" /> Actualizar y Derivar</>
          )}
        </button>
        
      </form>
      
      <p className="mt-4 text-xs text-gray-500 flex items-center">
        <AlertTriangle className="w-4 h-4 mr-1" />
        Esta acción registra un evento de trazabilidad y notifica al nuevo responsable.
      </p>

    </div>
  );
};

export default AsignacionForm;