import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './content-report.reducer';

export const ContentReportDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const contentReportEntity = useAppSelector(state => state.contentReport.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="contentReportDetailsHeading">
          <Translate contentKey="lumiApp.contentReport.detail.title">ContentReport</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{contentReportEntity.id}</dd>
          <dt>
            <span id="targetType">
              <Translate contentKey="lumiApp.contentReport.targetType">Target Type</Translate>
            </span>
          </dt>
          <dd>{contentReportEntity.targetType}</dd>
          <dt>
            <span id="targetId">
              <Translate contentKey="lumiApp.contentReport.targetId">Target Id</Translate>
            </span>
          </dt>
          <dd>{contentReportEntity.targetId}</dd>
          <dt>
            <span id="reason">
              <Translate contentKey="lumiApp.contentReport.reason">Reason</Translate>
            </span>
          </dt>
          <dd>{contentReportEntity.reason}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="lumiApp.contentReport.status">Status</Translate>
            </span>
          </dt>
          <dd>{contentReportEntity.status}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.contentReport.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {contentReportEntity.createdAt ? (
              <TextFormat value={contentReportEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="lumiApp.contentReport.reporter">Reporter</Translate>
          </dt>
          <dd>{contentReportEntity.reporter ? contentReportEntity.reporter.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/content-report" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/content-report/${contentReportEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ContentReportDetail;
