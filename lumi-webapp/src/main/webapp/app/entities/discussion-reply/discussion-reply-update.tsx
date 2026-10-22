import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getDiscussions } from 'app/entities/discussion/discussion.reducer';
import { getEntities as getDiscussionReplies } from 'app/entities/discussion-reply/discussion-reply.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, updateEntity } from './discussion-reply.reducer';

export const DiscussionReplyUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const discussions = useAppSelector(state => state.discussion.entities);
  const discussionReplies = useAppSelector(state => state.discussionReply.entities);
  const discussionReplyEntity = useAppSelector(state => state.discussionReply.entity);
  const loading = useAppSelector(state => state.discussionReply.loading);
  const updating = useAppSelector(state => state.discussionReply.updating);
  const updateSuccess = useAppSelector(state => state.discussionReply.updateSuccess);

  const handleClose = () => {
    navigate('/discussion-reply');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getDiscussions({}));
    dispatch(getDiscussionReplies({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...discussionReplyEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
      discussion: discussions.find(it => it.id.toString() === values.discussion?.toString()),
      parent: discussionReplies.find(it => it.id.toString() === values.parent?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
        }
      : {
          ...discussionReplyEntity,
          createdAt: convertDateTimeFromServer(discussionReplyEntity.createdAt),
          user: discussionReplyEntity?.user?.id,
          discussion: discussionReplyEntity?.discussion?.id,
          parent: discussionReplyEntity?.parent?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.discussionReply.home.createOrEditLabel" data-cy="DiscussionReplyCreateUpdateHeading">
            <Translate contentKey="lumiApp.discussionReply.home.createOrEditLabel">Create or edit a DiscussionReply</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm mode="all" defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="discussion-reply-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.discussionReply.content')}
                id="discussion-reply-content"
                name="content"
                data-cy="content"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 5000, message: translate('entity.validation.maxlength', { max: 5000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.discussionReply.createdAt')}
                id="discussion-reply-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="discussion-reply-user"
                name="user"
                data-cy="user"
                label={translate('lumiApp.discussionReply.user')}
                type="select"
                required
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="discussion-reply-discussion"
                name="discussion"
                data-cy="discussion"
                label={translate('lumiApp.discussionReply.discussion')}
                type="select"
                required
              >
                <option value="" key="0" />
                {discussions
                  ? discussions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="discussion-reply-parent"
                name="parent"
                data-cy="parent"
                label={translate('lumiApp.discussionReply.parent')}
                type="select"
              >
                <option value="" key="0" />
                {discussionReplies
                  ? discussionReplies.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/discussion-reply" replace variant="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default DiscussionReplyUpdate;
