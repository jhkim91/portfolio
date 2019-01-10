package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.model.Board;
import com.example.repository.BoardRepository;

@Service
public class BoardServiceImpl implements BoardService{
	@Autowired
	BoardRepository boardRepository;
	
	@Override
	public void boardInsert(Board board) {
		boardRepository.save(board);
	}
	
	@Override
	public void boardUpdate(Board board) {
		boardRepository.save(board);
	}

	@Override
	public Page<Board> findBoardSearchList(String searchCnd, String searchWrd, Pageable pageable) {
		String title="";
		String content="";
		if("1".equals(searchCnd)) title = searchWrd;
		else if("2".equals(searchCnd)) content = searchWrd;
		
		return boardRepository.findBoardSearchList(title, content, pageable);
	}

	@Override
	public Board boardView(int seq) {
		return boardRepository.findBySeq(seq);
	}
	
	@Override
	public void boardDelete(int seq, String uId) {
		boardRepository.deleteBySeqAndUId(seq, uId);
	}
	
}
