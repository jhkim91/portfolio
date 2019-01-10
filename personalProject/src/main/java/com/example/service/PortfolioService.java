package com.example.service;

import org.springframework.data.domain.Page;

import com.example.model.Portfolio;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface PortfolioService {

	public void portfolioInsert(Portfolio portfolio, MultipartFile file) throws Exception;
	
	public void portfolioUpdate(Portfolio portfolio, MultipartHttpServletRequest multiRequest) throws Exception;

//	public Page<Portfolio> getAllPortfolio(Pageable pageable);
	public Page<Portfolio> findPortfolioSearchList(String searchCnd, String searchWrd, Pageable pageable);
	
	public Portfolio portfolioView(int seq);
	
	public void portfolioDelete(int seq, String uId);

}
