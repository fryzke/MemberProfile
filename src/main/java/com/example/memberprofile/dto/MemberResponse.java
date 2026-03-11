package com.example.memberprofile.dto;

import com.example.memberprofile.entity.MemberEntity;

public record MemberResponse(Long id, String name, Integer age, String mbti) {
    public static MemberResponse from(MemberEntity member)  {
        return new MemberResponse(member.getId(), member.getName(), member.getAge(), member.getMbti());
    }
}
