import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ContentReport from './content-report';
import ContentReportDeleteDialog from './content-report-delete-dialog';
import ContentReportDetail from './content-report-detail';
import ContentReportUpdate from './content-report-update';

const ContentReportRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ContentReport />} />
    <Route path="new" element={<ContentReportUpdate />} />
    <Route path=":id">
      <Route index element={<ContentReportDetail />} />
      <Route path="edit" element={<ContentReportUpdate />} />
      <Route path="delete" element={<ContentReportDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ContentReportRoutes;
