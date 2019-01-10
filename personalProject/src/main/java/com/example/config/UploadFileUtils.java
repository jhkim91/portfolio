package com.example.config;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.model.FileVO;
import com.example.repository.FileRepository;

public class UploadFileUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(UploadFileUtils.class);

	public static FileVO uploadFile(MultipartFile uploadFile, int fileSn)throws Exception{
		
		String uploadPath=System.getProperty("user.dir")+"/bin/main/static/uploadFiles/";

		String originalName = uploadFile.getOriginalFilename();
		byte[] fileData = uploadFile.getBytes();
		
		UUID uid = UUID.randomUUID();

		String savedName = uid.toString() +"_"+originalName;

		String savedPath = calcPath(uploadPath);

		File target = new File(uploadPath+savedPath,savedName);

		FileCopyUtils.copy(fileData, target);

		String formatName = originalName.substring(originalName.lastIndexOf(".")+1);

		String uploadedFileName = null;

		FileVO file= new FileVO();
		if(MediaUtils.getMediaType(formatName) != null){
			uploadedFileName = makeThumbnail(uploadPath, savedPath, savedName);
			uploadedFileName = "Y";
		}else{
			uploadedFileName = makeIcon(uploadPath, savedPath, savedName);
			uploadedFileName = "N";
		}

		file.setThumbnailYn(uploadedFileName);
		file.setFileSn(fileSn);
		file.setStreFileNm(savedName);
		file.setFileStreCours(("/uploadFiles/"+savedPath).replace(File.separatorChar, '/').replace("//", "/"));
		file.setOrignlFileNm(uploadFile.getOriginalFilename());
		file.setFileMg((int) uploadFile.getSize());
		file.setFileExtsn(uploadFile.getContentType());

		return file;
	}

	private static  String makeIcon(String uploadPath, String path, String fileName)throws Exception{

		String iconName = uploadPath + path + File.separator+ fileName;

		return iconName.substring(uploadPath.length()).replace(File.separatorChar, '/');
	}


	private static  String makeThumbnail(String uploadPath, String path, String fileName)throws Exception{

		BufferedImage sourceImg = ImageIO.read(new File(uploadPath + path, fileName));

		BufferedImage destImg = Scalr.resize(sourceImg, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT,100);

		String thumbnailName = uploadPath + path + File.separator +"s_"+ fileName;

		File newFile = new File(thumbnailName);
		String formatName = fileName.substring(fileName.lastIndexOf(".")+1);

		ImageIO.write(destImg, formatName.toUpperCase(), newFile);
		return thumbnailName.substring(uploadPath.length()).replace(File.separatorChar, '/');
	}


	private static String calcPath(String uploadPath){

		Calendar cal = Calendar.getInstance();

		String yearPath = File.separator+cal.get(Calendar.YEAR);

		String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1);

		String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.DATE));

		makeDir(uploadPath, yearPath,monthPath,datePath);

		logger.info(datePath);

		return datePath;
	}


	private static void makeDir(String uploadPath, String... paths){

		if(new File(paths[paths.length-1]).exists()){
			return;
		}

		for (String path : paths) {

			File dirPath = new File(uploadPath + path);

			if(! dirPath.exists() ){
				dirPath.mkdir();
			}
		}
	}

}