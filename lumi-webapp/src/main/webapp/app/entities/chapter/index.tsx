import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Chapter from './chapter';
import ChapterDeleteDialog from './chapter-delete-dialog';
import ChapterDetail from './chapter-detail';
import ChapterUpdate from './chapter-update';

const ChapterRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Chapter />} />
    <Route path="new" element={<ChapterUpdate />} />
    <Route path=":id">
      <Route index element={<ChapterDetail />} />
      <Route path="edit" element={<ChapterUpdate />} />
      <Route path="delete" element={<ChapterDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ChapterRoutes;
