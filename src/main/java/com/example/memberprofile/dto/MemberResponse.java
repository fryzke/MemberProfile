package com.example.memberprofile.dto;

import com.example.memberprofile.entity.Members;

public record MemberResponse(Long id, String name, Integer age, String mbti) {
    public static MemberResponse from(Members members)  {
        return new MemberResponse(members.getId(), members.getName(), members.getAge(), members.getMbti());
    }
}
