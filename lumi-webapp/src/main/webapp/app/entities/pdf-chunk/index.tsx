import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import PdfChunk from './pdf-chunk';
import PdfChunkDeleteDialog from './pdf-chunk-delete-dialog';
import PdfChunkDetail from './pdf-chunk-detail';
import PdfChunkUpdate from './pdf-chunk-update';

const PdfChunkRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<PdfChunk />} />
    <Route path="new" element={<PdfChunkUpdate />} />
    <Route path=":id">
      <Route index element={<PdfChunkDetail />} />
      <Route path="edit" element={<PdfChunkUpdate />} />
      <Route path="delete" element={<PdfChunkDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PdfChunkRoutes;
