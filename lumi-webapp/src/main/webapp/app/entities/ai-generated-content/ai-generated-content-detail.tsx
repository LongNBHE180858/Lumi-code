import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ai-generated-content.reducer';

export const AiGeneratedContentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id!));
  }, []);

  const aiGeneratedContentEntity = useAppSelector(state => state.aiGeneratedContent.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="aiGeneratedContentDetailsHeading">
          <Translate contentKey="lumiApp.aiGeneratedContent.detail.title">AiGeneratedContent</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.id}</dd>
          <dt>
            <span id="contentType">
              <Translate contentKey="lumiApp.aiGeneratedContent.contentType">Content Type</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.contentType}</dd>
          <dt>
            <span id="requestHash">
              <Translate contentKey="lumiApp.aiGeneratedContent.requestHash">Request Hash</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.requestHash}</dd>
          <dt>
            <span id="generationConfig">
              <Translate contentKey="lumiApp.aiGeneratedContent.generationConfig">Generation Config</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.generationConfig}</dd>
          <dt>
            <span id="sourceVersion">
              <Translate contentKey="lumiApp.aiGeneratedContent.sourceVersion">Source Version</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.sourceVersion}</dd>
          <dt>
            <span id="modelName">
              <Translate contentKey="lumiApp.aiGeneratedContent.modelName">Model Name</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.modelName}</dd>
          <dt>
            <span id="promptVersion">
              <Translate contentKey="lumiApp.aiGeneratedContent.promptVersion">Prompt Version</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.promptVersion}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="lumiApp.aiGeneratedContent.content">Content</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.content}</dd>
          <dt>
            <span id="generationStatus">
              <Translate contentKey="lumiApp.aiGeneratedContent.generationStatus">Generation Status</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.generationStatus}</dd>
          <dt>
            <span id="visibility">
              <Translate contentKey="lumiApp.aiGeneratedContent.visibility">Visibility</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.visibility}</dd>
          <dt>
            <span id="version">
              <Translate contentKey="lumiApp.aiGeneratedContent.version">Version</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.version}</dd>
          <dt>
            <span id="reviewStatus">
              <Translate contentKey="lumiApp.aiGeneratedContent.reviewStatus">Review Status</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.reviewStatus}</dd>
          <dt>
            <span id="reviewedAt">
              <Translate contentKey="lumiApp.aiGeneratedContent.reviewedAt">Reviewed At</Translate>
            </span>
          </dt>
          <dd>
            {aiGeneratedContentEntity.reviewedAt ? (
              <TextFormat value={aiGeneratedContentEntity.reviewedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="reviewerFeedback">
              <Translate contentKey="lumiApp.aiGeneratedContent.reviewerFeedback">Reviewer Feedback</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.reviewerFeedback}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="lumiApp.aiGeneratedContent.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {aiGeneratedContentEntity.createdAt ? (
              <TextFormat value={aiGeneratedContentEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="lumiApp.aiGeneratedContent.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {aiGeneratedContentEntity.updatedAt ? (
              <TextFormat value={aiGeneratedContentEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="sourceQuestionId">
              <Translate contentKey="lumiApp.aiGeneratedContent.sourceQuestionId">Source Question Id</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.sourceQuestionId}</dd>
          <dt>
            <span id="contentHash">
              <Translate contentKey="lumiApp.aiGeneratedContent.contentHash">Content Hash</Translate>
            </span>
          </dt>
          <dd>{aiGeneratedContentEntity.contentHash}</dd>
          <dt>
            <Translate contentKey="lumiApp.aiGeneratedContent.createdBy">Created By</Translate>
          </dt>
          <dd>{aiGeneratedContentEntity.createdBy ? aiGeneratedContentEntity.createdBy.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.aiGeneratedContent.document">Document</Translate>
          </dt>
          <dd>{aiGeneratedContentEntity.document ? aiGeneratedContentEntity.document.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.aiGeneratedContent.lesson">Lesson</Translate>
          </dt>
          <dd>{aiGeneratedContentEntity.lesson ? aiGeneratedContentEntity.lesson.id : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.aiGeneratedContent.reviewedBy">Reviewed By</Translate>
          </dt>
          <dd>{aiGeneratedContentEntity.reviewedBy ? aiGeneratedContentEntity.reviewedBy.login : ''}</dd>
          <dt>
            <Translate contentKey="lumiApp.aiGeneratedContent.course">Course</Translate>
          </dt>
          <dd>{aiGeneratedContentEntity.course ? aiGeneratedContentEntity.course.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/ai-generated-content" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/ai-generated-content/${aiGeneratedContentEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AiGeneratedContentDetail;
