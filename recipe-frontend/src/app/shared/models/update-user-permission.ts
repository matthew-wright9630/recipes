export interface UpdateUserPermission {
  userId: number;
  permission: 'READ' | 'READ_WRITE' | 'OWNER' | 'REVOKED';
}
