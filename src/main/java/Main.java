import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static credentials.Credentials.getCredentials;


public class Main {
    /**
     *
     * Google cloud api - with Google account
     * Enable Google API in the clout account -> create project
     * Add deps: google oauth, google api services, google api client
     * Get client credentials
     *
     */



    public static void main(String[] args) throws GeneralSecurityException, IOException {

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        Gmail service = new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
                .setApplicationName("Test Mailer")
                .build();

        Gmail.Users.Messages.List request = service.users().messages().list("me");
        ListMessagesResponse listMessagesResponse = request.execute();

        List<Message> messages = listMessagesResponse.getMessages();
        messages.forEach(message -> {
            try {
                Message message1 = service.users().messages().get("me", message.getId()).execute();
                String body = StringUtils.newStringUtf8(
                        Base64.decodeBase64(
                                message1.getPayload().getParts().get(0).getBody().getData()
                        )
                );
                System.out.println(body);
                System.out.println("----------------------------------------------------------------------------------------------");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
