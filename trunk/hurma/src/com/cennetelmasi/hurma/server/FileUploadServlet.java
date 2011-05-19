package com.cennetelmasi.hurma.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		// parse the request to retrieve the individual items
		List<FileItem> items;
		try {
		items = upload.parseRequest(request);
		} catch (FileUploadException e) {
		throw new ServletException("File upload failed", e);
		}
		// handle the individual items appropriately
		for (FileItem item : items) {
		// code here to process the file
		System.out.println("item = " + item);
		}
	}

}
