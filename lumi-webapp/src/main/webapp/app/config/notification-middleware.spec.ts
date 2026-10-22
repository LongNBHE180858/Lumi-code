import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import { TranslatorContext } from 'react-jhipster';

import { configureStore } from '@reduxjs/toolkit';
import { toast } from 'react-toastify';

import {
  INVALID_PASSWORD_TYPE,
  MESSAGE_ALERT_HEADER_NAME,
  MESSAGE_ERROR_HEADER_NAME,
  MESSAGE_PARAM_HEADER_NAME,
} from 'app/shared/jhipster/constants';
import { ProblemWithMessageType } from 'app/shared/jhipster/problem-details';

import notificationMiddleware from './notification-middleware';

vi.mock('react-toastify', () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}));

describe('Notification Middleware', () => {
  let store: ReturnType<typeof makeStore>;

  const SUCCESS_TYPE = 'SUCCESS/fulfilled';
  const ERROR_TYPE = 'ERROR/rejected';

  // Default action for use in local tests
  const DEFAULT = {
    type: SUCCESS_TYPE,
    payload: 'foo',
  };
  const HEADER_SUCCESS = {
    type: SUCCESS_TYPE,
    payload: {
      status: 201,
      statusText: 'Created',
      headers: { [MESSAGE_ALERT_HEADER_NAME]: 'foo.created', [MESSAGE_PARAM_HEADER_NAME]: 'foo' },
    },
  };

  const DEFAULT_ERROR = {
    type: ERROR_TYPE,
    error: { message: 'foo' },
  };
  const VALIDATION_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: {
          type: ProblemWithMessageType,
          title: 'Method argument not valid',
          status: 400,
          path: '/api/foos',
          message: 'error.validation',
          fieldErrors: [{ objectName: 'foos', field: 'minField', message: 'Min' }],
        },
        status: 400,
        statusText: 'Bad Request',
        headers: { expires: '0' },
      },
    },
  };
  const HEADER_ERRORS = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        status: 400,
        statusText: 'Bad Request',
        headers: { [MESSAGE_ERROR_HEADER_NAME]: 'foo.creation', [MESSAGE_PARAM_HEADER_NAME]: 'foo' },
      },
    },
  };
  const NOT_FOUND_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: {
          status: 404,
          message: 'Not found',
        },
        status: 404,
      },
    },
  };
  const NO_SERVER_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        status: 0,
      },
    },
  };
  const GENERIC_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: {
          message: 'Error',
        },
      },
    },
  };
  const LOGIN_REJECTED_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: '',
        config: {
          url: 'api/authenticate',
        },
        status: 401,
      },
    },
  };

  const TITLE_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: {
          title: 'Incorrect password',
          status: 400,
          type: INVALID_PASSWORD_TYPE,
        },
        status: 400,
      },
    },
  };

  const STRING_DATA_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        data: 'Incorrect password string',
        status: 400,
      },
    },
  };

  const UNKNOWN_400_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
      response: {
        status: 400,
      },
    },
  };

  const UNKNOWN_ERROR = {
    type: ERROR_TYPE,
    error: {
      isAxiosError: true,
    },
  };

  const makeStore = () =>
    configureStore({
      reducer: (state = {}) => state,
      middleware: getDefaultMiddleware => getDefaultMiddleware().concat(notificationMiddleware),
    });

  beforeAll(() => {
    TranslatorContext.registerTranslations('vi', {});
  });

  beforeEach(() => {
    store = makeStore();
    vi.clearAllMocks();
    // ignore console errors
    vi.spyOn(globalThis.console, 'error').mockImplementation();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should not trigger a toast message but should return action', () => {
    expect(store.dispatch(DEFAULT).payload).toEqual('foo');
    expect(toast.error).not.toHaveBeenCalled();
    expect(toast.success).not.toHaveBeenCalled();
  });

  it('should trigger a success toast message for header alerts', () => {
    expect(store.dispatch(HEADER_SUCCESS).payload.status).toEqual(201);
    expect(toast.success).toHaveBeenCalledWith(expect.stringContaining('foo.created'));
  });

  it('should trigger an error toast message and return error', () => {
    expect(store.dispatch(DEFAULT_ERROR).error.message).toEqual('foo');
    expect(toast.error).toHaveBeenCalledWith('foo');
  });

  it('should trigger an error toast message and return error for generic message', () => {
    expect(store.dispatch(GENERIC_ERROR).error.response.data.message).toEqual('Error');
    expect(toast.error).toHaveBeenCalledWith('Error');
  });

  it('should trigger an error toast message and return error for 400 response code', () => {
    expect(store.dispatch(VALIDATION_ERROR).error.response.data.message).toEqual('error.validation');
    expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('error.Size'));
  });

  it('should trigger an error toast message and return error for 404 response code', () => {
    expect(store.dispatch(NOT_FOUND_ERROR).error.response.data.message).toEqual('Not found');
    expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('error.url.not.found'));
  });

  it('should trigger an error toast message and return error for 0 response code', () => {
    expect(store.dispatch(NO_SERVER_ERROR).error.response.status).toEqual(0);
    expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('error.server.not.reachable'));
  });

  it('should trigger an error toast message and return error for headers containing errors', () => {
    expect(store.dispatch(HEADER_ERRORS).error.response.status).toEqual(400);
    expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('foo.creation'));
  });

  it('should not trigger an error toast message and return error for 401 response code', () => {
    expect(store.dispatch(LOGIN_REJECTED_ERROR).error.response.status).toEqual(401);
    expect(toast.error).not.toHaveBeenCalled();
    expect(toast.success).not.toHaveBeenCalled();
  });

  it('should trigger an error toast message and return error for 400 response code', () => {
    expect(store.dispatch(TITLE_ERROR).error.response.status).toEqual(400);
    expect(toast.error).toHaveBeenCalledWith('Incorrect password');
  });

  it('should trigger an error toast message and return error for string in data', () => {
    expect(store.dispatch(STRING_DATA_ERROR).error.response.status).toEqual(400);
    expect(toast.error).toHaveBeenCalledWith('Incorrect password string');
  });

  it('should trigger an error toast message and return error for unknown 400 error', () => {
    expect(store.dispatch(UNKNOWN_400_ERROR).error.response.status).toEqual(400);
    expect(toast.error).toHaveBeenCalledWith('Unknown error!');
  });

  it('should trigger an error toast message and return error for unknown error', () => {
    expect(store.dispatch(UNKNOWN_ERROR).error.isAxiosError).toEqual(true);
    expect(toast.error).toHaveBeenCalledWith('Unknown error!');
  });

  // Bean-validation failures (@Valid on the request body) are returned by the backend with the
  // RFC7807 "constraint-violation" problem type and a structured `fieldErrors` array
  // (verified at runtime in C2.3). These tests cover the friendly, localized handling.
  describe('bean-validation errors (constraint-violation type)', () => {
    const CONSTRAINT_VIOLATION = 'https://www.jhipster.tech/problem/constraint-violation';

    // Mirrors the real response body captured in C2.3 when creating a Wishlist without a course.
    const MISSING_COURSE_ERROR = {
      type: ERROR_TYPE,
      error: {
        isAxiosError: true,
        response: {
          status: 400,
          statusText: 'Bad Request',
          headers: {},
          data: {
            detail: 'Validation failed for argument [0] ... with 2 errors: ...',
            instance: '/api/wishlists',
            status: 400,
            title: 'Method argument not valid',
            type: CONSTRAINT_VIOLATION,
            message: 'error.validation',
            path: '/api/wishlists',
            fieldErrors: [
              { field: 'course', message: 'must not be null', objectName: 'wishlist' },
              { field: 'student', message: 'must not be null', objectName: 'wishlist' },
            ],
          },
        },
      },
    };

    const DUPLICATE_ERROR = {
      type: ERROR_TYPE,
      error: {
        isAxiosError: true,
        response: {
          status: 400,
          statusText: 'Bad Request',
          headers: {},
          data: {
            type: ProblemWithMessageType,
            title: 'A wishlist for this course already exists',
            status: 400,
            message: 'error.duplicate',
            path: '/api/wishlists',
          },
        },
      },
    };

    const COURSE_NOT_FOUND_ERROR = {
      type: ERROR_TYPE,
      error: {
        isAxiosError: true,
        response: {
          status: 400,
          statusText: 'Bad Request',
          headers: {},
          data: {
            type: ProblemWithMessageType,
            title: 'Course not found',
            status: 400,
            message: 'error.coursenotfound',
            path: '/api/wishlists',
          },
        },
      },
    };

    // A validation failure on a different entity/field must NOT be reported as a missing course.
    const OTHER_ENTITY_VALIDATION_ERROR = {
      type: ERROR_TYPE,
      error: {
        isAxiosError: true,
        response: {
          status: 400,
          statusText: 'Bad Request',
          headers: {},
          data: {
            detail: 'Validation failed ...',
            status: 400,
            title: 'Method argument not valid',
            type: CONSTRAINT_VIOLATION,
            message: 'error.validation',
            path: '/api/courses',
            fieldErrors: [{ field: 'title', message: 'must not be null', objectName: 'course' }],
          },
        },
      },
    };

    beforeAll(() => {
      TranslatorContext.registerTranslations('vi', {});
    });

    // A. Missing `course` while creating a Wishlist -> routes to the friendly, localized message
    //    key (error.wishlist.course.required => "Please select a course." / "Vui lòng chọn khóa học.").
    //    Note: vitest does not load the full i18n bundle, so `translate` renders the missing-key
    //    placeholder; we assert on the resolved KEY, which is what proves correct routing.
    it('shows a friendly message when a Wishlist is created without a course', () => {
      store.dispatch(MISSING_COURSE_ERROR);
      expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('error.wishlist.course.required'));
      // The raw technical detail string must never be surfaced to the user.
      expect(toast.error).not.toHaveBeenCalledWith(expect.stringContaining('Validation failed for argument'));
      // The student field (also null in this payload) is rendered via the generic field-error path
      // with the normalized NotNull key, not mapped to the course message.
      expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('error.NotNull'));
    });

    // B. `duplicate` (BadRequestAlertException) still routes to its existing translation key.
    it('still shows the duplicate translation', () => {
      store.dispatch(DUPLICATE_ERROR);
      expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('error.duplicate'));
      expect(toast.error).not.toHaveBeenCalledWith(expect.stringContaining('error.wishlist.course.required'));
    });

    // C. `coursenotfound` still routes to its existing translation key.
    it('still shows the coursenotfound translation', () => {
      store.dispatch(COURSE_NOT_FOUND_ERROR);
      expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('error.coursenotfound'));
      expect(toast.error).not.toHaveBeenCalledWith(expect.stringContaining('error.wishlist.course.required'));
    });

    // D. A validation error unrelated to the Wishlist `course` field is not turned into the
    //    "select a course" message; it goes through the generic field-error handling instead.
    it('does not map unrelated validation errors to the course message', () => {
      store.dispatch(OTHER_ENTITY_VALIDATION_ERROR);
      expect(toast.error).not.toHaveBeenCalledWith(expect.stringContaining('error.wishlist.course.required'));
      expect(toast.error).toHaveBeenCalled();
    });

    // E. An error response with no known structure keeps the existing fallback.
    it('still falls back for an unknown 400 error', () => {
      store.dispatch(UNKNOWN_400_ERROR);
      expect(toast.error).toHaveBeenCalledWith('Unknown error!');
    });
  });
});
