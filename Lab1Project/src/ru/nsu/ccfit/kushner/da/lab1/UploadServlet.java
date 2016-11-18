package ru.nsu.ccfit.kushner.da.lab1;

import com.google.api.services.vision.v1.Vision;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ru.nsu.ccfit.kushner.da.lab1.vision.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.*;
import java.security.GeneralSecurityException;

/**
 * Created by konstantin on 11.11.16.
 */

public class UploadServlet extends HttpServlet {


    private static final String UPLOAD_DIRECTORY = "upload";


    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {


        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload();

// Parse the request
        FileItemIterator iter = null;
        try {
            iter = upload.getItemIterator(request);

            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String fileName = item.getName();
                String name = item.getFieldName();
                InputStream stream = item.openStream();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024*1024];
                while (true){
                    // читаем данные в буфер
                    int readBytesCount = stream.read(buffer);
                    if (readBytesCount == -1) {
                        // данные закончились
                        break;
                    }
                    if (readBytesCount > 0) {
                        // данные были считаны - есть, чтоx записать
                        baos.write(buffer, 0, readBytesCount);
                    }
                }
                baos.flush();
                baos.close();
                byte[] data = baos.toByteArray();


                String uploadPath = getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;


                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
//
//                String filePath = uploadPath + File.separator + fileName.substring(0, fileName.lastIndexOf(".")) + ".txt";
//                File storeFile = new File(filePath);
//
//                FileOutputStream fos = new FileOutputStream(storeFile);
                String sCert = javax.xml.bind.DatatypeConverter.printBase64Binary(data);

                Vision vision = Utils.getVisionService();
                //printLabels(System.out, imagePath, app.labelImage(imagePath, MAX_LABELS));


                //fos.write(sCert.getBytes());


                request.setAttribute("message", data.length);

                getServletContext().getRequestDispatcher("/uploadPage.jsp").forward(
                        request, response);
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }


    /**
     * Extracts file name from HTTP header content-disposition
     */
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return "";
    }
}
