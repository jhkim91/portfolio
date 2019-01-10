package com.example.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.model.Board;
import com.example.model.FileVO;
import com.example.model.Member;
import com.example.model.Portfolio;
import com.example.repository.BoardRepository;
import com.example.repository.FileRepository;
import com.example.repository.PortfolioRepository;
import com.example.service.MemberService;
import com.example.service.PortfolioService;


@Controller
public class MemberController {
    
    @Autowired
    MemberService memberService;
    
    @Autowired
    PortfolioRepository portfolioRepository; 
    
    @Autowired
    FileRepository fileRepository; 
    
    @Autowired
    BoardRepository boardRepository; 
    
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String main(Model model) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(System.getProperty("user.dir"));
    	if(auth!=null) {
			System.out.println(auth.getAuthorities());
		}
    	
    	int portIndex=0;
    	List<Portfolio> Portfolios = portfolioRepository.findFirst20ByAtchFileIdGreaterThanEqualOrderBySeqDesc(1);
    	for (Portfolio portfolio : Portfolios) {
    		List<FileVO> searchFile = fileRepository.findByAtchFileIdAndThumbnailYnOrderByFileSnAsc(portfolio.getAtchFileId(), "Y");
    		portfolio.setFileVO(searchFile);
			Portfolios.set(portIndex, portfolio);
			portIndex++;
		}
    	
    	List<Board> boards= boardRepository.findFirst4ByOrderBySeqDesc();
		model.addAttribute("portfolio", Portfolios);
		model.addAttribute("board", boards);
    	
		return "index";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
		return "view/member/login";
	}
	
	@RequestMapping(value = "/login-error", method = RequestMethod.GET)
		public String loginError(Model model) {
		model.addAttribute("loginError", true);
		return "view/member/login";
	}
    
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public void accessdeniedPage() {}
    
	@RequestMapping(value = "/regist/post", method = RequestMethod.GET)
	public String registPage(Member member) {
		return "view/member/registWrite";
	}
	
    @RequestMapping(value = "/regist/post", method = RequestMethod.POST)
	public String regist(@Valid Member member,
			BindingResult bindingResult, Model model)throws Exception{

		if (bindingResult.hasErrors()) {
			return "view/member/registWrite";
		} else {
			memberService.save(member);
		}
		return "index";
    }   
}