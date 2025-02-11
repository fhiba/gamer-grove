import { jwtDecode } from "jwt-decode";

export interface JwtPayload {
  id?: number;
  sub?: string;
  role?: string;
  exp?: number;
  iat?: number;
  [key: string]: any;
}

export const decodeToken = (token: string): JwtPayload | null => {
  try {
    return jwtDecode<JwtPayload>(token);
  } catch (error) {
    throw new Error("Invalid jwtToken");
  }
};
