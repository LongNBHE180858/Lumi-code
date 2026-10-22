import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getExamAttempts } from 'app/entities/exam-attempt/exam-attempt.reducer';
import { getEntities as getQuestions } from 'app/entities/question/question.reducer';
import { getEntities as getQuestionOptions } from 'app/entities/question-option/question-option.reducer';

import { createEntity, getEntity, updateEntity } from './exam-answer.reducer';

export const ExamAnswerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const examAttempts = useAppSelector(state => state.examAttempt.entities);
  const questions = useAppSelector(state => state.question.entities);
  const questionOptions = useAppSelector(state => state.questionOption.entities);
  const examAnswerEntity = useAppSelector(state => state.examAnswer.entity);
  const loading = useAppSelector(state => state.examAnswer.loading);
  const updating = useAppSelector(state => state.examAnswer.updating);
  const updateSuccess = useAppSelector(state => state.examAnswer.updateSuccess);

  const handleClose = () => {
    navigate('/exam-answer');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getExamAttempts({}));
    dispatch(getQuestions({}));
    dispatch(getQuestionOptions({}));
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
    if (values.score !== undefined && typeof values.score !== 'number') {
      values.score = Number(values.score);
    }

    const entity = {
      ...examAnswerEntity,
      ...values,
      attempt: examAttempts.find(it => it.id.toString() === values.attempt?.toString()),
      question: questions.find(it => it.id.toString() === values.question?.toString()),
      option: questionOptions.find(it => it.id.toString() === values.option?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...examAnswerEntity,
          attempt: examAnswerEntity?.attempt?.id,
          question: examAnswerEntity?.question?.id,
          option: examAnswerEntity?.option?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="lumiApp.examAnswer.home.createOrEditLabel" data-cy="ExamAnswerCreateUpdateHeading">
            <Translate contentKey="lumiApp.examAnswer.home.createOrEditLabel">Create or edit a ExamAnswer</Translate>
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
                  id="exam-answer-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('lumiApp.examAnswer.textAnswer')}
                id="exam-answer-textAnswer"
                name="textAnswer"
                data-cy="textAnswer"
                type="text"
                validate={{
                  maxLength: { value: 5000, message: translate('entity.validation.maxlength', { max: 5000 }) },
                }}
              />
              <ValidatedField
                label={translate('lumiApp.examAnswer.isCorrect')}
                id="exam-answer-isCorrect"
                name="isCorrect"
                data-cy="isCorrect"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('lumiApp.examAnswer.score')}
                id="exam-answer-score"
                name="score"
                data-cy="score"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="exam-answer-attempt"
                name="attempt"
                data-cy="attempt"
                label={translate('lumiApp.examAnswer.attempt')}
                type="select"
                required
              >
                <option value="" key="0" />
                {examAttempts
                  ? examAttempts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="exam-answer-question"
                name="question"
                data-cy="question"
                label={translate('lumiApp.examAnswer.question')}
                type="select"
                required
              >
                <option value="" key="0" />
                {questions
                  ? questions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="exam-answer-option"
                name="option"
                data-cy="option"
                label={translate('lumiApp.examAnswer.option')}
                type="select"
              >
                <option value="" key="0" />
                {questionOptions
                  ? questionOptions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/exam-answer" replace variant="info">
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

export default ExamAnswerUpdate;
