package com.example.service;

import org.springframework.data.domain.Page;

import com.example.model.Board;
import org.springframework.data.domain.Pageable;

public interface BoardService {

	public void boardInsert(Board board);
	
	public void boardUpdate(Board board);

//	public Page<Board> getAllBoard(Pageable pageable);
	public Page<Board> findBoardSearchList(String searchCnd, String searchWrd, Pageable pageable);
	public Board boardView(int seq);
	
	public void boardDelete(int seq, String uId);

}
