package ru.nsu.ccfit.kushner.da.lab1;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by konstantin on 11.11.16.
 */


/*@MultipartConfig(fileSizeThreshold=1024*1024*10, // 2MB
        maxFileSize=1024*1024*10,      // 10MB
        maxRequestSize=1024*1024*50)   // 50MB*/
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
                        // данные были считаны - есть, что записать
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

                //fos.write(sCert.getBytes());


                request.setAttribute("message", data.length);

                getServletContext().getRequestDispatcher("/uploadPage.jsp").forward(
                        request, response);

                /*if (item.isFormField()) {
                    System.out.println("Form field " + name + " with value "
                            + Streams.asString(stream) + " detected.");
                } else {
                    System.out.println("File field " + name + " with file name "
                            + item.getName() + " detected.");
                    // Process the input stream

                }*/
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }



        /*if (!ServletFileUpload.isMultipartContent(request)) {

            PrintWriter writer = response.getWriter();
            writer.println("Error: 表单必须包含 enctype=multipart/form-data");
            writer.flush();
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();

        factory.setSizeThreshold(MEMORY_THRESHOLD);


        //factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        factory.setRepository(null);

        ServletFileUpload upload = new ServletFileUpload(factory);


        upload.setFileSizeMax(MAX_FILE_SIZE);


        upload.setSizeMax(MAX_REQUEST_SIZE);

        String uploadPath = getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;


        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try {
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);

            if (formItems != null && formItems.size() > 0) {
                for (FileItem item : formItems) {
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
                        System.out.println(filePath);
                        item.write(storeFile);
                        request.setAttribute("message", "Upload has been done successfully!");
                    }
                }
            }
        } catch (Exception ex) {
            request.setAttribute("message",
                    "ERROR: " + ex.getMessage());
        }

        getServletContext().getRequestDispatcher("/uploadPage.jsp").forward(
                request, response);
    }*/





/*    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {




//        String appPath = request.getServletContext().getRealPath("");
//
//        // constructs path of the directory to save uploaded file
//        String savePath = appPath + File.separator + SAVE_DIR;

        for (Part part : request.getParts()) {
            //String fileName = extractFileName(part);
            //part.write(savePath + File.separator + fileName);
            InputStream inputStream = part.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024*1024];
            while (true){
                // читаем данные в буфер
                int readBytesCount = inputStream.read(buffer);
                if (readBytesCount == -1) {
                    // данные закончились
                    break;
                }
                if (readBytesCount > 0) {
                    // данные были считаны - есть, что записать
                    baos.write(buffer, 0, readBytesCount);
                }
            }
            baos.flush();
            baos.close();
            byte[] data = baos.toByteArray();

//            FileOutputStream fos = new FileOutputStream(new File(savePath + File.separator +
//                    fileName.substring(0, fileName.lastIndexOf(".")) + ".txt"));
//            String sCert = javax.xml.bind.DatatypeConverter.printBase64Binary(data);
//
//            fos.write(sCert.getBytes());

        }

        request.setAttribute("message", "Upload has been done successfully!");
        getServletContext().getRequestDispatcher("/uploadPage.jsp").forward(
                request, response);
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
