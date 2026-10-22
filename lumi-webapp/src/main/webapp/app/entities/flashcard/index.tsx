import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Flashcard from './flashcard';
import FlashcardDeleteDialog from './flashcard-delete-dialog';
import FlashcardDetail from './flashcard-detail';
import FlashcardUpdate from './flashcard-update';

const FlashcardRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Flashcard />} />
    <Route path="new" element={<FlashcardUpdate />} />
    <Route path=":id">
      <Route index element={<FlashcardDetail />} />
      <Route path="edit" element={<FlashcardUpdate />} />
      <Route path="delete" element={<FlashcardDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FlashcardRoutes;
