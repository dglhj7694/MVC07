package kr.bit.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileAddController implements Controller {

	@Override
	public String requestHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String UPLOAD_DIR = "file_repo";
		String uploadPath = request.getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
		File currentDirPath = new File(uploadPath); // 업로드할 경로를 File객체로 만들기
		if (!currentDirPath.exists()) {
			currentDirPath.mkdir();
		}

		// 파일 업로드 시 저장될 임시 저장 경로 설정
		// file 업로드 시 필요한 API - commos-fileupload, commons-io
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024 * 1024);

		String fileName = null;

		ServletFileUpload upload = new ServletFileUpload(factory);
		try { // items -> [FileItem],[FileItem],[FileItem],[]...
			List<FileItem> items = upload.parseRequest(request); // request안의
																	// 여러개의 파일이
			// 업로드된 경우 file 정보를 쉽게
			// 읽어올 수 있게 한다.
			for (int i = 0; i < items.size(); i++) {
				FileItem fileItem = items.get(i);
				if (fileItem.isFormField()) { // 폼필드 이면
					System.out.println(fileItem.getFieldName() + " = " + fileItem.getString("utf-8"));
				} else { // 파일이면
					if (fileItem.getSize() > 0) {
						int idx = fileItem.getName().lastIndexOf("\\"); // window 일때 '\\' , linux /
						if (idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");
						}
						fileName = fileItem.getName().substring(idx + 1); // 파일이름
						File uploadFile = new File(currentDirPath + "\\" + fileName);
						// 파일 중복 체크
						if (uploadFile.exists()) {
							fileName = System.currentTimeMillis() + "_" + fileName;
							uploadFile = new File(currentDirPath + "\\" + fileName);
						}
						fileItem.write(uploadFile); // 임시경로에서 새로운 경로에 파일 저장
					}
				}
			} // for
		} catch (Exception e) {
			e.printStackTrace();
		}
		// .$ajax() 쪽으로 업로드된 최종 파일 이름을 전송 (db저장을 위함)
		response.setContentType("text/html;charset=euc-kr");
		response.getWriter().print(fileName);
		return null;
	}

}
