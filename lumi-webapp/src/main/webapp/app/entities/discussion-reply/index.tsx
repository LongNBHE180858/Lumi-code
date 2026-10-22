import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DiscussionReply from './discussion-reply';
import DiscussionReplyDeleteDialog from './discussion-reply-delete-dialog';
import DiscussionReplyDetail from './discussion-reply-detail';
import DiscussionReplyUpdate from './discussion-reply-update';

const DiscussionReplyRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DiscussionReply />} />
    <Route path="new" element={<DiscussionReplyUpdate />} />
    <Route path=":id">
      <Route index element={<DiscussionReplyDetail />} />
      <Route path="edit" element={<DiscussionReplyUpdate />} />
      <Route path="delete" element={<DiscussionReplyDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DiscussionReplyRoutes;
