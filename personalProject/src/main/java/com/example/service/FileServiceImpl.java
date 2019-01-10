package com.example.service;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.config.UploadFileUtils;
import com.example.model.FileVO;
import com.example.repository.FileRepository;

@Service
public class FileServiceImpl {

	@Autowired
	FileRepository fileRepository;
	
	// 다중파일 추가
	public int uploadFiles(MultipartHttpServletRequest multiRequest, int atchFileId) throws Exception{

	    final Map<String, MultipartFile> files = multiRequest.getFileMap();
	    if (!files.isEmpty()) {
			if(atchFileId == 0) {
				atchFileId = fileRepository.getMaxId()+1;
			}
			int index = fileRepository.getMaxFileSn(atchFileId);

	    	Iterator<Entry<String, MultipartFile>> itr = files.entrySet().iterator();
			MultipartFile file;
			while (itr.hasNext()) {
				index++;
				Entry<String, MultipartFile> entry = itr.next();

				file = entry.getValue();

				if(!file.isEmpty()) {
					FileVO fileVO = UploadFileUtils.uploadFile(file, index);
					fileVO.setAtchFileId(atchFileId);
//					fileRepository.insertFileVO(fileVO.getAtchFileId(), fileVO.getThumbnailYn(), fileVO.getFileCn(), fileVO.getFileExtsn(), fileVO.getFileMg(), fileVO.getFileSn(), fileVO.getFileStreCours(), fileVO.getOrignlFileNm(), fileVO.getStreFileNm());
					fileRepository.save(fileVO);
				}					
			}
	    }
	    return atchFileId;
	}
	
	//파일삭제
	public void fileDeleteInfo(int atchFileId, int fileSn) {
		FileVO fileVO = fileRepository.findByAtchFileIdAndFileSn(atchFileId, fileSn);
	
		String saveDirectory = System.getProperty("user.dir")+"/bin/main/static"+fileVO.getFileStreCours()+"/";
		String filename = fileVO.getStreFileNm();
	
		File file = new File(saveDirectory + filename);
	
		long fSize = file.length();
	
		if (fSize > 0) {
			if("Y".equals(fileVO.getThumbnailYn())) {
				new File(saveDirectory + "s_" + filename.replace('/', File.separatorChar)).delete();
			}
			new File(saveDirectory + filename.replace('/', File.separatorChar)).delete();
			
			fileRepository.deleteByAtchFileIdAndFileSn(atchFileId, fileSn);
		}
	}
}
