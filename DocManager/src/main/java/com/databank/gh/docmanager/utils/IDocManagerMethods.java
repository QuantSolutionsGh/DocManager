package com.databank.gh.docmanager.utils;

import java.io.InputStream;
import java.util.List;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

public interface IDocManagerMethods {
	
	public String uploadFiles(List<UploadedFile> uploadedFiles) throws Exception;
	
	public String uploadFile(UploadedFile filename) throws Exception;   //returns a unique document number to be used by checker
	
	//public File getFile(String uniqueDocNumber);  //uniqueDocNumber is generated after successful file upload
	public List<String> getImages(String docRef);
	
	public StreamedContent getImage(String filename) throws Exception;
	

}
