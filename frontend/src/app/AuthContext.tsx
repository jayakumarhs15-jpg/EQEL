import { createContext, useContext, useState, useCallback, useEffect, type ReactNode } from 'react';
import type { AuthState, LoginRequest, Role } from '../types';
import authService from '../services/auth';

interface AuthContextValue extends AuthState {
  login: (request: LoginRequest) => Promise<void>;
  logout: () => Promise<void>;
  checkSession: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<AuthState>({
    user: null,
    isAuthenticated: false,
    isLoading: true,
  });

  const checkSession = useCallback(async () => {
    try {
      const user = await authService.me();
      setState({ user, isAuthenticated: true, isLoading: false });
    } catch {
      setState({ user: null, isAuthenticated: false, isLoading: false });
    }
  }, []);

  const login = useCallback(async (request: LoginRequest) => {
    setState(s => ({ ...s, isLoading: true }));
    try {
      const user = await authService.login(request);
      setState({ user, isAuthenticated: true, isLoading: false });
    } catch (err) {
      setState(s => ({ ...s, isLoading: false }));
      throw err;
    }
  }, []);

  const logout = useCallback(async () => {
    try {
      await authService.logout();
    } catch {
      // still clear local state
    }
    setState({ user: null, isAuthenticated: false, isLoading: false });
  }, []);

  useEffect(() => {
    checkSession();
  }, [checkSession]);

  return (
    <AuthContext.Provider value={{ ...state, login, logout, checkSession }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}

export function useRequireRole(role: Role) {
  const { user, isAuthenticated } = useAuth();
  if (!isAuthenticated || !user || user.role !== role) {
    return { authorized: false, user: null };
  }
  return { authorized: true, user };
}
