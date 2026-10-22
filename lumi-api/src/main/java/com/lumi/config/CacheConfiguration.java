package com.lumi.config;

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Caffeine caffeine = jHipsterProperties.getCache().getCaffeine();

        CaffeineConfiguration<Object, Object> caffeineConfiguration = new CaffeineConfiguration<>();
        caffeineConfiguration.setMaximumSize(OptionalLong.of(caffeine.getMaxEntries()));
        caffeineConfiguration.setExpireAfterWrite(OptionalLong.of(TimeUnit.SECONDS.toNanos(caffeine.getTimeToLiveSeconds())));
        caffeineConfiguration.setStatisticsEnabled(true);
        jcacheConfiguration = caffeineConfiguration;
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.lumi.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.lumi.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.lumi.domain.Authority.class.getName());
            createCache(cm, com.lumi.domain.UserProfile.class.getName());
            createCache(cm, com.lumi.domain.TeacherVerification.class.getName());
            createCache(cm, com.lumi.domain.ParentStudentLink.class.getName());
            createCache(cm, com.lumi.domain.AuthSession.class.getName());
            createCache(cm, com.lumi.domain.Category.class.getName());
            createCache(cm, com.lumi.domain.Subject.class.getName());
            createCache(cm, com.lumi.domain.Grade.class.getName());
            createCache(cm, com.lumi.domain.Course.class.getName());
            createCache(cm, com.lumi.domain.Chapter.class.getName());
            createCache(cm, com.lumi.domain.Lesson.class.getName());
            createCache(cm, com.lumi.domain.CourseMaterial.class.getName());
            createCache(cm, com.lumi.domain.CourseEnrollment.class.getName());
            createCache(cm, com.lumi.domain.Wishlist.class.getName());
            createCache(cm, com.lumi.domain.CourseReview.class.getName());
            createCache(cm, com.lumi.domain.StudyClass.class.getName());
            createCache(cm, com.lumi.domain.ClassMember.class.getName());
            createCache(cm, com.lumi.domain.ClassMaterial.class.getName());
            createCache(cm, com.lumi.domain.SystemPlan.class.getName());
            createCache(cm, com.lumi.domain.TeacherQuota.class.getName());
            createCache(cm, com.lumi.domain.Coupon.class.getName());
            createCache(cm, com.lumi.domain.CourseOrder.class.getName());
            createCache(cm, com.lumi.domain.Payment.class.getName());
            createCache(cm, com.lumi.domain.Refund.class.getName());
            createCache(cm, com.lumi.domain.InstructorWallet.class.getName());
            createCache(cm, com.lumi.domain.WalletTransaction.class.getName());
            createCache(cm, com.lumi.domain.PayoutRequest.class.getName());
            createCache(cm, com.lumi.domain.QuestionBank.class.getName());
            createCache(cm, com.lumi.domain.Question.class.getName());
            createCache(cm, com.lumi.domain.QuestionOption.class.getName());
            createCache(cm, com.lumi.domain.Exam.class.getName());
            createCache(cm, com.lumi.domain.ExamQuestion.class.getName());
            createCache(cm, com.lumi.domain.Assignment.class.getName());
            createCache(cm, com.lumi.domain.AssignmentSubmission.class.getName());
            createCache(cm, com.lumi.domain.ExamAttempt.class.getName());
            createCache(cm, com.lumi.domain.ExamAnswer.class.getName());
            createCache(cm, com.lumi.domain.ProctorSession.class.getName());
            createCache(cm, com.lumi.domain.ProctorEvent.class.getName());
            createCache(cm, com.lumi.domain.StudentNote.class.getName());
            createCache(cm, com.lumi.domain.StudentStreak.class.getName());
            createCache(cm, com.lumi.domain.FlashcardDeck.class.getName());
            createCache(cm, com.lumi.domain.Flashcard.class.getName());
            createCache(cm, com.lumi.domain.FlashcardReview.class.getName());
            createCache(cm, com.lumi.domain.KnowledgeNode.class.getName());
            createCache(cm, com.lumi.domain.KnowledgeEdge.class.getName());
            createCache(cm, com.lumi.domain.PdfChunk.class.getName());
            createCache(cm, com.lumi.domain.AiConversation.class.getName());
            createCache(cm, com.lumi.domain.AiMessage.class.getName());
            createCache(cm, com.lumi.domain.Discussion.class.getName());
            createCache(cm, com.lumi.domain.DiscussionReply.class.getName());
            createCache(cm, com.lumi.domain.SupportTicket.class.getName());
            createCache(cm, com.lumi.domain.TicketMessage.class.getName());
            createCache(cm, com.lumi.domain.ContentReport.class.getName());
            createCache(cm, com.lumi.domain.Notification.class.getName());
            createCache(cm, com.lumi.domain.AuditLog.class.getName());
            createCache(cm, com.lumi.domain.AssignmentStudent.class.getName());
            // jhipster-needle-caffeine-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
