package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@IdClass(FileVOId.class)
public class FileVO implements Serializable{
	@Id
	private int atchFileId; //첨부파일 아이디
	@Id
	private int fileSn; // 파일연번
	
	private String orignlFileNm; // 원파일명
	private String streFileNm; // 저장파일명
	private String thumbnailYn; // 썸네일 여부
	private String fileExtsn; // 파일확장자
	private int fileMg; // 파일크기
	private String fileStreCours; // 파일저장경로
	@CreationTimestamp
	private Date creatDt; // 생성일자
	private String fileCn; // 파일내용
}
