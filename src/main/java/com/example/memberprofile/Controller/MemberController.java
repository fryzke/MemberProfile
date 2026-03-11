package com.example.memberprofile.Controller;

import com.example.memberprofile.dto.MemberRequest;
import com.example.memberprofile.dto.MemberResponse;
import com.example.memberprofile.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> create(@RequestBody MemberRequest request) {
        log.info("멤버 등록 요청: {}", request.name());
        MemberResponse response = memberService.saveMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        log.info("멤버 조회 요청 ID: {}", id);
        MemberResponse response = memberService.findMemberById(id);
        return ResponseEntity.ok(response);
    }
}
