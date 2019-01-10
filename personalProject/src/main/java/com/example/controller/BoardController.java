package com.example.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.Board;
import com.example.model.CustomUser;
import com.example.model.PageWrapper;
import com.example.model.Portfolio;
import com.example.service.BoardServiceImpl;


@Controller
@RequestMapping("/board")
public class BoardController {

	@Autowired
	private BoardServiceImpl boardService;

	//게시글 목록
	@RequestMapping(method=RequestMethod.GET)
	public String list(Model model,@PageableDefault(size=10, sort="seq",direction = Sort.Direction.DESC) Pageable pageable
			, @RequestParam(value = "searchCnd", defaultValue = "") String searchCnd
	   		, @RequestParam(value = "searchWrd", defaultValue = "") String searchWrd
	   			) throws Exception{
		PageWrapper<Board> page = new PageWrapper<Board>(boardService.findBoardSearchList(searchCnd, searchWrd, pageable), "/board");
        model.addAttribute("page", page);

        if (!model.containsAttribute("board")) {
            model.addAttribute("board", new Board());
        }

        return "/view/board/boardList";
	}

	//게시글 작성 페이지(GET)
	@RequestMapping(value = "/post",method=RequestMethod.GET)
	public String writeForm(@AuthenticationPrincipal CustomUser user, Board board) throws Exception{
		if(user == null) {
			return "redirect:/";
		}
		board.setUId(user.getUsername());
		board.setWriter(user.getUser_name());
		
		return "/view/board/boardWrite";
	}

	//로그인정보와 게시글 작성자와 맞는지 확인
	public boolean userChk(Board board) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CustomUser userDetails = (CustomUser)principal;
		return !(board.getWriter().equals(userDetails.getUser_name()) && board.getUId().equals(userDetails.getUsername()));
	}

	//게시글 작성(POST)
	@RequestMapping(value = "/post",method=RequestMethod.POST)
	public String write(@Valid Board board
			, BindingResult bindingResult
			, Model model)throws Exception{

		if (bindingResult.hasErrors()) {
			System.out.println("BINDING RESULT ERROR");
			return "view/board/boardWrite";
		} else if(userChk(board)) {
			return "redirect:/";
		} else {
			boardService.boardInsert(board);
		}
		return "redirect:/board";
	}
	
	//게시글 상세
	@RequestMapping(value = "/{bno}",method=RequestMethod.GET)
	public String view(@PathVariable("bno") int bno, Model model) throws Exception{

		Board board = boardService.boardView(bno);
		model.addAttribute("board", board);
		
		return "/view/board/boardView";
	}

	//게시글 수정 페이지(GET)
	@RequestMapping(value = "/post/{bno}", method=RequestMethod.GET)
	public String updateForm(@AuthenticationPrincipal CustomUser user, @PathVariable("bno") int bno, Model model) throws Exception{
		if(user == null) {
			return "redirect:/board/"+bno;
		}
		Board board = boardService.boardView(bno);
		model.addAttribute("board", board);

		return "/view/board/boardModify";
	}

	//게시글 수정(PATCH)
	@RequestMapping(value = "/post/{bno}", method=RequestMethod.PUT)
	public String update(
			@Valid Board board
			, BindingResult bindingResult
			, @PathVariable("bno") int bno
			, Model model)throws Exception{

		if (bindingResult.hasErrors()) {
			System.out.println("BINDING RESULT ERROR");
			return "view/board/boardModify";
		} else if(userChk(board)) {
			return "redirect:/";
		} else {
			board.setSeq(bno);
			boardService.boardUpdate(board);
		}
		return "redirect:/board/"+bno;
		
//		boardMapper.boardUpdate(board);
	}

	//게시글 삭제(DELETE)
	@RequestMapping(value = "/post/{bno}", method=RequestMethod.DELETE)
	public String delete(@AuthenticationPrincipal CustomUser user, @PathVariable("bno") int bno) throws Exception{
		if(user == null) {
			return "redirect:/board/"+bno;
		}
		boardService.boardDelete(bno, user.getUsername());

		return "redirect:/board";
	}
}