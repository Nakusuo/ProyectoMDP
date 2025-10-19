import React from 'react';
import Link from 'next/link';
import { Documento } from '@/utils/types.ts'; 
import { ArrowRight, FileText, Clock } from 'lucide-react';

interface DocumentosTableProps {
  documentos: Documento[];
  title: string;
}

const DocumentosTable: React.FC<DocumentosTableProps> = ({ documentos, title }) => {

  const getEstadoClasses = (estado: string) => {
    switch (estado) {
      case 'Registrado':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'En Proceso':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'Finalizado':
      case 'Salida':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'Observado':
        return 'bg-red-100 text-red-800 border-red-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const Img = ({ src, alt, className }: { src: string, alt: string, className?: string }) => {
    return (
      <img
        src={src}
        alt={alt}
        className={`${className || 'w-5 h-5 object-contain'}`}
        onError={(e) => { 
          const target = e.target as HTMLImageElement;
          target.onerror = null; 
          target.src = 'https://placehold.co/24x24/10b981/ffffff?text=DOC'; 
        }}
      />
    );
  };

  return (
    <div className="bg-white shadow-xl rounded-2xl overflow-hidden border border-gray-100">
      <div className="p-5 border-b bg-gray-50 flex justify-between items-center">
        <h2 className="text-xl font-bold text-gray-800 flex items-center">
          <FileText className="w-6 h-6 mr-2 text-emerald-700" />
          {title}
        </h2>
        <span className="text-sm text-gray-500 font-medium">{documentos.length} trámites</span>
      </div>
      
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">CÓDIGO</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">TÍTULO / ASUNTO</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">REMITENTE</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">FECHA INGRESO</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ESTADO</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">ACCIÓN</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-100">
            {documentos.map((doc) => (
              <tr key={doc.id} className="hover:bg-gray-50 transition duration-150">
                <td className="px-6 py-4 whitespace-nowrap text-sm font-semibold text-gray-900">
                  <span className="p-1.5 rounded-md bg-indigo-50 text-indigo-700 text-xs font-bold tracking-wider">
                    {doc.codigo}
                  </span>
                </td>
                <td className="px-6 py-4 max-w-xs truncate text-sm text-gray-700">
                  {doc.titulo}
                  <p className="text-xs text-gray-500 truncate">{doc.descripcion}</p>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {doc.remitente}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {new Date(doc.fechaIngreso).toLocaleDateString()}
                  <p className="text-xs text-gray-400">{new Date(doc.fechaIngreso).toLocaleTimeString()}</p>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-3 py-1 inline-flex text-xs leading-5 font-semibold rounded-full border ${getEstadoClasses(doc.estado)}`}>
                    {doc.estado}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <Link 
                    href={`/asignar/${doc.id}`} 
                    className="text-emerald-700 hover:text-emerald-900 flex items-center justify-end font-bold transition transform hover:scale-[1.03]"
                  >
                    Ver Detalle
                    <ArrowRight className="w-4 h-4 ml-1" />
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      
      {documentos.length === 0 && (
          <div className="p-8 text-center text-gray-500">
              <Clock className="w-8 h-8 mx-auto mb-2 text-gray-400" />
              <p>No se encontraron documentos para mostrar en esta lista.</p>
              <p className="text-sm mt-1">Intenta registrar un nuevo documento o verifica los filtros.</p>
          </div>
      )}

    </div>
  );
};

export default DocumentosTable;