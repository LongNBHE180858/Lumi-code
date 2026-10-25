import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';
import {
  ArrowDown,
  ArrowUp,
  ArrowUpDown,
  ChevronDown,
  ChevronLeft,
  ChevronRight,
  ChevronsLeft,
  ChevronsRight,
  Eye,
  Plus,
  RefreshCw,
  Search,
} from 'lucide-react';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { Authority } from 'app/shared/jhipster/constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, SORT } from 'app/shared/util/pagination.constants';

import { getUsersAsAdmin, setUserActivation } from './user-management.reducer';
import { MANAGED_ROLES, getManagedRole, getManagedRoleLabel } from './user-role';

const USERS_PER_PAGE = 10;
const roleFromQuery = (search: string): Authority | '' => {
  const role = new URLSearchParams(search).get('role');
  return MANAGED_ROLES.find(value => value === role) ?? '';
};

export const UserManagement = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [pagination, setPagination] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, USERS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [roleFilter, setRoleFilter] = useState<Authority | ''>(() => roleFromQuery(pageLocation.search));
  const selectedRoleClass = roleFilter ? `admin-user-role-${roleFilter.toLowerCase().replace('role_', '')}` : '';
  const [searchInput, setSearchInput] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [activationPendingLogin, setActivationPendingLogin] = useState<string | null>(null);
  const [activationError, setActivationError] = useState<string | null>(null);

  const getUsersFromProps = () => {
    const request = dispatch(
      getUsersAsAdmin({
        page: pagination.activePage - 1,
        size: pagination.itemsPerPage,
        sort: `${pagination.sort},${pagination.order}`,
        role: roleFilter || undefined,
        search: searchQuery || undefined,
      }),
    );
    const endURL = `?page=${pagination.activePage}&sort=${pagination.sort},${pagination.order}${roleFilter ? `&role=${roleFilter}` : ''}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
    return request;
  };

  useEffect(() => {
    const request = getUsersFromProps();
    return () => request.abort();
  }, [pagination.activePage, pagination.order, pagination.sort, roleFilter, searchQuery]);

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      const query = searchInput.trim();
      if (query !== searchQuery) {
        setSearchQuery(query);
        setPagination(current => (current.activePage === 1 ? current : { ...current, activePage: 1 }));
      }
    }, 300);
    return () => window.clearTimeout(timeout);
  }, [searchInput, searchQuery]);

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
  };

  const toggleActivation = async (login: string, activated: boolean) => {
    setActivationPendingLogin(login);
    setActivationError(null);
    const result = await dispatch(setUserActivation({ login, activated }));
    if (setUserActivation.rejected.match(result)) {
      setActivationError(`Không thể cập nhật trạng thái tài khoản ${login}. Vui lòng thử lại.`);
    }
    setActivationPendingLogin(null);
  };

  const users = useAppSelector(state => state.userManagement.users);
  const totalItems = useAppSelector(state => state.userManagement.totalItems);
  const loading = useAppSelector(state => state.userManagement.loading);
  const visibleUsers = users;
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
      <div className="admin-user-search">
        <Search size={18} aria-hidden="true" />
        <input
          type="search"
          value={searchInput}
          onChange={event => setSearchInput(event.target.value)}
          placeholder="Tìm theo tên hoặc email"
          aria-label="Tìm tài khoản theo tên hoặc email"
        />
      </div>
      {activationError && (
        <p className="admin-user-activation-error" role="alert">
          {activationError}
        </p>
      )}
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
            <th className="hand admin-user-centered-column" onClick={sort('langKey')}>
              <Translate contentKey="userManagement.langKey">Lang Key</Translate> {getSortIconByFieldName('langKey')}
            </th>
            <th className="admin-user-centered-column">
              <div className="admin-user-role-filter">
                <label htmlFor="admin-user-role-filter">
                  <Translate contentKey="userManagement.profiles">Role</Translate>
                </label>
                <span className={`admin-user-role admin-user-role-filter-chip ${selectedRoleClass}`}>
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
                  <ChevronDown size={12} aria-hidden="true" />
                </span>
              </div>
            </th>
            <th className="hand" onClick={sort('createdDate')}>
              <Translate contentKey="userManagement.createdDate">Created Date</Translate> {getSortIconByFieldName('createdDate')}
            </th>
            <th className="admin-user-centered-column">
              <Translate contentKey="userManagement.status">Status</Translate>
            </th>
            <th />
          </tr>
        </thead>
        <tbody>
          {visibleUsers.map(user => {
            const isAdmin = user.authorities?.includes(Authority.ADMIN);
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
                <td className="admin-user-centered-column">{user.langKey}</td>
                <td className="admin-user-centered-column">
                  <span
                    className={`admin-user-role admin-user-role-${getManagedRole(user.authorities).toLowerCase().replace('role_', '')}`}
                  >
                    {getManagedRoleLabel(getManagedRole(user.authorities))}
                  </span>
                </td>
                <td>{user.createdDate && <TextFormat value={user.createdDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />}</td>
                <td className="admin-user-centered-column">
                  {isAdmin ? (
                    <span className={`admin-user-status ${statusClass}`}>{statusLabel}</span>
                  ) : (
                    <Button
                      variant="link"
                      className={`admin-user-status ${statusClass}`}
                      onClick={() => {
                        if (user.login) void toggleActivation(user.login, !user.activated);
                      }}
                      disabled={activationPendingLogin !== null || !user.login}
                      aria-label={`${user.activated ? 'Tạm khóa' : 'Kích hoạt'} tài khoản ${user.login}`}
                      title={user.activated ? 'Tạm khóa tài khoản' : 'Kích hoạt tài khoản'}
                      data-cy="entityActivationButton"
                    >
                      {statusLabel}
                    </Button>
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
                  </div>
                </td>
              </tr>
            );
          })}
          {!loading && visibleUsers.length === 0 && (
            <tr>
              <td colSpan={8} className="admin-user-empty">
                {searchQuery ? 'Không tìm thấy tài khoản theo tên hoặc email.' : 'Chưa có người dùng.'}
              </td>
            </tr>
          )}
        </tbody>
      </Table>
      {searchQuery && (
        <p className="admin-user-management-search-count" role="status">
          Tìm thấy {totalItems} tài khoản.
        </p>
      )}
      {!!totalItems && (
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
