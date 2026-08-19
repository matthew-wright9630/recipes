export interface SharedUser {
  userId: number;
  username: string;
  email: string;
  accessLevel: 'READER' | 'EDITOR';
}
