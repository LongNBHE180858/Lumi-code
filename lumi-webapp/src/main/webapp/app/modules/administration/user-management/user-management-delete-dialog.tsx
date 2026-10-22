import React, { useEffect } from 'react';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { useNavigate, useParams } from 'react-router';
import { Ban, Trash2, X } from 'lucide-react';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { deleteUser, getUser } from './user-management.reducer';

export const UserManagementDeleteDialog = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();
  const { login } = useParams<'login'>();

  useEffect(() => {
    dispatch(getUser(login!));
  }, []);

  const handleClose = () => {
    navigate('/admin/user-management');
  };

  const user = useAppSelector(state => state.userManagement.user);

  const confirmDelete = () => {
    dispatch(deleteUser(user.login));
    handleClose();
  };

  return (
    <Modal show onHide={handleClose} className="admin-user-delete-modal">
      <ModalHeader data-cy="userManagementDeleteDialogHeading">
        <ModalTitle>
          <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
        </ModalTitle>
        <button type="button" className="admin-user-modal-close" onClick={handleClose} aria-label="Đóng">
          <X size={18} aria-hidden="true" />
        </button>
      </ModalHeader>
      <ModalBody>
        <Translate contentKey="userManagement.delete.question" interpolate={{ login: user.login }}>
          Are you sure you want to delete this User?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button variant="light" className="admin-user-button admin-user-button-secondary" onClick={handleClose}>
          <Ban size={16} className="align-middle" aria-hidden="true" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button
          variant="danger"
          className="admin-user-button admin-user-button-danger"
          onClick={confirmDelete}
          data-cy="entityConfirmDeleteButton"
        >
          <Trash2 size={16} className="align-middle" aria-hidden="true" />
          &nbsp;
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default UserManagementDeleteDialog;
