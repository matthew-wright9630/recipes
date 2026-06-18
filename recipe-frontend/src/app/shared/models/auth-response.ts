export interface AuthResponse {
  id: number;
  accessToken: string;
  refreshToken: string;
  email: string;
  username: string;
  role: string;
  avatarUrl: string;
}
