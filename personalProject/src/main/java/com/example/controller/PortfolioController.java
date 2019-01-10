package com.example.controller;

import javax.ws.rs.core.MediaType;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.devtools.tunnel.server.PortProvider;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.config.UploadFileUtils;
import com.example.model.CustomUser;
import com.example.model.FileVO;
import com.example.model.PageWrapper;
import com.example.model.Portfolio;
import com.example.repository.FileRepository;
import com.example.service.FileServiceImpl;
import com.example.service.PortfolioServiceImpl;


@Controller
@RequestMapping("/portfolio")
public class PortfolioController {

	@Autowired
	private PortfolioServiceImpl portfolioService;

	@Autowired
	FileRepository fileRepository;

	//게시글 목록
	@RequestMapping(method=RequestMethod.GET)
	public String list(Model model,@PageableDefault(size=10, sort="seq",direction = Sort.Direction.DESC) Pageable pageable
			, @RequestParam(value = "searchCnd", defaultValue = "") String searchCnd
	   		, @RequestParam(value = "searchWrd", defaultValue = "") String searchWrd
				) throws Exception{
		PageWrapper<Portfolio> page = new PageWrapper<Portfolio>(portfolioService.findPortfolioSearchList(searchCnd, searchWrd, pageable), "/portfolio");
        model.addAttribute("page", page);
        	      
        if (!model.containsAttribute("portfolio")) {
            model.addAttribute("portfolio", new Portfolio());
        }

        return "/view/portfolio/portfolioList";
	}

	//게시글 작성 페이지(GET)
	@RequestMapping(value = "/post",method=RequestMethod.GET)
	public String writeForm(@AuthenticationPrincipal CustomUser user, Portfolio portfolio) throws Exception{
		if(user == null) {
			return "redirect:/";
		}
		portfolio.setUId(user.getUsername());
		portfolio.setWriter(user.getUser_name());
		
		return "/view/portfolio/portfolioWrite";
	}

	//로그인정보와 게시글 작성자와 맞는지 확인
	public boolean userChk(Portfolio portfolio) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CustomUser userDetails = (CustomUser)principal;
		return !(portfolio.getWriter().equals(userDetails.getUser_name()) && portfolio.getUId().equals(userDetails.getUsername()));
	}

	//게시글 작성(POST)
//	@RequestMapping(value = "/post", method = RequestMethod.POST,consumes={"multipart/form-data", "application/json"}, headers = "content-type=application/x-www-form-urlencoded")
	@RequestMapping(value = "/post", method = RequestMethod.POST)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String write(@Valid Portfolio portfolio
			, BindingResult bindingResult
			, @RequestParam("file") MultipartFile file
			, Model model) throws Exception {

		if (bindingResult.hasErrors()) {
			System.out.println("BINDING RESULT ERROR");
			return "view/portfolio/portfolioWrite";
		} else if(userChk(portfolio)) {
			return "redirect:/";
		} else {
			portfolioService.portfolioInsert(portfolio, file);
		}
		return "redirect:/portfolio";
	}
	
	//게시글 상세
	@RequestMapping(value = "/{bno}",method=RequestMethod.GET)
	public String view(@PathVariable("bno") int bno, Model model, HttpServletRequest request) throws Exception{
		Portfolio portfolio = portfolioService.portfolioView(bno);
		if(portfolio.getAtchFileId()>0) {
			List<FileVO> searchFile = fileRepository.findByAtchFileIdOrderByFileSnAsc(portfolio.getAtchFileId());
			portfolio.setFileVO(searchFile);
		}
		model.addAttribute("portfolio", portfolio);
		
		return "/view/portfolio/portfolioView";
	}

	//게시글 수정 페이지(GET)
	@RequestMapping(value = "/post/{bno}", method=RequestMethod.GET)
	public String updateForm(@AuthenticationPrincipal CustomUser user, @PathVariable("bno") int bno, Model model) throws Exception{
		if(user == null) {
			return "redirect:/portfolio/"+bno;
		}
		Portfolio portfolio = portfolioService.portfolioView(bno);
		if(portfolio.getAtchFileId()>0) {
			List<FileVO> searchFile = fileRepository.findByAtchFileIdOrderByFileSnAsc(portfolio.getAtchFileId());
			portfolio.setFileVO(searchFile);
			
			model.addAttribute("fileListCnt", searchFile.size());
			model.addAttribute("atchFileId", portfolio.getAtchFileId());
		}
		model.addAttribute("portfolio", portfolio);

		return "/view/portfolio/portfolioModify";
	}
	
	//게시글 수정(PATCH)
	@RequestMapping(value = "/update/{bno}", method=RequestMethod.POST)
	public String updatePortfolio(@Valid Portfolio portfolio
			, final MultipartHttpServletRequest multiRequest
			, BindingResult bindingResult
			, @PathVariable("bno") int bno
			, Model model)throws Exception{

		if (bindingResult.hasErrors()) {
			System.out.println("BINDING RESULT ERROR");
			return "view/portfolio/portfolioWrite";
		} else if(userChk(portfolio)) {
			return "redirect:/";
		} else {
			portfolio.setSeq(bno);
			portfolioService.portfolioUpdate(portfolio, multiRequest);
		}
		return "redirect:/portfolio/"+bno;
		
//		portfolioMapper.portfolioUpdate(portfolio);
	}

	//게시글 삭제(DELETE)
	@RequestMapping(value = "/post/{bno}", method=RequestMethod.DELETE)
	public String delete(@AuthenticationPrincipal CustomUser user, @PathVariable("bno") int bno) throws Exception{
		if(user == null) {
			return "redirect:/portfolio/"+bno;
		}
		
		portfolioService.portfolioDelete(bno, user.getUsername());

		return "redirect:/portfolio";
	}
}