import React, { useState, useEffect, useCallback } from 'react';
import ClienteLayout from '@/components/layout/ClienteLayout';
import DocumentoDetailCard from '@/components/cards/DocumentoDetailCard';
import AsignacionForm from '@/components/forms/AsignacionForm';
import { Documento } from '@/utils/types';
import { getApiUrl } from '@/utils/api';
import { Loader2, AlertTriangle, FileText } from 'lucide-react';
import { useParams } from 'next/navigation';

// URL base del Servlet de Documentos
const DOCUMENTOS_API_URL = getApiUrl('/api/documentos');

const AsignarDocumentoPage: React.FC = () => {
  // Utilizamos useParams para obtener el ID de la URL (Next.js App Router)
  const params = useParams();
  const id = params.id as string;
  
  const [documento, setDocumento] = useState<Documento | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Función para obtener los datos del documento
  const fetchDocumento = useCallback(async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    
    try {
      // Petición GET al Servlet por ID (DocumentoServlet.doGet)
      const response = await fetch(`${DOCUMENTOS_API_URL}?id=${id}`, {
        method: 'GET',
      });

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      const data: Documento = await response.json();
      
      // El Servlet devuelve un objeto Documento
      setDocumento(data);
      
    } catch (err: any) {
      console.error("Error fetching document detail:", err);
      setError(`No se pudo cargar el documento ${id}. Verifique el Servlet y la BD.`);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchDocumento();
  }, [fetchDocumento]);

  if (loading) {
    return (
      <ClienteLayout title="Cargando Trámite...">
        <div className="flex items-center justify-center h-64">
          <Loader2 className="w-8 h-8 animate-spin text-emerald-700 mr-2" />
          <p className="text-gray-600">Buscando detalles del documento #{id}...</p>
        </div>
      </ClienteLayout>
    );
  }

  if (error || !documento) {
    return (
      <ClienteLayout title="Error al Cargar">
        <div className="p-8 bg-red-50 border border-red-300 rounded-xl text-red-700 flex items-center">
          <AlertTriangle className="w-6 h-6 mr-3" />
          <p className="font-semibold">{error || `El documento con ID ${id} no fue encontrado.`}</p>
        </div>
      </ClienteLayout>
    );
  }

  return (
    <ClienteLayout title={`Gestión del Trámite: ${documento.codigo}`}>
      
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Columna 1 y 2: Detalle y Trazabilidad */}
        <div className="lg:col-span-2">
          <DocumentoDetailCard documento={documento} />
        </div>
        
        {/* Columna 3: Formulario de Acción (Derivación/Cierre) */}
        <div className="lg:col-span-1">
          <AsignacionForm documento={documento} onUpdate={fetchDocumento} />
        </div>
        
      </div>

    </ClienteLayout>
  );
};

export default AsignarDocumentoPage;