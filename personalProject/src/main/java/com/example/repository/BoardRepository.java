package com.example.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Board;
import com.example.model.Portfolio;

public interface BoardRepository extends JpaRepository<Board, Long> {

	public List<Board> findByOrderBySeqDesc();

	@Query(value = "select * from board where "
		    + "(?1 = '' or lower(title) like concat('%', lower(?1), '%'))"
		    + "and"
		    + "(?2 = '' or lower(content) like concat('%', lower(?2), '%'))" , nativeQuery = true)
	public Page<Board> findBoardSearchList(String title, String content, Pageable pageable);
//	public Page<Board> findAll(Pageable pageable);

	public Board findBySeq(int seq);
	
	@Transactional
	public Long deleteBySeqAndUId(int seq, String uId);
	
	//메인에서 출력
	public List<Board> findFirst4ByOrderBySeqDesc();
	
}