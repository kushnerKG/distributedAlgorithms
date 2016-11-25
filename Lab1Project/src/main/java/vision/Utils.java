package vision;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.*;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Created by konstantin on 25.11.16.
 */
public class Utils {

    private static final String APPLICATION_NAME = "Lab1/1.0";
    private static final int MAX_RESULTS = 6;
    private static final String TEXT_DETECTION_IDENTIFIER = "TEXT_DETECTION";

    public static Vision getVisionService() throws IOException, GeneralSecurityException {
        GoogleCredential credential;
        credential = GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static Vision.Images.Annotate constructRequest(byte[] data, Vision vision) throws IOException {

        AnnotateImageRequest request =
                new AnnotateImageRequest().setImage(new Image().encodeContent(data))
                        .setFeatures(ImmutableList.of(new Feature()
                                .setType(TEXT_DETECTION_IDENTIFIER).setMaxResults(MAX_RESULTS)));

        return vision.images().annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
    }

    public static List<EntityAnnotation> parsingResponse(Vision.Images.Annotate annotate) throws IOException {
        BatchAnnotateImagesResponse batchResponse = annotate.execute();
        assert batchResponse.getResponses().size() == 1;
        AnnotateImageResponse response = batchResponse.getResponses().get(0);

        return response.getTextAnnotations();
    }

    public static String responseText(List<EntityAnnotation> res){

        StringBuilder stringBuilder = new StringBuilder();
        for (EntityAnnotation label : res) {
            stringBuilder.append(label.getDescription()).append("<br />");
        }

        return stringBuilder.toString();
    }
}
