package com.example.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.config.MediaUtils;
import com.example.config.UploadFileUtils;
import com.example.model.FileVO;
import com.example.repository.FileRepository;
import com.example.service.FileServiceImpl;

@Controller
public class UploadController {

	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	@Autowired
	FileRepository fileRepository;
	
	@Autowired
	FileServiceImpl fileService;

	/*@RequestMapping(value = "/uploadForm", method = RequestMethod.GET)
	public void uploadForm() throws Exception {
	}

	@RequestMapping(value = "/uploadForm", method = RequestMethod.POST)
	public String uploadForm(MultipartFile file, Model model) throws Exception {

		logger.info("originalName: " + file.getOriginalFilename());
		logger.info("size: " + file.getSize());
		logger.info("contentType: " + file.getContentType());

		FileVO fileVO = UploadFileUtils.uploadFile(file, 0);
		
		fileRepository.save(fileVO);
		model.addAttribute("savedName", savedName);
		ResponseEntity<String> aaa = new ResponseEntity<String>( UploadFileUtils.uploadFile(uploadPath, file.getOriginalFilename(), file.getBytes()), HttpStatus.OK);
		System.out.println("> "+aaa.toString());

		return "uploadResult";
	}*/

/*	private FileVO uploadFile(String originalName, byte[] fileData) throws Exception {

		UUID uid = UUID.randomUUID();
		String savedName = uid.toString() + "_" + originalName;
		File target = new File(uploadPath, savedName);
		FileCopyUtils.copy(fileData, target);

		FileVO fileVO= new FileVO();
		return fileVO;
	} */
	
/*	@ResponseBody
	@RequestMapping(value = "/uploadAjax", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public String uploadAjax(MultipartFile file, String str, HttpSession session,
			HttpServletRequest request, Model model) throws Exception {

		logger.info("originalName: " + file.getOriginalFilename());

			ResponseEntity<String> img_path = new ResponseEntity<>(UploadFileUtils.uploadFile(uploadPath, file.getOriginalFilename(), file.getBytes()), HttpStatus.CREATED);
			String user_imgPath = (String) img_path.getBody();

			logger.info(user_imgPath);
			UserVO vo = new UserVO();
			vo.setUser_profileImagePath(user_imgPath);
			UserVO id = (UserVO) session.getAttribute("login");
			System.out.println(id.getUser_id());
			vo.setUser_id(id.getUser_id());
			logger.info("file name : " + user_imgPath);

			return user_imgPath;
	}*/

	/*@ResponseBody
	@RequestMapping("/displayFile")
	public ResponseEntity<byte[]> displayFile(String fileName) throws Exception {
		String uploadPath="C:\\spring-tool-suite-4-4.0.2.RELEASE-e4.9.0-win32.win32.x86_64\\workspace\\portfolio-3\\src\\main\\resources\\static\\uploadFiles\\";
		
		InputStream in = null;
		ResponseEntity<byte[]> entity = null;

		logger.info("FILE NAME: " + fileName);

		try {

			String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);

			MediaType mType = MediaUtils.getMediaType(formatName);

			HttpHeaders headers = new HttpHeaders();

			in = new FileInputStream(uploadPath + fileName);

			if (mType != null) {
				headers.setContentType(mType);
			} else {
				fileName = fileName.substring(fileName.indexOf("_") + 1);
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.add("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
			}

			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		} finally {
			in.close();
		}
		return entity;
	}*/


	@RequestMapping(value = "/deleteFile/{atchFileId}/{fileSn}", method = RequestMethod.POST)
	public String deleteFile(@PathVariable("atchFileId") int atchFileId, @PathVariable("fileSn") int fileSn,HttpServletRequest req, HttpServletResponse resp) {

		fileService.fileDeleteInfo(atchFileId, fileSn);

		return "forward:/portfolio/post/"+atchFileId;
	}
	
	@RequestMapping(value = "/download/{atchFileId}/{fileSn}", method = RequestMethod.GET)
	public String bbsService(@PathVariable("atchFileId") int atchFileId, @PathVariable("fileSn") int fileSn,HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		FileVO fileVO = fileRepository.findByAtchFileIdAndFileSn(atchFileId, fileSn);

		String saveDirectory = System.getProperty("user.dir")+"/bin/main/static"+fileVO.getFileStreCours()+"/";
		String filename = fileVO.getStreFileNm();
	
		File file = new File(saveDirectory + filename);
	
		long fSize = file.length();

		if (fSize > 0) {
			resp.setContentType("aplication/download");
			resp.setContentLengthLong(file.length());
		
			// 모든 브라우저가 지원
			filename = URLEncoder.encode(fileVO.getOrignlFileNm(), "utf-8").replace("+", "%20").replace("(", "%28").replace(")", "%29");
		
			// 익스플로러는 지원 안됨
			//        originFileName = new String(originFileName.getBytes("utf-8"), "iso-8859-1").replace("+", "%20");
		
			// Content-Disposition: form-data; name="fileName"; filename="파일명"
			resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\";");
		
			OutputStream out = resp.getOutputStream();
			FileInputStream fis = null;
		
			try {
				int temp;
				fis = new FileInputStream(file);
				while((temp = fis.read()) != -1) {
					out.write(temp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(fis != null) {
					try {
						fis.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		}else {
			resp.setContentType("application/x-msdownload");

			PrintWriter printwriter = resp.getWriter();
			
			printwriter.println("<html>");
			printwriter.println("<br><br><br><h2>Could not get file name:<br>" + fileVO.getOrignlFileNm() + "</h2>");
			printwriter.println("<br><br><br><center><h3><a href='javascript: history.go(-1)'>Back</a></h3></center>");
			printwriter.println("<br><br><br>&copy; webAccess");
			printwriter.println("</html>");
			
			printwriter.flush();
			printwriter.close();
		}
	
		return null;
	}

}