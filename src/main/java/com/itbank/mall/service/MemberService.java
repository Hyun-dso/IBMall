package com.itbank.mall.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.itbank.mall.dto.GoogleSignupRequestDto;
import com.itbank.mall.dto.SignupRequestDto;
import com.itbank.mall.dto.TempUserDto;
import com.itbank.mall.entity.Member;
import com.itbank.mall.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService; // 🔥 추가

    public Member signin(String email, String rawPassword) {
        Member member = memberMapper.findByEmail(email);
        if (member == null) {
            return null;
        }

        boolean matched = passwordEncoder.matches(rawPassword, member.getPassword());
        return matched ? member : null;
    }
    
    public void signup(SignupRequestDto dto) {
        if (memberMapper.findByEmail(dto.getEmail()) != null) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }
        
        // 닉네임 중복 체크
        if (memberMapper.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        Member member = new Member();
        member.setEmail(dto.getEmail());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setNickname(dto.getNickname());
        member.setProvider("local");
        member.setProviderId(null);
        member.setVerified(false);

        memberMapper.insertMember(member);
    }
    
    public Member findByEmail(String email) {
        return memberMapper.findByEmail(email);
    }
    
    public boolean existsByEmail(String email) {
        return memberMapper.countByEmail(email) > 0;
    }
    
    public Member signupByGoogle(GoogleSignupRequestDto dto, TempUserDto tempUser) {
        Member member = new Member();
        member.setEmail(dto.getEmail());
        member.setNickname(dto.getNickname());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));  // 비번 암호화
        member.setProvider("google");
        member.setProviderId(tempUser.getProviderId());
        member.setVerified(true);

        memberMapper.insertByGoogle(member);
        return member;
    }
    
    public boolean existsByNickname(String nickname) {
        return memberMapper.countByNickname(nickname) > 0;
    }
}
