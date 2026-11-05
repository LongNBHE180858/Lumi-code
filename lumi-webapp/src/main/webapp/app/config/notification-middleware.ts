import { translate } from 'react-jhipster';

import { isAxiosError } from 'axios';
import { toast } from 'react-toastify';

import { getMessageFromHeaders } from 'app/shared/jhipster/headers';
import { FieldErrorVM, isProblemWithMessage } from 'app/shared/jhipster/problem-details';
import { isFulfilledAction, isRejectedAction } from 'app/shared/reducers/reducer.utils';

type ToastMessage = {
  message?: string;
  key?: string;
  data?: any;
};

const addErrorAlert = (message: ToastMessage) => {
  toast.error(message.key ? (translate(message.key, message.data) ?? message.message) : message.message);
};

// RFC7807 problem type used by Spring/JHipster for bean-validation failures (@Valid on the request
// body), e.g. creating a Wishlist without a course. This is distinct from "problem-with-message"
// (used by BadRequestAlertException for duplicate/coursenotfound/idnotfound).
// Confirmed at runtime in C2.3: the validation response carries this `type` and a `fieldErrors` array.
const CONSTRAINT_VIOLATION_TYPE = 'https://www.jhipster.tech/problem/constraint-violation';

const normalizeFieldError = (fieldError: FieldErrorVM): FieldErrorVM => {
  if (['Min', 'Max', 'DecimalMin', 'DecimalMax'].includes(fieldError.message)) {
    return { ...fieldError, message: 'Size' };
  }
  if (fieldError.message === 'must not be null') {
    return { ...fieldError, message: 'NotNull' };
  }
  return fieldError;
};

const getFieldErrorsToasts = (fieldErrors: FieldErrorVM[]): ToastMessage[] =>
  fieldErrors.map(fieldError => {
    const normalizedFieldError = normalizeFieldError(fieldError);
    const field = typeof normalizedFieldError.field === 'string' ? normalizedFieldError.field : '';
    const objectName = typeof normalizedFieldError.objectName === 'string' ? normalizedFieldError.objectName : 'unknown';
    // convert 'something[14].other[4].id' to 'something[].other[].id' so translations can be written to it
    const convertedField = field.replace(/\[\d*\]/g, '[]');
    const fieldName = translate(`lumiApp.${objectName}.${convertedField}`);
    return {
      message: `Error on field "${fieldName}"`,
      key: `error.${normalizedFieldError.message}`,
      data: { fieldName },
    };
  });

// eslint-disable-next-line complexity
export default () => next => action => {
  const { error, payload } = action;

  // Course builder reports contextual notifications in its components.
  if (action.type?.startsWith('courseBuilder/')) return next(action);

  /**
   *
   * The notification middleware serves to add success and error notifications
   */
  if (isFulfilledAction(action) && payload?.headers) {
    const { alertMessage, alertKey, param } = getMessageFromHeaders(payload.headers);
    if (alertKey) {
      toast.success(translate(alertKey, { param }));
    } else if (alertMessage) {
      toast.success(alertMessage);
    }
  }

  if (isRejectedAction(action) && isAxiosError(error)) {
    if (error.response) {
      const { response } = error;
      if (response.status === 401) {
        // Ignore, page will be redirected to login.
      } else if (
        error.config?.url?.endsWith('api/account') ||
        error.config?.url?.endsWith('api/authenticate') ||
        error.config?.url?.endsWith('api/auth/login')
      ) {
        // Ignore, authentication status check and authentication are treated differently.
      } else if (response.status === 0) {
        // connection refused, server not reachable
        addErrorAlert({
          message: 'Server not reachable',
          key: 'error.server.not.reachable',
        });
      } else if (response.status === 404) {
        addErrorAlert({
          message: 'Not found',
          key: 'error.url.not.found',
        });
      } else {
        const { data } = response;
        const problem = isProblemWithMessage(data) ? data : null;
        // Bean-validation failures (@Valid on the request body) are returned with the
        // "constraint-violation" problem type and a `fieldErrors` array, e.g. creating a Wishlist
        // without a course. Handle them explicitly (instead of the generic detail-string fallback)
        // so the user sees a friendly, localized message. Detection relies on the problem `type`
        // and the structured `fieldErrors` entries — not on guessing from the `detail` string.
        const validationFieldErrors =
          data?.type === CONSTRAINT_VIOLATION_TYPE ? (data.fieldErrors as FieldErrorVM[] | undefined) : undefined;
        if (validationFieldErrors && validationFieldErrors.length > 0) {
          const fieldNames = validationFieldErrors.map(fieldError => fieldError.field);
          // A missing/invalid `course` is the actionable case for the Wishlist form: the client
          // must pick a course before saving. Only map this specific field, never blanket-map 400s.
          if (fieldNames.includes('course')) {
            addErrorAlert({ key: 'error.wishlist.course.required' });
          }
          // Any other invalid field is rendered through the generic field-error path so existing
          // behavior and translations are preserved. Spring reports @NotNull failures with the human
          // message "must not be null", which has no translation; normalize it to the "NotNull"
          // constraint key (which is translated) so the message stays friendly and localized.
          const otherFieldErrors = validationFieldErrors.filter(fieldError => fieldError.field !== 'course').map(normalizeFieldError);
          getFieldErrorsToasts(otherFieldErrors).forEach(message => addErrorAlert(message));
        } else if (problem?.fieldErrors) {
          getFieldErrorsToasts(problem.fieldErrors).forEach(message => addErrorAlert(message));
        } else {
          const { errorMessage, errorKey, param } = getMessageFromHeaders(response.headers ?? {});
          if (errorKey) {
            const entityName = translate(`global.menu.entities.${param}`);
            addErrorAlert({ key: errorKey, data: { entityName } });
          } else if (errorMessage) {
            addErrorAlert({ message: errorMessage });
          } else if (problem?.message) {
            addErrorAlert({ message: problem.detail, key: problem.message });
          } else if (data?.type === CONSTRAINT_VIOLATION_TYPE && typeof data?.message === 'string' && data.message !== '') {
            // Validation error without a usable fieldErrors list: fall back to the generic
            // validation message key rather than exposing the raw technical `detail` string.
            addErrorAlert({ key: data.message });
          } else if (typeof data === 'string' && data !== '') {
            addErrorAlert({ message: data });
          } else {
            toast.error(data?.detail ?? data?.message ?? data?.error ?? data?.title ?? 'Unknown error!');
          }
        }
      }
    } else if (error.config?.url?.endsWith('api/account') && error.config.method === 'get') {
      // eslint-disable-next-line no-console
      console.log('Authentication Error: Trying to access url api/account with GET.');
    } else {
      addErrorAlert({ message: error.message ?? 'Unknown error!' });
    }
  } else if (error) {
    addErrorAlert({ message: error.message ?? 'Unknown error!' });
  }

  return next(action);
};
