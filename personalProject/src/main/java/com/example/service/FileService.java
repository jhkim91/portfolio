package com.example.service;

import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface FileService {
	public int uploadFiles(MultipartHttpServletRequest multiRequest, int atchFileId);

	public void fileDeleteInfo(int atchFileId, int fileSn);
}
