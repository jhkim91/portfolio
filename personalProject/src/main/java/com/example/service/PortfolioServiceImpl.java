package com.example.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.config.UploadFileUtils;
import com.example.model.FileVO;
import com.example.model.PageWrapper;
import com.example.model.Portfolio;
import com.example.repository.FileRepository;
import com.example.repository.PortfolioRepository;

@Service
public class PortfolioServiceImpl implements PortfolioService{
	@Autowired
	PortfolioRepository portfolioRepository;
	
	@Autowired
	FileServiceImpl fileService;

	@Autowired
	FileRepository fileRepository;
	
	@Override
	public void portfolioInsert(Portfolio portfolio, MultipartFile file) throws Exception {
		if(!file.isEmpty()) {
			FileVO fileVO = UploadFileUtils.uploadFile(file, 0);
			int atchFileId = fileRepository.getMaxId()+1;
			fileVO.setAtchFileId(atchFileId);
			fileRepository.save(fileVO);
			portfolio.setAtchFileId(atchFileId);
		}
		
		portfolioRepository.save(portfolio);
	}
	
	@Override
	public void portfolioUpdate(Portfolio portfolio, MultipartHttpServletRequest multiRequest) throws Exception{
		int atchFileId = fileService.uploadFiles(multiRequest, portfolio.getAtchFileId());
		portfolio.setAtchFileId(atchFileId);
		
		portfolioRepository.save(portfolio);
	}

	@Override
	public Page<Portfolio> findPortfolioSearchList(String searchCnd, String searchWrd, Pageable pageable) {
		String title="";
		String content="";
		if("1".equals(searchCnd)) title = searchWrd;
		else if("2".equals(searchCnd)) content = searchWrd;
		
		return portfolioRepository.findPortfolioSearchList(title, content, pageable);
	}

	@Override
	public Portfolio portfolioView(int seq) {
		return portfolioRepository.findBySeq(seq);
	}
	
	@Override
	public void portfolioDelete(int seq, String uId) {

		Portfolio portfolio = portfolioRepository.findBySeq(seq);
		
		if(portfolio.getAtchFileId()>0) {
			List<FileVO> searchFile = fileRepository.findByAtchFileIdOrderByFileSnAsc(portfolio.getAtchFileId());
			for (FileVO fileVO : searchFile) {
				fileService.fileDeleteInfo(fileVO.getAtchFileId(), fileVO.getFileSn());
			}
		}
		
		portfolioRepository.deleteBySeqAndUId(seq, uId);
	}
	
}
