package com.example.service;

import com.example.model.Member;

public interface MemberService {

	public Member findByUemail(String email);

    public void save(Member member);

}