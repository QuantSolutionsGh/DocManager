package com.databank.gh.docmanager.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
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
		int docRef = getDocRef();
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

	public void uploadPic(UploadedFile _file, int docRef) throws Exception {
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

			// suffix in filename will be used as the file format
			OutputStream output = new FileOutputStream(new File(storagePath, _file.getFileName()));
			String fileName = _file.getFileName() + "-" + (pageCounter++) + ".png";
			ImageIOUtil.writeImage(bim, fileName, output, 300);

			this.storeInDb(docRef, fileName);

		}
		document.close();

	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	

	@Override
	public List<String> getImages(String docRef) {
		String sql = "select file_name from tbl_main where doc_ref=?";
		List<String> fileList = new ArrayList<String>();
		List<Map<String, Object>> rows = this.getJdbcTemplate().queryForList(sql, docRef);

		for (Map row : rows) {

			fileList.add((String) row.get("file_name"));

		}
		return fileList;
	}

	

	@Override
	public void pushFile(UploadedFile item, int docRef) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getDocRef() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void storeInDb(int docRef, String fileName) {
		// TODO Auto-generated method stub
		
	}

}
