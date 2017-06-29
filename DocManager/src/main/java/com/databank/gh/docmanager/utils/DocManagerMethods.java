package com.databank.gh.docmanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Transactional;

public class DocManagerMethods extends JdbcDaoSupport implements IDocManagerMethods {

	public String storagePath;

	@Override
	@Transactional
	public String uploadFiles(List<UploadedFile> uploadedFiles) throws Exception {
		int docRef=getDocRef();
		for (UploadedFile item : uploadedFiles) {

			uploadFile(item);
			this.storeInDb(docRef, item.getFileName());
		}
		return String.valueOf(docRef);
	}

	@Override
	
	public String uploadFile(UploadedFile _file) throws Exception {
		String filename = FilenameUtils.getName(_file.getFileName());
		InputStream input = _file.getInputstream();
		OutputStream output = new FileOutputStream(new File(storagePath, filename));

		try {
			IOUtils.copy(input, output);
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
		return null; 

	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}


	private int getDocRef() {
		String sql = "select max(doc_ref)+1 from sequencer";
		int docRef = this.getJdbcTemplate().queryForObject(sql, Integer.class);
		this.getJdbcTemplate().update("update sequencer set doc_ref=?", docRef);
		return docRef;
	}
	
	private void storeInDb(int docRef,String fileName){
		String sql = "insert into tbl_main(doc_ref,file_name) values (?,?)";
		this.getJdbcTemplate().update(sql, new Object[] { docRef,fileName
			});
	}

	@Override
	public List<String> getImages(String docRef) {
		String sql="select file_name from tbl_main where doc_ref=?";
		List<String> fileList = new ArrayList<String>();
		List<Map<String, Object>>rows =this.getJdbcTemplate().queryForList(sql, docRef);
		
		for (Map row: rows){
			
			fileList.add((String)row.get("file_name"));
			
		}
		return fileList;
	}

	@Override
	public StreamedContent getImage(String filename) throws Exception {
		FacesContext context = FacesContext.getCurrentInstance();

	    if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
	        // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
	        return new DefaultStreamedContent();
	    }
	    else {
	        // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
	     //   String filename = context.getExternalContext().getRequestParameterMap().get("filename");
	    	StreamedContent a;
	        a= new DefaultStreamedContent(new FileInputStream(new File(this.storagePath, filename)));
	        return a;
	    }
	}

}
