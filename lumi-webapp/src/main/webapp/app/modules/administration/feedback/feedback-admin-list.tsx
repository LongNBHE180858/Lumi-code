import React, { useEffect, useState } from 'react';
import { Button, Form, Modal, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, translate } from 'react-jhipster';
import { Link } from 'react-router';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEye, faSort, faSortDown, faSortUp, faSync, faTrash } from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { FeedbackStatus, FeedbackType } from 'app/shared/model/feedback.model';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { deleteFeedback, getFeedbacks } from 'app/entities/feedback/feedback.reducer';
import './feedback-admin.scss';

export const FeedbackAdminList = () => {
  const dispatch = useAppDispatch();

  const [activePage, setActivePage] = useState(1);
  const [itemsPerPage] = useState(10);
  const [sortField, setSortField] = useState('createdAt');
  const [sortOrder, setSortOrder] = useState<string>(DESC);
  const [filterStatus, setFilterStatus] = useState<FeedbackStatus | ''>('');
  const [filterType, setFilterType] = useState<FeedbackType | ''>('');

  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [feedbackToDelete, setFeedbackToDelete] = useState<number | null>(null);

  const feedbacks = useAppSelector(state => state.feedback.entities);
  const totalItems = useAppSelector(state => state.feedback.totalItems);
  const loading = useAppSelector(state => state.feedback.loading);
  const updating = useAppSelector(state => state.feedback.updating);

  const fetchFeedbacks = () => {
    dispatch(
      getFeedbacks({
        page: activePage - 1,
        size: itemsPerPage,
        sort: `${sortField},${sortOrder}`,
        status: filterStatus || undefined,
        type: filterType || undefined,
      }),
    );
  };

  useEffect(() => {
    fetchFeedbacks();
  }, [activePage, sortField, sortOrder, filterStatus, filterType]);

  const handleSort = (field: string) => () => {
    if (sortField === field) {
      setSortOrder(sortOrder === ASC ? DESC : ASC);
    } else {
      setSortField(field);
      setSortOrder(DESC);
    }
  };

  const getSortIcon = (field: string) => {
    if (sortField !== field) {
      return faSort;
    }
    return sortOrder === ASC ? faSortUp : faSortDown;
  };

  const handleDeleteConfirm = () => {
    if (feedbackToDelete) {
      dispatch(deleteFeedback(feedbackToDelete)).then(res => {
        if (!('error' in res)) {
          toast.success(translate('lumiApp.feedback.admin.deleted') || 'Đã xóa phản hồi thành công');
          setDeleteModalOpen(false);
          setFeedbackToDelete(null);
          fetchFeedbacks();
        }
      });
    }
  };

  const statusLabel = (st: FeedbackStatus) => {
    const key = `lumiApp.feedback.statuses.${st}`;
    return translate(key);
  };

  const typeLabel = (tp: FeedbackType) => {
    const key = `lumiApp.feedback.types.${tp}`;
    return translate(key);
  };

  return (
    <div className="feedback-admin-page">
      <div className="feedback-admin-header">
        <h2 id="feedback-admin-heading" data-cy="feedbackAdminHeading">
          <Translate contentKey="lumiApp.feedback.admin.title">Quản lý phản hồi người dùng</Translate>
        </h2>
        <div>
          <Button variant="outline-primary" onClick={fetchFeedbacks} disabled={loading} data-cy="feedbackRefreshBtn">
            <FontAwesomeIcon icon={faSync} spin={loading} /> &nbsp;
            <Translate contentKey="lumiApp.feedback.home.refreshListLabel">Làm mới</Translate>
          </Button>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="feedback-filters-bar">
        <div className="filter-group">
          <label htmlFor="filter-type">
            <Translate contentKey="lumiApp.feedback.admin.filterType">Loại:</Translate>
          </label>
          <Form.Select
            id="filter-type"
            data-cy="feedbackFilterType"
            value={filterType}
            onChange={e => {
              setFilterType(e.target.value as FeedbackType | '');
              setActivePage(1);
            }}
          >
            <option value="">-- {translate('lumiApp.feedback.admin.filterAll')} --</option>
            <option value={FeedbackType.BUG_REPORT}>{typeLabel(FeedbackType.BUG_REPORT)}</option>
            <option value={FeedbackType.FEATURE_REQUEST}>{typeLabel(FeedbackType.FEATURE_REQUEST)}</option>
            <option value={FeedbackType.UI_UX}>{typeLabel(FeedbackType.UI_UX)}</option>
            <option value={FeedbackType.GENERAL}>{typeLabel(FeedbackType.GENERAL)}</option>
          </Form.Select>
        </div>

        <div className="filter-group">
          <label htmlFor="filter-status">
            <Translate contentKey="lumiApp.feedback.admin.filterStatus">Trạng thái:</Translate>
          </label>
          <Form.Select
            id="filter-status"
            data-cy="feedbackFilterStatus"
            value={filterStatus}
            onChange={e => {
              setFilterStatus(e.target.value as FeedbackStatus | '');
              setActivePage(1);
            }}
          >
            <option value="">-- {translate('lumiApp.feedback.admin.filterAll')} --</option>
            <option value={FeedbackStatus.NEW}>{statusLabel(FeedbackStatus.NEW)}</option>
            <option value={FeedbackStatus.IN_REVIEW}>{statusLabel(FeedbackStatus.IN_REVIEW)}</option>
            <option value={FeedbackStatus.RESOLVED}>{statusLabel(FeedbackStatus.RESOLVED)}</option>
            <option value={FeedbackStatus.DISMISSED}>{statusLabel(FeedbackStatus.DISMISSED)}</option>
          </Form.Select>
        </div>
      </div>

      {/* Table */}
      <div className="feedback-table">
        <Table responsive hover className="mb-0">
          <thead>
            <tr>
              <th>
                <Translate contentKey="lumiApp.feedback.id">Mã</Translate>
              </th>
              <th className="sortable" onClick={handleSort('type')} data-cy="sortType">
                <Translate contentKey="lumiApp.feedback.type">Loại</Translate> <FontAwesomeIcon icon={getSortIcon('type')} />
              </th>
              <th>
                <Translate contentKey="lumiApp.feedback.title">Tiêu đề</Translate>
              </th>
              <th>
                <Translate contentKey="lumiApp.feedback.sender">Người gửi</Translate>
              </th>
              <th>
                <Translate contentKey="lumiApp.feedback.contactEmail">Email liên hệ</Translate>
              </th>
              <th className="sortable" onClick={handleSort('status')} data-cy="sortStatus">
                <Translate contentKey="lumiApp.feedback.status">Trạng thái</Translate> <FontAwesomeIcon icon={getSortIcon('status')} />
              </th>
              <th className="sortable" onClick={handleSort('createdAt')} data-cy="sortCreatedAt">
                <Translate contentKey="lumiApp.feedback.createdAt">Thời gian</Translate> <FontAwesomeIcon icon={getSortIcon('createdAt')} />
              </th>
              <th className="text-end">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {feedbacks && feedbacks.length > 0 ? (
              feedbacks.map(f => (
                <tr key={f.id} data-cy={`feedbackRow-${f.id}`}>
                  <td>
                    <Link to={`/admin/feedback/${f.id}`} className="fw-bold text-decoration-none">
                      #{f.id}
                    </Link>
                  </td>
                  <td>
                    <span className="badge-type">{typeLabel(f.type)}</span>
                  </td>
                  <td className="fw-semibold text-truncate" style={{ maxWidth: '240px' }} title={f.title}>
                    {f.title}
                  </td>
                  <td>
                    {f.senderIdentity ? (
                      f.senderIdentity === 'Tài khoản đã bị xóa' ? (
                        <span className="deleted-user-label">{translate('lumiApp.feedback.admin.deletedAccount')}</span>
                      ) : (
                        <span>{f.senderIdentity}</span>
                      )
                    ) : (
                      <span className="deleted-user-label">{translate('lumiApp.feedback.admin.deletedAccount')}</span>
                    )}
                  </td>
                  <td>{f.contactEmail}</td>
                  <td>
                    <span className={`badge-status status-${f.status}`}>{statusLabel(f.status)}</span>
                  </td>
                  <td>{f.createdAt && <TextFormat value={f.createdAt} type="date" format={APP_DATE_FORMAT} blankOnInvalid />}</td>
                  <td className="text-end">
                    <div className="btn-group">
                      <Button as={Link as any} to={`/admin/feedback/${f.id}`} variant="info" size="sm" data-cy={`viewFeedbackBtn-${f.id}`}>
                        <FontAwesomeIcon icon={faEye} />
                      </Button>
                      <Button
                        variant="danger"
                        size="sm"
                        onClick={() => {
                          setFeedbackToDelete(f.id);
                          setDeleteModalOpen(true);
                        }}
                        data-cy={`deleteFeedbackBtn-${f.id}`}
                      >
                        <FontAwesomeIcon icon={faTrash} />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={8} className="text-center py-4 text-muted">
                  <Translate contentKey="lumiApp.feedback.home.notFound">Không tìm thấy phản hồi nào</Translate>
                </td>
              </tr>
            )}
          </tbody>
        </Table>
      </div>

      {/* Pagination */}
      {totalItems > 0 && (
        <div className="mt-3">
          <div className="justify-content-center d-flex">
            <JhiItemCount page={activePage} total={totalItems} itemsPerPage={itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={activePage}
              onSelect={page => setActivePage(page)}
              maxButtons={5}
              itemsPerPage={itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      <Modal show={deleteModalOpen} onHide={() => setDeleteModalOpen(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa phản hồi</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Translate contentKey="lumiApp.feedback.admin.deleteConfirm" interpolate={{ id: feedbackToDelete }}>
            {`Bạn có chắc chắn muốn xóa phản hồi #${feedbackToDelete} này không?`}
          </Translate>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setDeleteModalOpen(false)}>
            Hủy
          </Button>
          <Button variant="danger" onClick={handleDeleteConfirm} disabled={updating} data-cy="confirmDeleteFeedbackBtn">
            {updating ? 'Đang xóa...' : 'Xóa phản hồi'}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default FeedbackAdminList;
