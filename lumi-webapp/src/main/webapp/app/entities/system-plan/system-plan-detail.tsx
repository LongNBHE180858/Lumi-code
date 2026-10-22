import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './system-plan.reducer';

export const SystemPlanDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const systemPlanEntity = useAppSelector(state => state.systemPlan.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="systemPlanDetailsHeading">
          <Translate contentKey="lumiApp.systemPlan.detail.title">SystemPlan</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{systemPlanEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="lumiApp.systemPlan.name">Name</Translate>
            </span>
          </dt>
          <dd>{systemPlanEntity.name}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="lumiApp.systemPlan.price">Price</Translate>
            </span>
          </dt>
          <dd>{systemPlanEntity.price}</dd>
          <dt>
            <span id="durationDays">
              <Translate contentKey="lumiApp.systemPlan.durationDays">Duration Days</Translate>
            </span>
          </dt>
          <dd>{systemPlanEntity.durationDays}</dd>
          <dt>
            <span id="storageQuotaMb">
              <Translate contentKey="lumiApp.systemPlan.storageQuotaMb">Storage Quota Mb</Translate>
            </span>
          </dt>
          <dd>{systemPlanEntity.storageQuotaMb}</dd>
          <dt>
            <span id="aiGenerationLimit">
              <Translate contentKey="lumiApp.systemPlan.aiGenerationLimit">Ai Generation Limit</Translate>
            </span>
          </dt>
          <dd>{systemPlanEntity.aiGenerationLimit}</dd>
          <dt>
            <span id="maxClasses">
              <Translate contentKey="lumiApp.systemPlan.maxClasses">Max Classes</Translate>
            </span>
          </dt>
          <dd>{systemPlanEntity.maxClasses}</dd>
        </dl>
        <Button as={Link as any} to="/system-plan" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/system-plan/${systemPlanEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SystemPlanDetail;
