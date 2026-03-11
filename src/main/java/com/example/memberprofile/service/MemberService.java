package com.example.memberprofile.service;

import com.example.memberprofile.dto.MemberRequest;
import com.example.memberprofile.dto.MemberResponse;
import com.example.memberprofile.entity.MemberEntity;
import com.example.memberprofile.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse saveMember(MemberRequest request) {
        MemberEntity member = new MemberEntity(request.name(), request.age(), request.mbti());
        MemberEntity saveMember = memberRepository.save(member);

        return MemberResponse.from(saveMember);
    }

    public MemberResponse findMemberById(Long id) {
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀원을 찾을 수 없습니다."));

        return MemberResponse.from((member));
    }
}
