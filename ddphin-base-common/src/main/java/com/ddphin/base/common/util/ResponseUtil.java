package com.ddphin.base.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ResponseUtil {	
	private final static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);
	
	public static void writer(HttpServletResponse response, String message) {
		OutputStream os = null;
		try {	
			byte[] bytes = message.getBytes("UTF-8");
			
			os = response.getOutputStream();
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Expires", "0");
			response.setHeader("Pragma", "No-cache");
			response.setContentType("text/json");
			response.setCharacterEncoding("UTF-8");
			response.setContentLength(bytes.length);
			
			os.write(bytes);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}		
	}
	    
	public static Boolean writeResult(String result, HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		Boolean success = false;
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.print(result);
			writer.flush();
			success = true;
		} catch (IOException e) {
			logger.error("writeResult", e);
		} finally {
			if (null != writer) {
				writer.close();
			}
		}
		return success;
	}
}
