import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FlashcardDeck from './flashcard-deck';
import FlashcardDeckDeleteDialog from './flashcard-deck-delete-dialog';
import FlashcardDeckDetail from './flashcard-deck-detail';
import FlashcardDeckUpdate from './flashcard-deck-update';

const FlashcardDeckRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FlashcardDeck />} />
    <Route path="new" element={<FlashcardDeckUpdate />} />
    <Route path=":id">
      <Route index element={<FlashcardDeckDetail />} />
      <Route path="edit" element={<FlashcardDeckUpdate />} />
      <Route path="delete" element={<FlashcardDeckDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FlashcardDeckRoutes;
