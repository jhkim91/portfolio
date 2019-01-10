package com.example.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.FileVO;

public interface FileRepository extends JpaRepository<FileVO, Long> {
	
	@Query(value = "SELECT coalesce(max(fv.atch_file_id), 0) FROM filevo fv", nativeQuery = true)
	public int getMaxId();
	
	@Query(value = "SELECT coalesce(max(fv.file_sn), 0) FROM filevo fv WHERE fv.atch_file_id = ?", nativeQuery = true)
	public int getMaxFileSn(int atchFileId);

//    @Query(value = "insert into filevo (atch_file_id, thumbnail_yn, file_cn, file_extsn, file_mg, file_sn, file_stre_cours, orignl_file_nm, stre_file_nm) VALUES (:atchFileId, :ThumbnailYn, :fileCn, :fileExtsn, :fileMg, :fileSn, :fileStreCours, :orignlFileNm, :streFileNm)", nativeQuery = true)
//    @Transactional
//    void insertFileVO(int atchFileId, String ThumbnailYn, String fileCn, String fileExtsn, int fileMg, int fileSn, String fileStreCours, String orignlFileNm, String streFileNm);
    
	public List<FileVO> findByAtchFileIdOrderByFileSnAsc(int atchFileId);
	
	//메인
	public List<FileVO> findByAtchFileIdAndThumbnailYnOrderByFileSnAsc(int atchFileId, String thumbnailYn);
	
	public FileVO findByAtchFileIdAndFileSn(int atchFileId, int fileSn);

	@Transactional
	public Long deleteByAtchFileIdAndFileSn(int atchFileId, int fileSn);
}
