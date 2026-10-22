import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-profile.reducer';

export const UserProfileDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const userProfileEntity = useAppSelector(state => state.userProfile.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userProfileDetailsHeading">
          <Translate contentKey="lumiApp.userProfile.detail.title">UserProfile</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.id}</dd>
          <dt>
            <span id="phoneNumber">
              <Translate contentKey="lumiApp.userProfile.phoneNumber">Phone Number</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.phoneNumber}</dd>
          <dt>
            <span id="bio">
              <Translate contentKey="lumiApp.userProfile.bio">Bio</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.bio}</dd>
          <dt>
            <span id="schoolName">
              <Translate contentKey="lumiApp.userProfile.schoolName">School Name</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.schoolName}</dd>
          <dt>
            <span id="gradeLevel">
              <Translate contentKey="lumiApp.userProfile.gradeLevel">Grade Level</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.gradeLevel}</dd>
          <dt>
            <span id="is2faEnabled">
              <Translate contentKey="lumiApp.userProfile.is2faEnabled">Is 2 Fa Enabled</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.is2faEnabled ? 'true' : 'false'}</dd>
          <dt>
            <span id="instructorStatus">
              <Translate contentKey="lumiApp.userProfile.instructorStatus">Instructor Status</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.instructorStatus}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.userProfile.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {userProfileEntity.updatedAt ? <TextFormat value={userProfileEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.userProfile.user">User</Translate>
          </dt>
          <dd>{userProfileEntity.user ? userProfileEntity.user.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/user-profile" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/user-profile/${userProfileEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserProfileDetail;
