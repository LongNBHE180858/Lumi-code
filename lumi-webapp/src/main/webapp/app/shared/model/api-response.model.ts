export interface ApiResponse<T = any> {
  status: number;
  code: number;
  message?: string;
  data: T;
}
