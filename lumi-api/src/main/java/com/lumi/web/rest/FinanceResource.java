package com.lumi.web.rest;

import com.lumi.service.finance.FinanceDTOs.*;
import com.lumi.service.finance.FinanceService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceResource {

    private final FinanceService service;

    @GetMapping("/wallet")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<WalletView> wallet() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.wallet());
    }

    @GetMapping("/wallet/transactions")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<TransactionView>> transactions() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.transactions());
    }

    @GetMapping("/payouts/my")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<PayoutView>> myPayouts() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.myPayouts());
    }

    @PostMapping("/payouts")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public PayoutView requestPayout(@Valid @RequestBody PayoutCreate request) {
        return service.requestPayout(request);
    }

    @GetMapping("/refunds/my")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<List<RefundView>> myRefunds() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.myRefunds());
    }

    @PostMapping("/refunds")
    @PreAuthorize("hasRole('PARENT')")
    public RefundView requestRefund(@Valid @RequestBody RefundCreate request) {
        return service.requestRefund(request);
    }

    @GetMapping("/admin/payouts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PayoutView>> payouts() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.allPayouts());
    }

    @PostMapping("/admin/payouts/{id}/decision")
    @PreAuthorize("hasRole('ADMIN')")
    public PayoutView decidePayout(@PathVariable Long id, @Valid @RequestBody Decision request) {
        return service.decidePayout(id, request);
    }

    @PostMapping("/admin/payouts/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public PayoutView completePayout(@PathVariable Long id, @Valid @RequestBody Transfer request) {
        return service.completePayout(id, request);
    }

    @GetMapping("/admin/refunds")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RefundView>> refunds() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.allRefunds());
    }

    @PostMapping("/admin/refunds/{id}/decision")
    @PreAuthorize("hasRole('ADMIN')")
    public RefundView decideRefund(@PathVariable Long id, @Valid @RequestBody Decision request) {
        return service.decideRefund(id, request);
    }

    @PostMapping("/admin/refunds/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public RefundView completeRefund(@PathVariable Long id, @Valid @RequestBody Transfer request) {
        return service.completeRefund(id, request);
    }
}
