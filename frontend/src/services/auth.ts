/* Auth Service — login, logout, session check */

import api from './api';
import type { AuthUser, LoginRequest } from '../types';

export const authService = {
  login(request: LoginRequest): Promise<AuthUser> {
    return api.post<AuthUser>('/api/auth/login', request);
  },

  logout(): Promise<void> {
    return api.post<void>('/api/auth/logout');
  },

  me(): Promise<AuthUser> {
    return api.get<AuthUser>('/api/auth/me');
  },
};

export default authService;
