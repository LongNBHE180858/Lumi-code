import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FlashcardReview from './flashcard-review';
import FlashcardReviewDeleteDialog from './flashcard-review-delete-dialog';
import FlashcardReviewDetail from './flashcard-review-detail';
import FlashcardReviewUpdate from './flashcard-review-update';

const FlashcardReviewRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FlashcardReview />} />
    <Route path="new" element={<FlashcardReviewUpdate />} />
    <Route path=":id">
      <Route index element={<FlashcardReviewDetail />} />
      <Route path="edit" element={<FlashcardReviewUpdate />} />
      <Route path="delete" element={<FlashcardReviewDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FlashcardReviewRoutes;
