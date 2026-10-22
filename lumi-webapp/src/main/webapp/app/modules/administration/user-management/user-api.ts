import axios from 'axios';

import type { IUser } from 'app/shared/model/user.model';
import type { IQueryParams } from 'app/shared/reducers/reducer.utils';
import type { Authority } from 'app/shared/jhipster/constants';

const usersUrl = 'api/users';
const adminUsersUrl = 'api/admin/users';
const authoritiesUrl = 'api/admin/roles';

export interface AdminCounts {
  adminCount: number;
  activeAdminCount: number;
}

export interface AdminUserQueryParams extends IQueryParams {
  role?: Authority;
}

const withPagination = (url: string, { page, size, sort }: IQueryParams) =>
  `${url}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;

export const userApi = {
  getUsers: (params: IQueryParams) => axios.get<IUser[]>(withPagination(usersUrl, params)),

  getUsersAsAdmin: (params: AdminUserQueryParams) => axios.get<IUser[]>(adminUsersUrl, { params }),

  getAdminCounts: () => axios.get<AdminCounts>(`${adminUsersUrl}/admin-counts`),

  getRoles: () => axios.get<string[]>(authoritiesUrl),

  getUser: (login: string) => axios.get<IUser>(`${adminUsersUrl}/${login}`),

  createUser: (user: IUser) => axios.post<IUser>(adminUsersUrl, user),

  updateUser: (user: IUser) => axios.put<IUser>(adminUsersUrl, user),

  deleteUser: (login: string) => axios.delete<IUser>(`${adminUsersUrl}/${login}`),
};
