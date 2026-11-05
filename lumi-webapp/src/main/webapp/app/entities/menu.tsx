import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/user-profile">
        <Translate contentKey="global.menu.entities.userProfile" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/teacher-verification">
        <Translate contentKey="global.menu.entities.teacherVerification" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/parent-student-link">
        <Translate contentKey="global.menu.entities.parentStudentLink" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/auth-session">
        <Translate contentKey="global.menu.entities.authSession" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/subject">
        <Translate contentKey="global.menu.entities.subject" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/grade">
        <Translate contentKey="global.menu.entities.grade" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/course">
        <Translate contentKey="global.menu.entities.course" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/chapter">
        <Translate contentKey="global.menu.entities.chapter" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/lesson">
        <Translate contentKey="global.menu.entities.lesson" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/course-material">
        <Translate contentKey="global.menu.entities.courseMaterial" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/course-enrollment">
        <Translate contentKey="global.menu.entities.courseEnrollment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/wishlist">
        <Translate contentKey="global.menu.entities.wishlist" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/course-review">
        <Translate contentKey="global.menu.entities.courseReview" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/study-class">
        <Translate contentKey="global.menu.entities.studyClass" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/class-member">
        <Translate contentKey="global.menu.entities.classMember" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/class-material">
        <Translate contentKey="global.menu.entities.classMaterial" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/system-plan">
        <Translate contentKey="global.menu.entities.systemPlan" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/refund">
        <Translate contentKey="global.menu.entities.refund" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/instructor-wallet">
        <Translate contentKey="global.menu.entities.instructorWallet" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/wallet-transaction">
        <Translate contentKey="global.menu.entities.walletTransaction" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/payout-request">
        <Translate contentKey="global.menu.entities.payoutRequest" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/pdf-chunk">
        <Translate contentKey="global.menu.entities.pdfChunk" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ai-conversation">
        <Translate contentKey="global.menu.entities.aiConversation" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ai-message">
        <Translate contentKey="global.menu.entities.aiMessage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/content-report">
        <Translate contentKey="global.menu.entities.contentReport" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/notification">
        <Translate contentKey="global.menu.entities.notification" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/course-review-request">
        <Translate contentKey="global.menu.entities.courseReviewRequest" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/lesson-progress">
        <Translate contentKey="global.menu.entities.lessonProgress" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/student-knowledge-progress">
        <Translate contentKey="global.menu.entities.studentKnowledgeProgress" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/learning-document">
        <Translate contentKey="global.menu.entities.learningDocument" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ai-generated-content">
        <Translate contentKey="global.menu.entities.aiGeneratedContent" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/question-knowledge-link">
        <Translate contentKey="global.menu.entities.questionKnowledgeLink" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
