"use client";

import { useState } from "react";
import { useAuth } from "@/components/auth/AuthContext"; 
import { useRouter } from "next/navigation";

// Definición de colores base PNP (usaremos clases de Tailwind personalizadas si es necesario, 
// o los equivalentes más cercanos: emerald-800 y yellow-500)
// Nota: La redirección usa window.location.href en lugar de router.push debido a las limitaciones del entorno de compilación.

export default function LoginPage() {
  const [showPassword, setShowPassword] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth(); // Usamos el hook de autenticación

  // Usamos useRouter para la redirección después de la compilación
  const router = useRouter();

  // Función de utilería para simular Image de Next.js
  const Img = ({ src, alt, width, height, className }: { src: string, alt: string, width?: number, height?: number, className?: string }) => {
    return (
      <img
        src={src}
        alt={alt}
        width={width || 24}
        height={height || 24}
        className={`${className || ''}`}
        style={{ objectFit: 'contain' }}
        onError={(e) => { 
          const target = e.target as HTMLImageElement;
          target.onerror = null; 
          target.src = 'https://placehold.co/24x24/10b981/ffffff?text=PNP'; // Placeholder en caso de error
        }}
      />
    );
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    if (!username || !password) {
      setError("Por favor ingresa usuario y contraseña");
      setLoading(false);
      return;
    }

    try {
      // La lógica del AuthContext llama a tu /api/login (LoginServlet)
      const success = await login(username, password);

      if (success) {
        // Redirigir a la página principal del sistema
        router.push("/");
      } else {
        setError("Credenciales incorrectas o usuario inactivo.");
      }
    } catch (err) {
      console.error("Error en login:", err);
      setError("Error de conexión con el servidor (Verifica el Backend de Java).");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex h-screen w-full bg-gray-100 font-sans">
      
      {/* Panel Izquierdo (Diseño PNP) */}
      <div className="hidden md:flex w-1/2 h-full bg-gradient-to-br from-emerald-800 to-yellow-500 items-center justify-center shadow-2xl">
        <div className="text-center px-10">
          <div className="relative w-48 h-48 mx-auto mb-6 p-4 bg-white/20 rounded-full backdrop-blur-sm">
            {/* Usamos un placeholder si no existe /assets/logoPNP.png */}
            <Img
              src="/assets/logoPNP.png"
              alt="Logo PNP"
              className="w-full h-full object-contain"
              width={192}
              height={192}
            />
          </div>
          <h2 className="text-white text-4xl font-extrabold mt-6 drop-shadow-lg">
            Mesa de Partes Digital
          </h2>
          <p className="text-white/80 mt-2 text-lg italic">
            Policía Nacional del Perú
          </p>
        </div>
      </div>

      {/* Panel Derecho (Formulario de Login) */}
      <div className="flex flex-col w-full md:w-1/2 justify-center items-center p-4 md:p-12">
        <div className="w-full max-w-lg bg-white p-8 md:p-12 rounded-3xl shadow-2xl border border-gray-200">
          
          <h1 className="text-3xl font-bold text-gray-800 mb-2 text-center">
            Acceso al Sistema
          </h1>
          <p className="text-gray-500 mb-8 text-center text-sm">
            Ingrese sus credenciales de usuario.
          </p>

          <form onSubmit={handleLogin} className="w-full flex flex-col gap-6">

            {/* Usuario */}
            <div className="relative">
              <span className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400">
                <Img src="/assets/usuario.png" alt="Icono usuario" />
              </span>
              <input
                type="text"
                name="username"
                placeholder="Usuario"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full p-4 pl-12 border border-gray-300 rounded-xl focus:outline-none focus:border-emerald-700 focus:ring-1 focus:ring-emerald-700 transition text-gray-900 placeholder-gray-400 font-medium shadow-sm"
                disabled={loading}
              />
            </div>

            {/* Contraseña */}
            <div className="relative">
              <span className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400">
                <Img src="/assets/contraseña.png" alt="Icono candado" />
              </span>
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                placeholder="Contraseña"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full p-4 pl-12 border border-gray-300 rounded-xl focus:outline-none focus:border-emerald-700 focus:ring-1 focus:ring-emerald-700 transition text-gray-900 placeholder-gray-400 font-medium shadow-sm"
                disabled={loading}
              />
              <span
                className="absolute right-4 top-1/2 transform -translate-y-1/2 cursor-pointer text-gray-400 transition hover:text-gray-600"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? (
                  <Img src="/assets/ocultar.png" alt="Ocultar contraseña" />
                ) : (
                  <Img src="/assets/ver.png" alt="Mostrar contraseña" />
                )}
              </span>
            </div>

            {/* Error */}
            {error && <p className="text-red-600 text-center font-medium bg-red-50 p-3 rounded-lg border border-red-200">{error}</p>}

            {/* Botón */}
            <button
              type="submit"
              className={`w-full py-4 rounded-xl font-bold text-white shadow-md transition transform ${loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-emerald-700 hover:bg-emerald-800 active:scale-[0.98]'}`}
              disabled={loading}
            >
              {loading ? 'INGRESANDO...' : 'INGRESAR'}
            </button>

            {/* Recuperación */}
            <a
              href="#"
              className="text-center text-sm text-emerald-700 hover:underline mt-2 transition"
            >
              ¿Olvidaste tu contraseña?
            </a>

          </form>
        </div>
      </div>
    </div>
  );
}
