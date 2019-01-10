package com.example.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

	public List<Portfolio> findByOrderBySeqDesc();
	
	//메인에서 출력
	public List<Portfolio> findFirst20ByAtchFileIdGreaterThanEqualOrderBySeqDesc(int atchFileId);
	
	public Page<Portfolio> findAll(Pageable pageable);

	public Portfolio findBySeq(int seq);
	
	@Transactional
	public Long deleteBySeqAndUId(int seq, String uId);
	
	public Page<Portfolio> findByTitleContaining(String title, Pageable pageable);

	@Query(value = "select * from portfolio where "
		    + "(?1 = '' or lower(title) like concat('%', lower(?1), '%'))"
		    + "and"
		    + "(?2 = '' or lower(content) like concat('%', lower(?2), '%'))" , nativeQuery = true)
	public Page<Portfolio> findPortfolioSearchList(String title, String content, Pageable pageable);
	
}