package kr.bit.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileGetController implements Controller {

	@Override
	public String requestHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String filename = request.getParameter("filename");
		System.out.println(filename);

		String UPLOAD_DIR = "file_repo";
		String uploadPath = request.getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
		File f = new File(uploadPath + "\\" + filename);

		// 클라이언트로 부터 넘어오는 파일이름에 한글 있는경우 한글 깨짐 방지
		filename = URLEncoder.encode(filename, "UTF-8");
		filename = filename.replace("+", " "); // 크롬의 경우 공백에 '+' 가 들어간다

		// 파일 다운로드 준비 (서버 > 클라이언트 에게 다운로드 준비 _ 다운로드 창 띄우기)
		response.setContentLength((int) f.length());
		response.setContentType("application/x-msdownload;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename + ";"); // attachment 다운로드할 파일을 보이게함
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		// 실제 다운로드
		FileInputStream in = new FileInputStream(f); // 파일 읽기 준비
		OutputStream out = response.getOutputStream(); //
		byte[] buffer = new byte[1024];
		while (true) {
			int count = in.read(buffer);
			if (count == -1) {
				break;
			}
			out.write(buffer, 0, count); // 실제 다운로드
		} // _while

		in.close();
		out.close();

		return null;
	}

}
