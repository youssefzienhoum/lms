package com.lms.lms.Services;

import com.lms.lms.DTOS.CertificateResponse;
import com.lms.lms.Entity.User;
import com.lms.lms.Repo.CertificateRepository;
import com.lms.lms.Repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;

    // View certificates
    public List<CertificateResponse> getMyCertificates() {
        User student = getLoggedInStudent();

        return certificateRepository.findByStudent(student)
                .stream()
                .map(CertificateResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private User getLoggedInStudent() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
}