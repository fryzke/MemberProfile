package com.example.memberprofile.repository;

import com.example.memberprofile.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Members, Long> {
}
