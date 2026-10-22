package com.lumi.repository;

import com.lumi.domain.UserProfile;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserProfile entity.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>, JpaSpecificationExecutor<UserProfile> {
    Optional<UserProfile> findOneByUserId(Long userId);

    @Query(
        value = "select p from UserProfile p join fetch p.user u join u.authorities a " +
            "where a.name = :authority and u.activated = true and p.instructorStatus is not null " +
            "and p.instructorStatus = :status",
        countQuery = "select count(p) from UserProfile p join p.user u join u.authorities a " +
            "where a.name = :authority and u.activated = true and p.instructorStatus is not null " +
            "and p.instructorStatus = :status"
    )
    Page<UserProfile> findInstructorApplications(
        @Param("authority") String authority,
        @Param("status") InstructorApplicationStatus status,
        Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from UserProfile p where p.user.id = :userId")
    Optional<UserProfile> findInstructorApplicationForUpdate(@Param("userId") Long userId);

    default Optional<UserProfile> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UserProfile> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UserProfile> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select userProfile from UserProfile userProfile left join fetch userProfile.user",
        countQuery = "select count(userProfile) from UserProfile userProfile"
    )
    Page<UserProfile> findAllWithToOneRelationships(Pageable pageable);

    @Query("select userProfile from UserProfile userProfile left join fetch userProfile.user")
    List<UserProfile> findAllWithToOneRelationships();

    @Query("select userProfile from UserProfile userProfile left join fetch userProfile.user where userProfile.id =:id")
    Optional<UserProfile> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<UserProfile> findOneByUserLogin(String login);

    Optional<UserProfile> findOneByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<UserProfile> findUserByUserId(Long userId);

    //check sdt đã có ai sử dụng chưa, bỏ qua user có userid truyền xuống
    boolean existsByPhoneNumberAndUserIdNot(String phoneNumber, Long userId);
}
