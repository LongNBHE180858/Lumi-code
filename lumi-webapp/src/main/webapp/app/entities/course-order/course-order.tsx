import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './course-order.reducer';

export const CourseOrder = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const courseOrderList = useAppSelector(state => state.courseOrder.entities);
  const loading = useAppSelector(state => state.courseOrder.loading);
  const totalItems = useAppSelector(state => state.courseOrder.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const { order } = paginationState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="course-order-heading" data-cy="CourseOrderHeading">
        <Translate contentKey="lumiApp.courseOrder.home.title">Course Orders</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="lumiApp.courseOrder.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/course-order/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="lumiApp.courseOrder.home.createLabel">Create new Course Order</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {courseOrderList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="lumiApp.courseOrder.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('originalPrice')}>
                  <Translate contentKey="lumiApp.courseOrder.originalPrice">Original Price</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('originalPrice')} />
                </th>
                <th className="hand" onClick={sort('discountAmount')}>
                  <Translate contentKey="lumiApp.courseOrder.discountAmount">Discount Amount</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('discountAmount')} />
                </th>
                <th className="hand" onClick={sort('finalPrice')}>
                  <Translate contentKey="lumiApp.courseOrder.finalPrice">Final Price</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('finalPrice')} />
                </th>
                <th className="hand" onClick={sort('revenueSplitRate')}>
                  <Translate contentKey="lumiApp.courseOrder.revenueSplitRate">Revenue Split Rate</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('revenueSplitRate')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="lumiApp.courseOrder.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('idempotencyKey')}>
                  <Translate contentKey="lumiApp.courseOrder.idempotencyKey">Idempotency Key</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('idempotencyKey')} />
                </th>
                <th className="hand" onClick={sort('rowVersion')}>
                  <Translate contentKey="lumiApp.courseOrder.rowVersion">Row Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rowVersion')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="lumiApp.courseOrder.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th>
                  <Translate contentKey="lumiApp.courseOrder.student">Student</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.courseOrder.course">Course</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.courseOrder.plan">Plan</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="lumiApp.courseOrder.coupon">Coupon</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {courseOrderList.map(courseOrder => (
                <tr key={`entity-${courseOrder.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/course-order/${courseOrder.id}`} variant="link" size="sm">
                      {courseOrder.id}
                    </Button>
                  </td>
                  <td>{courseOrder.originalPrice}</td>
                  <td>{courseOrder.discountAmount}</td>
                  <td>{courseOrder.finalPrice}</td>
                  <td>{courseOrder.revenueSplitRate}</td>
                  <td>
                    <Translate contentKey={`lumiApp.OrderStatus.${courseOrder.status}`} />
                  </td>
                  <td>{courseOrder.idempotencyKey}</td>
                  <td>{courseOrder.rowVersion}</td>
                  <td>
                    {courseOrder.createdAt ? <TextFormat type="date" value={courseOrder.createdAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{courseOrder.student ? courseOrder.student.login : ''}</td>
                  <td>{courseOrder.course ? <Link to={`/course/${courseOrder.course.id}`}>{courseOrder.course.title}</Link> : ''}</td>
                  <td>{courseOrder.plan ? <Link to={`/system-plan/${courseOrder.plan.id}`}>{courseOrder.plan.name}</Link> : ''}</td>
                  <td>{courseOrder.coupon ? <Link to={`/coupon/${courseOrder.coupon.id}`}>{courseOrder.coupon.code}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/course-order/${courseOrder.id}`}
                        variant="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/course-order/${courseOrder.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (globalThis.location.href = `/course-order/${courseOrder.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="lumiApp.courseOrder.home.notFound">No Course Orders found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={courseOrderList && courseOrderList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default CourseOrder;
