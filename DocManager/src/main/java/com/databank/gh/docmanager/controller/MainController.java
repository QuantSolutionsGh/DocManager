package com.databank.gh.docmanager.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.databank.gh.docmanager.utils.IDocManagerMethods;



public class MainController implements Serializable {
	
	
	private static final long serialVersionUID = -933602118417202939L;
	
	private IDocManagerMethods appMethods;
	
	private UploadedFile uploadedFile1;
	
	private UploadedFile uploadedFile2;
	
	private UploadedFile uploadedFile3;
	
	private List<String> imageList;
	
	private List<UploadedFile> uploadedList = new ArrayList<UploadedFile>();
	
	private String docRefToSearch;

	public MainController(){
		
		
	}
	
	
	public void getImages(){
		
		imageList=appMethods.getImages(this.docRefToSearch);   //returns filenames as string
	}
	
	
	public StreamedContent dispImage(String fileName) throws Exception{
		return appMethods.getImage(fileName);
	}
	
	
	public void save(){
		
		try {
			
			if (!uploadedFile1.getFileName().isEmpty()) {
				this.uploadedList.add(uploadedFile1);
			}
			if (!uploadedFile2.getFileName().isEmpty()) {
				this.uploadedList.add(uploadedFile2);
			}
			if (!uploadedFile3.getFileName().isEmpty()) {
				this.uploadedList.add(uploadedFile3);
			}
			
			
			//this.uploadedList.add(uploadedFile3);
			String docRef=appMethods.uploadFiles(uploadedList);
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Documents successfully uploaded."
			 		+ "Your document ref number is "+docRef));
			 this.uploadedList=new ArrayList<UploadedFile>();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	

	

	public UploadedFile getUploadedFile1() {
		return uploadedFile1;
	}

	public void setUploadedFile1(UploadedFile uploadedFile1) {
		this.uploadedFile1 = uploadedFile1;
	//	this.uploadedList.add(uploadedFile1);
	}

	public UploadedFile getUploadedFile2() {
		return uploadedFile2;
	}

	public void setUploadedFile2(UploadedFile uploadedFile2) {
		this.uploadedFile2 = uploadedFile2;
	//	this.uploadedList.add(uploadedFile2);
	}

	public UploadedFile getUploadedFile3() {
		return uploadedFile3;
	}

	public void setUploadedFile3(UploadedFile uploadedFile3) {
		this.uploadedFile3 = uploadedFile3;
	//	this.uploadedList.add(uploadedFile3);

	}




	public void setAppMethods(IDocManagerMethods appMethods) {
		this.appMethods = appMethods;
	}


	public String getDocRefToSearch() {
		return docRefToSearch;
	}


	public void setDocRefToSearch(String docRefToSearch) {
		this.docRefToSearch = docRefToSearch;
	}


	public List<String> getImageList() {
		return imageList;
	}


	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
	}
	
	
	
	
	
	
	
	

	
	
}
