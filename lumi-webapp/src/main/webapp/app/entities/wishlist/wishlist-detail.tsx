import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './wishlist.reducer';

export const WishlistDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const wishlistEntity = useAppSelector(state => state.wishlist.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="wishlistDetailsHeading">
          <Translate contentKey="lumiApp.wishlist.detail.title">Wishlist</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{wishlistEntity.id}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.wishlist.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{wishlistEntity.createdAt ? <TextFormat value={wishlistEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="lumiApp.wishlist.student">Student</Translate>
          </dt>
          <dd>{wishlistEntity.student ? wishlistEntity.student.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.wishlist.course">Course</Translate>
          </dt>
          <dd>{wishlistEntity.course ? wishlistEntity.course.title : ''}</dd>
        </dl>
        <Button as={Link as any} to="/wishlist" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/wishlist/${wishlistEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default WishlistDetail;
