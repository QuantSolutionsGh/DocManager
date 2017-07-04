package com.databank.gh.docmanager.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.databank.gh.docmanager.utils.IDocManagerMethods;



public class MainController implements Serializable {
	
	
	private static final long serialVersionUID = -933602118417202939L;
	
	private IDocManagerMethods appMethods;
	
	private UploadedFile uploadedFile1;
	
	private UploadedFile uploadedFile2;
	
	private int docRef=0;
	
	private UploadedFile uploadedFile3;
	
	private UploadedFile uploadedFile4;
	
	private List<String> imageList;
	
	private String imageToDisplay;
	
	private List<UploadedFile> uploadedList = new ArrayList<UploadedFile>();
	
	private String docRefToSearch;

	public MainController(){
		
		
	}
	
	
	public void getImages(){
		
		imageList=appMethods.getImages(this.docRefToSearch);   //returns filenames as string
	}
	
	
	public StreamedContent dispImage(String fileName) throws Exception{
		return null;
	}
	
	
	public void displayPopUp(String displayImage){
		this.setImageToDisplay("/images/"+displayImage);
		RequestContext.getCurrentInstance().execute("PF('myDialogVar').show();");
		System.out.println("1");
	}
	
	
	
	public void uploadDoc(FileUploadEvent e) throws Exception{
	//	int docRef=0;  //int docRef=appMethods.getDocRef; i.e 
		
		if (docRef==0){
			docRef=appMethods.getDocRef();  //initialize docRef
		}
		
		appMethods.pushFile(e.getFile(), docRef);
		
		//System.out.println(e.getFile().getFileName());
	}
	
	
	
	public void uploadComplete() throws Exception{

		System.out.println("Complete");
	
		//now lets push into database
		//this.uploadedList.add(uploadedFile3);
		//String docRef=appMethods.uploadFiles(uploadedList);
		
		 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Documents successfully uploaded."
		 		+ "Your document ref number is "+String.valueOf(docRef)));
		 this.uploadedList=new ArrayList<UploadedFile>();
		 
		 this.docRef=0; //reset doc ref
		 
		 
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
			if (!uploadedFile4.getFileName().isEmpty()) {
				this.uploadedList.add(uploadedFile4);
			}
			
			if (this.uploadedList.size()==0){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No document has been attached."
				 		));
				
			}
			else {
			//this.uploadedList.add(uploadedFile3);
			String docRef=appMethods.uploadFiles(uploadedList);
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Documents successfully uploaded."
			 		+ "Your document ref number is "+docRef));
			 this.uploadedList=new ArrayList<UploadedFile>();}
			
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


	public String getImageToDisplay() {
		return imageToDisplay;
	}


	public void setImageToDisplay(String imageToDisplay) {
		this.imageToDisplay = imageToDisplay;
	}


	public UploadedFile getUploadedFile4() {
		return uploadedFile4;
	}


	public void setUploadedFile4(UploadedFile uploadedFile4) {
		this.uploadedFile4 = uploadedFile4;
	}
	
	
	
	
	
	
	
	
	
	
	

	
	
}
