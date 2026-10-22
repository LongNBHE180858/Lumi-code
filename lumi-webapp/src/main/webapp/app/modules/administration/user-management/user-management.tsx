import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';
import {
  ArrowDown,
  ArrowUp,
  ArrowUpDown,
  ChevronLeft,
  ChevronRight,
  ChevronsLeft,
  ChevronsRight,
  Eye,
  Pencil,
  Plus,
  RefreshCw,
  Trash2,
} from 'lucide-react';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { Authority } from 'app/shared/jhipster/constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, SORT } from 'app/shared/util/pagination.constants';

import { getAdminCounts, getUsersAsAdmin, updateUser } from './user-management.reducer';
import { MANAGED_ROLES, getManagedRole, getManagedRoleLabel } from './user-role';

interface UserManagementProps {
  searchValue: string;
}

const USERS_PER_PAGE = 10;
const roleFromQuery = (search: string): Authority | '' => {
  const role = new URLSearchParams(search).get('role');
  return MANAGED_ROLES.find(value => value === role) ?? '';
};

export const UserManagement = ({ searchValue }: UserManagementProps) => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [pagination, setPagination] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, USERS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [roleFilter, setRoleFilter] = useState<Authority | ''>(() => roleFromQuery(pageLocation.search));

  const getUsersFromProps = () => {
    dispatch(
      getUsersAsAdmin({
        page: pagination.activePage - 1,
        size: pagination.itemsPerPage,
        sort: `${pagination.sort},${pagination.order}`,
        role: roleFilter || undefined,
      }),
    );
    const endURL = `?page=${pagination.activePage}&sort=${pagination.sort},${pagination.order}${roleFilter ? `&role=${roleFilter}` : ''}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    getUsersFromProps();
  }, [pagination.activePage, pagination.order, pagination.sort, roleFilter]);

  useEffect(() => {
    dispatch(getAdminCounts());
  }, [dispatch]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sortParam = params.get(SORT);
    setRoleFilter(roleFromQuery(pageLocation.search));
    if (page && sortParam) {
      const sortSplit = sortParam.split(',');
      setPagination({
        ...pagination,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () =>
    setPagination({
      ...pagination,
      order: pagination.order === ASC ? DESC : ASC,
      sort: p,
    });

  const handlePagination = currentPage =>
    setPagination({
      ...pagination,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    getUsersFromProps();
    dispatch(getAdminCounts());
  };

  const toggleActive = user => () => {
    dispatch(
      updateUser({
        ...user,
        activated: !user.activated,
      }),
    ).then(result => {
      if (updateUser.fulfilled.match(result)) {
        getUsersFromProps();
      }
    });
  };

  const account = useAppSelector(state => state.authentication.account);
  const users = useAppSelector(state => state.userManagement.users);
  const totalItems = useAppSelector(state => state.userManagement.totalItems);
  const loading = useAppSelector(state => state.userManagement.loading);
  const adminCounts = useAppSelector(state => state.userManagement.adminCounts);
  const searchTerm = searchValue.trim().toLocaleLowerCase('vi');
  const visibleUsers = searchTerm
    ? users.filter(user =>
        [user.login, user.email, user.firstName, user.lastName].some(value => value?.toLocaleLowerCase('vi').includes(searchTerm)),
      )
    : users;
  const totalPages = Math.ceil(totalItems / pagination.itemsPerPage);
  const pageCount = Math.min(5, totalPages);
  const firstVisiblePage = Math.max(1, Math.min(pagination.activePage - 2, totalPages - pageCount + 1));
  const visiblePages = Array.from({ length: pageCount }, (_, index) => firstVisiblePage + index);
  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = pagination.sort;
    const { order } = pagination;
    if (sortFieldName !== fieldName) {
      return <ArrowUpDown size={14} className="align-middle" aria-hidden="true" />;
    }
    return order === ASC ? (
      <ArrowUp size={14} className="align-middle" aria-hidden="true" />
    ) : (
      <ArrowDown size={14} className="align-middle" aria-hidden="true" />
    );
  };

  return (
    <div className="admin-user-management">
      <div className="admin-user-management-toolbar">
        <h2 id="user-management-page-heading" data-cy="UserManagementHeading">
          <Translate contentKey="userManagement.home.title">Users</Translate>
        </h2>
        <div className="admin-user-management-actions">
          <Button variant="light" className="admin-user-button admin-user-button-secondary" onClick={handleSyncList} disabled={loading}>
            <RefreshCw size={16} className="align-middle" aria-hidden="true" /> Làm mới
          </Button>
          <Link to="new" className="btn admin-user-button admin-user-button-primary jh-create-entity" data-cy="entityCreateButton">
            <Plus size={16} className="align-middle" aria-hidden="true" />{' '}
            <Translate contentKey="userManagement.home.createLabel">Create a new user</Translate>
          </Link>
        </div>
      </div>
      <div className="admin-user-role-filter">
        <label htmlFor="admin-user-role-filter">Vai trò</label>
        <select
          id="admin-user-role-filter"
          value={roleFilter}
          onChange={event => {
            setRoleFilter(event.target.value as Authority | '');
            setPagination(current => ({ ...current, activePage: 1 }));
          }}
        >
          <option value="">Tất cả</option>
          {MANAGED_ROLES.map(role => (
            <option key={role} value={role}>
              {getManagedRoleLabel(role)}
            </option>
          ))}
        </select>
      </div>
      <Table responsive className="admin-user-table">
        <thead>
          <tr>
            <th className="hand" onClick={sort('id')}>
              <Translate contentKey="global.field.id">ID</Translate> {getSortIconByFieldName('id')}
            </th>
            <th className="hand" onClick={sort('login')}>
              <Translate contentKey="userManagement.login">Login</Translate> {getSortIconByFieldName('login')}
            </th>
            <th className="hand" onClick={sort('email')}>
              <Translate contentKey="userManagement.email">Email</Translate> {getSortIconByFieldName('email')}
            </th>
            <th />
            <th className="hand admin-user-centered-column" onClick={sort('langKey')}>
              <Translate contentKey="userManagement.langKey">Lang Key</Translate> {getSortIconByFieldName('langKey')}
            </th>
            <th className="admin-user-centered-column">
              <Translate contentKey="userManagement.profiles">Profiles</Translate>
            </th>
            <th className="hand" onClick={sort('createdDate')}>
              <Translate contentKey="userManagement.createdDate">Created Date</Translate> {getSortIconByFieldName('createdDate')}
            </th>
            <th className="hand" onClick={sort('lastModifiedBy')}>
              <Translate contentKey="userManagement.lastModifiedBy">Last Modified By</Translate> {getSortIconByFieldName('lastModifiedBy')}
            </th>
            <th id="modified-date-sort" className="hand" onClick={sort('lastModifiedDate')}>
              <Translate contentKey="userManagement.lastModifiedDate">Last Modified Date</Translate>{' '}
              {getSortIconByFieldName('lastModifiedDate')}
            </th>
            <th />
          </tr>
        </thead>
        <tbody>
          {visibleUsers.map(user => {
            const isSelf = Boolean(account.login && user.login && account.login.toLowerCase() === user.login.toLowerCase());
            const isAdmin = user.authorities?.includes(Authority.ADMIN);
            const isLastAdmin =
              isAdmin && (!adminCounts || adminCounts.adminCount <= 1 || (user.activated && adminCounts.activeAdminCount <= 1));
            const isProtected = isSelf || isLastAdmin;
            const statusClass = user.activated ? 'admin-user-status-active' : 'admin-user-status-inactive';
            const statusLabel = user.activated ? (
              <Translate contentKey="userManagement.activated">Activated</Translate>
            ) : (
              <Translate contentKey="userManagement.deactivated">Deactivated</Translate>
            );

            return (
              <tr id={user.login} key={user.id} data-cy="entityTable">
                <td>
                  <Button as={Link as any} to={user.login} variant="link" size="sm" className="admin-user-id-link">
                    {user.id}
                  </Button>
                </td>
                <td title={user.login}>{user.login}</td>
                <td title={user.email}>{user.email}</td>
                <td>
                  {isProtected ? (
                    <span className={`admin-user-status ${statusClass}`}>{statusLabel}</span>
                  ) : (
                    <Button variant="link" className={`admin-user-status ${statusClass}`} onClick={toggleActive(user)}>
                      {statusLabel}
                    </Button>
                  )}
                </td>
                <td className="admin-user-centered-column">{user.langKey}</td>
                <td className="admin-user-centered-column">
                  <span
                    className={`admin-user-role admin-user-role-${getManagedRole(user.authorities).toLowerCase().replace('role_', '')}`}
                  >
                    {getManagedRoleLabel(getManagedRole(user.authorities))}
                  </span>
                </td>
                <td>{user.createdDate && <TextFormat value={user.createdDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />}</td>
                <td title={user.lastModifiedBy}>{user.lastModifiedBy}</td>
                <td>
                  {user.lastModifiedDate && (
                    <TextFormat value={user.lastModifiedDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />
                  )}
                </td>
                <td className="text-end">
                  <div className="admin-user-row-actions">
                    <Button
                      as={Link as any}
                      to={user.login}
                      variant="link"
                      size="sm"
                      className="admin-user-row-action"
                      aria-label="Xem tài khoản"
                      data-cy="entityDetailsButton"
                    >
                      <Eye size={16} className="align-middle" aria-hidden="true" />{' '}
                      <span className="d-none d-md-inline">
                        <Translate contentKey="entity.action.view">View</Translate>
                      </span>
                    </Button>
                    <Button
                      as={Link as any}
                      to={`${user.login}/edit`}
                      variant="link"
                      size="sm"
                      className="admin-user-row-action"
                      aria-label="Sửa thông tin tài khoản"
                      data-cy="entityEditButton"
                    >
                      <Pencil size={16} className="align-middle" aria-hidden="true" />{' '}
                      <span className="d-none d-md-inline">
                        <Translate contentKey="entity.action.edit">Edit</Translate>
                      </span>
                    </Button>
                    {!isProtected && (
                      <Button
                        as={Link as any}
                        to={`${user.login}/delete`}
                        variant="link"
                        size="sm"
                        className="admin-user-row-action admin-user-row-action-danger"
                        aria-label="Xóa tài khoản"
                        data-cy="entityDeleteButton"
                      >
                        <Trash2 size={16} className="align-middle" aria-hidden="true" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    )}
                  </div>
                </td>
              </tr>
            );
          })}
          {!loading && visibleUsers.length === 0 && (
            <tr>
              <td colSpan={10} className="admin-user-empty">
                {searchTerm ? 'Không tìm thấy người dùng trong trang hiện tại.' : 'Chưa có người dùng.'}
              </td>
            </tr>
          )}
        </tbody>
      </Table>
      {searchTerm && (
        <p className="admin-user-management-search-count" role="status">
          Tìm thấy {visibleUsers.length} người dùng trong trang hiện tại.
        </p>
      )}
      {!searchTerm && !!totalItems && (
        <div className={users.length > 0 ? 'admin-user-pagination-footer' : 'd-none'}>
          <div className="admin-user-item-count">
            Hiển thị {(pagination.activePage - 1) * pagination.itemsPerPage + 1}–
            {Math.min(pagination.activePage * pagination.itemsPerPage, totalItems)} trong tổng số {totalItems} tài khoản.
          </div>
          <nav className="admin-user-pagination" aria-label="Phân trang người dùng">
            <button type="button" aria-label="Trang đầu" disabled={pagination.activePage === 1} onClick={() => handlePagination(1)}>
              <ChevronsLeft size={16} aria-hidden="true" />
            </button>
            <button
              type="button"
              aria-label="Trang trước"
              disabled={pagination.activePage === 1}
              onClick={() => handlePagination(pagination.activePage - 1)}
            >
              <ChevronLeft size={16} aria-hidden="true" />
            </button>
            {visiblePages.map(page => (
              <button
                key={page}
                type="button"
                className={page === pagination.activePage ? 'active' : undefined}
                aria-label={`Trang ${page}`}
                aria-current={page === pagination.activePage ? 'page' : undefined}
                onClick={() => handlePagination(page)}
              >
                {page}
              </button>
            ))}
            <button
              type="button"
              aria-label="Trang sau"
              disabled={pagination.activePage === totalPages}
              onClick={() => handlePagination(pagination.activePage + 1)}
            >
              <ChevronRight size={16} aria-hidden="true" />
            </button>
            <button
              type="button"
              aria-label="Trang cuối"
              disabled={pagination.activePage === totalPages}
              onClick={() => handlePagination(totalPages)}
            >
              <ChevronsRight size={16} aria-hidden="true" />
            </button>
          </nav>
        </div>
      )}
    </div>
  );
};

export default UserManagement;
