package com.example.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Portfolio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int seq;

	private String uId;
	
	@NotNull
	@Size(min = 1, max = 100, message = "Must be between 1 and 100")
	private String title;
	
//	@NotEmpty(message = "내용을 입력해주세요.")
	@NotNull
	@Size(min=2, max=2000)
	private String content;
	
	private String writer;
	
	@CreationTimestamp
	@Column(name = "REG_DATE", updatable = false)
	private Date regdate;

	@UpdateTimestamp
	@Column(name = "UPDATE_DATE", insertable = false)
	private Date updatedate;
	
	private int atchFileId; //첨부파일 아이디
	
	@Transient	
	private MultipartFile file;
	@Transient	
	private List<FileVO> fileVO;
}
