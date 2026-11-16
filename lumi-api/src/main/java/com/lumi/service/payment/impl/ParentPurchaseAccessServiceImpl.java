package com.lumi.service.payment.impl;

import com.lumi.domain.ParentStudentLink;
import com.lumi.domain.User;
import com.lumi.repository.ParentStudentLinkRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.security.SecurityUtils;
import com.lumi.service.dto.ParentPurchaseAccessDTO;
import com.lumi.service.payment.ParentPurchaseAccessService;

import java.util.LinkedHashMap;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
@PreAuthorize("hasRole('PARENT')")
public class ParentPurchaseAccessServiceImpl implements ParentPurchaseAccessService {

    UserRepository userRepository;
    ParentStudentLinkRepository linkRepository;

    //validate cho hàm tạo checkout ở bên
    @Override
    public ParentPurchaseAccessDTO validateForCheckout(Long studentId) {
        validateStudentId(studentId);

        User parent = getCurrentActiveParent();
        validateParentIsNotStudent(parent.getId(), studentId);
        validateLinkedRelationship(parent.getId(), studentId);

        User student = getActiveStudent(studentId);

        return ParentPurchaseAccessDTO.builder().parent(parent).student(student).build();
    }

    // Phải dùng transaction của tạo đơn để giữ khóa đến khi đơn được lưu xong.
    // bắt buộc phương thức phải được chạy bên trong một transaction đã có sẵn, ko thì sẽ báo lỗi
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public ParentPurchaseAccessDTO validateAndLockForOrder(Long studentId) {
        validateStudentId(studentId);

        User parent = getCurrentActiveParent();
        validateParentIsNotStudent(parent.getId(), studentId);
        validateLinkedRelationship(parent.getId(), studentId);

        lockParentAndStudentAccounts(parent.getId(), studentId);
        if (!parent.isActivated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản phụ huynh chưa được kích hoạt.");
        }

        // Liên kết có thể vừa bị thu hồi trong lúc chờ khóa: phải kiểm tra lại.
        validateAndLockLinkedRelationship(parent.getId(), studentId);

        User student = getActiveStudent(studentId);

        return ParentPurchaseAccessDTO.builder().parent(parent).student(student).build();
    }


    //lấy danh sách học sinh đã liên kết với phụ huynh để mua khóa học
    @Override
    public List<ParentStudentLink> getPurchasableChildren() {
        User parent = getCurrentActiveParent();
        LinkedHashMap<Long, ParentStudentLink> linksByStudentId = new LinkedHashMap<>();
        for (ParentStudentLink link : linkRepository.findByParentIdWithStudent(parent.getId())) {
            if (!isLinked(link)) {
                continue;
            }

            User student = link.getStudent();
            if (student == null || parent.getId().equals(student.getId())) {
                continue;
            }
            if (!isEligibleStudent(student)) {
                continue;
            }

            // Một học sinh chỉ xuất hiện một lần dù có nhiều bản ghi liên kết.
            linksByStudentId.putIfAbsent(student.getId(), link);
        }
        return List.copyOf(linksByStudentId.values());
    }

    private void validateStudentId(Long studentId) {
        if (studentId == null || studentId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã học sinh không hợp lệ.");
        }
    }

    private void validateParentIsNotStudent(Long parentId, Long studentId) {
        if (parentId.equals(studentId)) {
            throw forbidden();
        }
    }

    private void validateLinkedRelationship(Long parentId, Long studentId) {
        if (!linkRepository.existsByParentIdAndStudentIdAndStatusAndRevokedAtIsNull(parentId, studentId, "LINKED")) {
            throw forbidden();
        }
    }

    private void lockParentAndStudentAccounts(Long parentId, Long studentId) {
        // Luôn khóa ID nhỏ trước để các yêu cầu đồng thời không khóa chéo nhau.
        Long smallerId = Math.min(parentId, studentId);
        Long largerId = Math.max(parentId, studentId);

        userRepository.findForPurchaseAccess(smallerId).orElseThrow(this::invalidStudent);
        userRepository.findForPurchaseAccess(largerId).orElseThrow(this::invalidStudent);
    }

    private void validateAndLockLinkedRelationship(Long parentId, Long studentId) {
        // Khóa cả bản ghi đã thu hồi để đọc được trạng thái mới nhất sau khi chờ.
        List<ParentStudentLink> links = linkRepository.findForPurchaseAccess(parentId, studentId);
        for (ParentStudentLink link : links) {
            if (isLinked(link)) {
                return;
            }
        }
        throw forbidden();
    }


    private User getActiveStudent(Long studentId) {
        return userRepository.findOneWithAuthoritiesById(studentId).filter(ParentPurchaseAccessServiceImpl::isEligibleStudent).orElseThrow(this::invalidStudent);
    }

    @Override
    public User getCurrentActiveParent() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập."));
        return userRepository.findOneByLogin(login).filter(User::isActivated).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản không tồn tại hoặc chưa được kích hoạt."));
    }

    //check trạng thái LINKED
    private static boolean isLinked(ParentStudentLink link) {
        return "LINKED".equals(link.getStatus()) && link.getRevokedAt() == null;
    }

    //check học sinh phải đủ cả điều kiện là trạng thái tài khoản active và role phải là student, chứ không phải chỉ check mỗi trạng thái LINKED
    private static boolean isEligibleStudent(User student) {
        return student.isActivated() && student.getAuthorities().stream().anyMatch(authority -> AuthoritiesConstants.STUDENT.equals(authority.getName()));
    }


    private ResponseStatusException forbidden() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có liên kết hợp lệ với học sinh này.");
    }

    private ResponseStatusException invalidStudent() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Tài khoản học sinh không hợp lệ hoặc chưa được kích hoạt.");
    }
}
