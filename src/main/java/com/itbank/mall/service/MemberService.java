package com.itbank.mall.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.itbank.mall.dto.GoogleSignupRequestDto;
import com.itbank.mall.dto.SignupRequestDto;
import com.itbank.mall.dto.TempUserDto;
import com.itbank.mall.dto.UpdateMemberRequestDto;
import com.itbank.mall.entity.Member;
import com.itbank.mall.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public Member signin(String email, String rawPassword) {
        Member member = memberMapper.findByEmail(email);
        if (member == null) {
            return null;
        }

        boolean matched = passwordEncoder.matches(rawPassword, member.getPassword());
        return matched ? member : null;
    }
    
    public void signup(SignupRequestDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        
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
        member.setName(dto.getName());
        member.setNickname(dto.getNickname());
        member.setPhone(dto.getPhone());       // ✅ 추가
        member.setAddress(dto.getAddress());   // ✅ 추가
        member.setProvider("local");
        member.setProviderId(null);
        member.setVerified(false);
        member.setGrade("BASIC");

        memberMapper.insertMember(member);
    }
    
    public Member findByEmail(String email) {
        return memberMapper.findByEmail(email);
    }
    
    public boolean existsByEmail(String email) {
        return memberMapper.countByEmail(email) > 0;
    }
    
    public Member signupByGoogle(GoogleSignupRequestDto dto, TempUserDto tempUser) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        
        Member member = new Member();
        member.setEmail(dto.getEmail());
        member.setNickname(dto.getNickname());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));  // 비번 암호화
        member.setProvider("google");
        member.setProviderId(tempUser.getProviderId());
        member.setVerified(true);
        member.setGrade("BASIC");

        memberMapper.insertByGoogle(member);
        return member;
    }
    
    public void updateMemberInfo(Long memberId, UpdateMemberRequestDto dto) {
        Member member = memberMapper.findById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다");
        }

        if (dto.getName() != null) member.setName(dto.getName());
        if (dto.getNickname() != null) member.setNickname(dto.getNickname());
        if (dto.getPhone() != null) member.setPhone(dto.getPhone());
        if (dto.getAddress() != null) member.setAddress(dto.getAddress());

        memberMapper.updateMember(member);
    }
    
    public boolean existsByNickname(String nickname) {
        return memberMapper.countByNickname(nickname) > 0;
    }
}
