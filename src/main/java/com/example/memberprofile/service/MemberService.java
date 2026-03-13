package com.example.memberprofile.service;

import com.example.memberprofile.dto.MemberRequest;
import com.example.memberprofile.dto.MemberResponse;
import com.example.memberprofile.entity.Members;
import com.example.memberprofile.repository.MemberRepository;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {


    private final MemberRepository memberRepository;
    private final S3Template s3Template;
    private final S3Presigner s3Presigner;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Transactional
    public MemberResponse saveMember(MemberRequest request) {
        Members members = new Members(request.name(), request.age(), request.mbti());
        Members saveMembers = memberRepository.save(members);

        return MemberResponse.from(saveMembers);
    }

    public MemberResponse findMemberById(Long id) {
        Members members = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀원을 찾을 수 없습니다."));

        return MemberResponse.from((members));
    }

    @Transactional
    public String uploadProfileImage(Long id, MultipartFile file) {
        Members members = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀원을 찾을 수 없습니다."));

        // 파일명 생성 (profiles/UUID-원본이름)
        String s3Key = "profiles/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            // 1. S3 업로드
            s3Template.upload(bucketName, s3Key, file.getInputStream());

            // 2. DB 업데이트 (이미지 경로/키 저장)
            members.updateProfileImage(s3Key);

            return s3Key;
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * GET /api/members/{id}/profile-image
     * 7일 유효기간의 Presigned URL 생성
     */
    public String getPresignedProfileImageUrl(Long id) {
        Members members = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀원을 찾을 수 없습니다."));

        String s3Key = members.getProfileImageUrl(); // 엔티티에 저장된 이미지 키

        if (s3Key == null || s3Key.isEmpty()) {
            throw new IllegalArgumentException("등록된 프로필 이미지가 없습니다.");
        }

        // Presigned URL 요청 생성
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(7)) // 유효기간 7일 설정
                .getObjectRequest(getObjectRequest)
                .build();

        // Presigned URL 반환
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
