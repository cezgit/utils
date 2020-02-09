package com.wds.util.io;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * usage: new DownloadResponseUtil("text/csv; charset=utf-8").sendResponse(response, content, "FILE_NAME.csv");
 */
public class DownloadResponseUtil {

    private String contentType;

    public DownloadResponseUtil(){
        contentType = "application/octet-stream";
    }

    public DownloadResponseUtil(String contentType) {
        this.contentType = contentType;
    }

    public void sendResponse(HttpServletResponse response, String contentAsString, String fileName) throws IOException {
        sendResponse(response, contentAsString.getBytes(), fileName);
    }
    
    public void sendResponse(HttpServletResponse response, byte[] contents, String fileName) throws IOException {
    	response.reset();
        response.setContentType(contentType);
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\""+ fileName +"\"");
        
        response.getOutputStream().write(contents);
    }


}
