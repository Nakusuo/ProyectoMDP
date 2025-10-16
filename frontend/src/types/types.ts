// Este tipo debe coincidir con el objeto Usuario que envia el backend
export interface User {
  id: number;
  nombre: string;
  apellido: string;
  username: string;
  email: string;
  avatarUrl: string;
}