package com.lms.lms.Controller;

import com.lms.lms.DTOS.CertificateResponse;
import com.lms.lms.Services.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class CertificateController {

    private final CertificateService certificateService;

    // View certificates
    @GetMapping
    public ResponseEntity<List<CertificateResponse>> getMyCertificates() {
        return ResponseEntity.ok(certificateService.getMyCertificates());
    }
}