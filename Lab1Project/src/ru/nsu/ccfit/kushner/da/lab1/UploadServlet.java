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

import java.io.*;
import java.security.GeneralSecurityException;

/**
 * Created by konstantin on 11.11.16.
 */

public class UploadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        ServletFileUpload upload = new ServletFileUpload();

        FileItemIterator iterator;
        try {
            iterator = upload.getItemIterator(request);

            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                InputStream stream = item.openStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024*1024];
                while (true){
                    int readBytesCount = stream.read(buffer);
                    if (readBytesCount == -1) {
                        break;
                    }
                    if (readBytesCount > 0) {
                        baos.write(buffer, 0, readBytesCount);
                    }
                }
                baos.flush();
                baos.close();
                byte[] data = baos.toByteArray();

                Vision.Images.Annotate annotate = Utils.constructRequest(data, Utils.getVisionService());
                String str = Utils.responseText(Utils.parsingResponse(annotate));
                request.setAttribute("message", str);

                getServletContext().getRequestDispatcher("/uploadPage.jsp").forward(
                        request, response);
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
