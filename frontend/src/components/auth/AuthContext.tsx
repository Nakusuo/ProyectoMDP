'use client';

import React, { createContext, useState, useContext, ReactNode, useEffect } from 'react';
import { User } from '@/types/types'; // Crearemos este archivo de tipos

// Definimos el tipo para el valor del contexto
interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  isLoading: boolean;
}

// Creamos el contexto
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Proveedor del contexto
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Intentar cargar el usuario desde sessionStorage al iniciar
  useEffect(() => {
    try {
      const storedUser = sessionStorage.getItem('user');
      if (storedUser) {
        setUser(JSON.parse(storedUser));
      }
    } catch (error) {
      console.error("Failed to parse user from sessionStorage", error);
      sessionStorage.removeItem('user');
    } finally {
      setIsLoading(false);
    }
  }, []);

  const login = async (username: string, password: string) => {
    // Usamos URLSearchParams para enviar datos como un formulario
    const body = new URLSearchParams();
    body.append('username', username);
    body.append('password', password);

    const response = await fetch('http://localhost:8080/proyectomdp/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: body.toString(),
    });

    const data = await response.json();

    if (response.ok && data.status === 'success') {
      setUser(data.user);
      // Guardar el usuario en sessionStorage para persistir entre recargas de página
      sessionStorage.setItem('user', JSON.stringify(data.user));
    } else {
      throw new Error(data.message || 'Error al iniciar sesión');
    }
  };

  const logout = () => {
    setUser(null);
    sessionStorage.removeItem('user');
    // Aquí también podrías llamar a un endpoint de logout en el backend si lo tuvieras
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
};

// Hook personalizado para usar el contexto fácilmente
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth debe ser usado dentro de un AuthProvider');
  }
  return context;
};