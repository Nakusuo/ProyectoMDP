import './globals.css';
import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import { AuthProvider } from '@/components/auth/AuthContext'; // Importar

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'Mesa de Partes - PROYECTOMDP',
  description: 'Sistema de gestión documental',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="es">
      <body className={inter.className}>
        <AuthProvider> {/* Envolver todo con el AuthProvider */}
          {children}
        </AuthProvider>
      </body>
    </html>
  );
}