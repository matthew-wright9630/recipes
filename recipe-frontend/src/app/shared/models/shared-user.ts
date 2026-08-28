export interface SharedUser {
  userId: number;
  username: string;
  avatarUrl: string;
  permission: 'READ' | 'READ_WRITE' | 'OWNER' | 'REVOKED';
}
