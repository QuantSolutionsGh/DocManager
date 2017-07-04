package com.databank.gh.docmanager.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Transactional;

public class dummy extends JdbcDaoSupport implements IDocManagerMethods,Serializable {
	
	private static final long serialVersionUID = -933602118117202939L;
	
	public String storagePath;

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	@Override
	public String uploadFiles(List<UploadedFile> uploadedFiles) throws Exception {
		int docRef = this.getDocRef();
		for (UploadedFile item : uploadedFiles) {
			// check if file is a pdf if it is output to remote disk,create
			// individual images and upload

			if (item.getFileName().toUpperCase().contains(".PDF")) {
				uploadPDF(item, docRef);
			} else
				uploadPic(item, docRef);

			// uploadFile(item);
			// this.storeInDb(docRef, item.getFileName());
		}
		return String.valueOf(docRef);
	}

	private void uploadPic(UploadedFile _file, int docRef) throws Exception {
		String filename = FilenameUtils.getName(_file.getFileName());
		InputStream input = _file.getInputstream();
		OutputStream output = new FileOutputStream(new File(storagePath, filename));

		try {
			IOUtils.copy(input, output);
			this.storeInDb(docRef, _file.getFileName());
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}

	}

	public void uploadPDF(UploadedFile _file, int docRef) throws Exception {

		PDDocument document = PDDocument.load(_file.getInputstream());
		
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		int pageCounter = 0;
		for (PDPage page : document.getPages()) {
			// note that the page number parameter is zero based
			BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter, 300, ImageType.RGB);
			
			
			String fileName = _file.getFileName() + "-" + (pageCounter++) + ".jpeg";
			OutputStream output = new FileOutputStream(new File(storagePath, fileName));
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
			if (!writers.hasNext())
				throw new IllegalStateException("No writers found");
			ImageWriter writer = (ImageWriter) writers.next();
			
			ImageOutputStream ios = ImageIO.createImageOutputStream(output);
			
			writer.setOutput(ios);
			
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(0.1f);
			writer.write(null, new IIOImage(bim, null, null), param);
			 this.storeInDb(docRef, fileName);
			
			
			output.close();
			ios.close();
			writer.dispose();
			
			
			
			//just a thought we can also upload original image as well
			
			
			
			
				
				
				
				
				
			// suffix in filename will be used as the file format
		//	String fileName = _file.getFileName() + "-" + (pageCounter++) + ".png";
		//	OutputStream output = new FileOutputStream(new File(storagePath, fileName));
			
		//	ImageIOUtil.writeImage(bim, "jpg", output, 300);
			// this.storeInDb(docRef, item.getFileName());
			
			

			

		}
		document.close();

	}

	@Override
	public String uploadFile(UploadedFile filename) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getImages(String docRef) {
		String sql = "select file_name from tbl_main2 where doc_ref=?";
		List<String> fileList = new ArrayList<String>();
		List<Map<String, Object>> rows = this.getJdbcTemplate().queryForList(sql, docRef);

		for (Map row : rows) {

			fileList.add((String) row.get("file_name"));

		}
		return fileList;
	}

	

	@Override
	public void pushFile(UploadedFile item,int docRef) throws Exception {
		if (item.getFileName().toUpperCase().contains(".PDF")) {
			uploadPDF(item, docRef);
		} else
			uploadPic(item, docRef);
		
	}

	@Override
	@Transactional
	public int getDocRef() {
		String sql = "select max(doc_ref)+1 from sequencer2";
		int docRef = this.getJdbcTemplate().queryForObject(sql, Integer.class);
		this.getJdbcTemplate().update("update sequencer2 set doc_ref=?", docRef);
		return docRef;
	}

	@Override
	@Transactional
	public void storeInDb(int docRef, String fileName) {
		String sql = "insert into tbl_main2(doc_ref,file_name) values (?,?)";
		this.getJdbcTemplate().update(sql, new Object[] { docRef, fileName });
		
	}

}
