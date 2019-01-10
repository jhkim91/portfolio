package com.example.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Member;

@Repository
public interface MemberRepository extends CrudRepository<Member, Long> {

	Member findByUemail(String email);

}
