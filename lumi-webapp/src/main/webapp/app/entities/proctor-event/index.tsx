import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProctorEvent from './proctor-event';
import ProctorEventDeleteDialog from './proctor-event-delete-dialog';
import ProctorEventDetail from './proctor-event-detail';
import ProctorEventUpdate from './proctor-event-update';

const ProctorEventRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProctorEvent />} />
    <Route path="new" element={<ProctorEventUpdate />} />
    <Route path=":id">
      <Route index element={<ProctorEventDetail />} />
      <Route path="edit" element={<ProctorEventUpdate />} />
      <Route path="delete" element={<ProctorEventDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProctorEventRoutes;
