package ru.nsu.ccfit.kushner.da.lab1.vision;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;



import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.model.*;
import com.google.common.collect.ImmutableList;


import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Created by konstantin on 18.11.16.
 */
public class Utils {

    private static final String APPLICATION_NAME = "Lab1/1.0";
    private static final int MAX_RESULTS = 1;
    /**
     * Connects to the Vision API using Application Default Credentials.
     */
    public static Vision getVisionService() throws IOException, GeneralSecurityException {
        GoogleCredential credential =
                GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    public static Vision.Images.Annotate constructRequest(byte[] data, Vision vision) throws IOException {
        //byte[] data = Files.readAllBytes(path);

        AnnotateImageRequest request =
                new AnnotateImageRequest()
                        .setImage(new Image().encodeContent(data))
                        .setFeatures(ImmutableList.of(
                                new Feature()
                                        .setType("TEXT_DETECTION").setMaxResults(MAX_RESULTS)));

        return vision.images()
                        .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
    }


    //Parsing the Response

    public static List<EntityAnnotation> parsingResponse(Vision.Images.Annotate annotate) throws IOException {
        BatchAnnotateImagesResponse batchResponse = annotate.execute();
        assert batchResponse.getResponses().size() == 1;
        AnnotateImageResponse response = batchResponse.getResponses().get(0);

        return response.getTextAnnotations();
    }

    public static void printLabels(PrintStream out, List<EntityAnnotation> labels) {

        for (EntityAnnotation label : labels) {
            out.printf(
                    "\t%s (score: %.3f)\n",
                    label.getDescription(),
                    label.getScore());
        }
        if (labels.isEmpty()) {
            out.println("\tNo labels found.");
        }
    }

}
