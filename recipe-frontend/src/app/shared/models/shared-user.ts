export interface SharedUser {
  userId: number;
  username: string;
  permission: 'READ' | 'READ_WRITE' | 'OWNER';
}
