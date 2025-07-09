package com.itbank.mall.service;

import com.itbank.mall.dto.SignupRequestDto;
import com.itbank.mall.entity.Member;
import com.itbank.mall.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final EmailVerificationService emailVerificationService;
    private final BCryptPasswordEncoder passwordEncoder; 

    public void signup(SignupRequestDto dto) {
        if (memberMapper.findByEmail(dto.getEmail()) != null) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        Member member = new Member();
        member.setEmail(dto.getEmail());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setNickname(dto.getNickname());
        member.setProvider("local");
        member.setProviderId(null);
        member.setVerified(false);

        memberMapper.insertMember(member);

        emailVerificationService.sendVerificationCode(dto.getEmail());
    }

    public void verifyMember(String email) {
        memberMapper.verifyMemberByEmail(email);
    }
}
