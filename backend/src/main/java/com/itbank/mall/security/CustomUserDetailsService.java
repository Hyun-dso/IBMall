package com.itbank.mall.security;

import com.itbank.mall.entity.member.Member;
import com.itbank.mall.mapper.member.MemberMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberMapper.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("존재하지 않는 이메일입니다: " + email);
        }

        return new CustomUserDetails(member);
    }
}
