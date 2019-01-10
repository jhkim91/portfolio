package com.example.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.model.Member;
import com.example.model.MemberRole;
import com.example.repository.MemberRepository;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MemberRepository members;

    public Member findByUemail(String email) {
    	return members.findByUemail(email);
    }
    
    public void save(Member member) {
		MemberRole role = new MemberRole();
		member.setUpw(passwordEncoder.encode(member.getUpw()));
		role.setRoleName("BASIC");
		member.setRoles(Arrays.asList(role));
        
        members.save(member);
    }
}