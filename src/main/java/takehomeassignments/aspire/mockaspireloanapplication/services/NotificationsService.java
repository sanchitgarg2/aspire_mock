package takehomeassignments.aspire.mockaspireloanapplication.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import takehomeassignments.aspire.mockaspireloanapplication.entities.UserEntity;
import takehomeassignments.aspire.mockaspireloanapplication.enums.NotificationChannel;
import takehomeassignments.aspire.mockaspireloanapplication.exceptions.UserNotNotifiedError;

@AllArgsConstructor
@Service
@Slf4j
public class NotificationsService {

    private final UserService userService;


    public void sendNotification(String message, String userId, NotificationChannel channel){
        //Send notification to the user
        switch (channel){

            case EMAIL -> {
                try{
                    String userEmail = getUserEmail(userId);
                    sendNotificationViaEmail(userEmail, message);
                } catch (UserNotNotifiedError e) {
                    //Handle how you want to... Possible exceptions could be that the email is invalid, or it has been blocked etc
                    log.info("notifyOverEmail failed for userId " + userId);
                    //Possible fallback is to schedule a call with the user.
                    sendNotificationViaPhoneCall(userId,message);
                }
            }
            case TEXT_MESSAGE -> {
                try{
                    String userPhoneNumber = getUserPhoneNumber(userId);
                    sendNotificationViaSMS(userPhoneNumber, message);
                } catch (UserNotNotifiedError e) {
                    //Handle how you want to... Possible exceptions could be that the email is invalid, or it has been blocked etc
                    log.info("notifyOverSMS failed for userId " + userId);
                    //Possible fallback is to schedule a call with the user.
                    sendNotificationViaPhoneCall(userId,message);
                }
            }
            case PHONE_CALL -> sendNotificationViaPhoneCall(userId, message);
        }
    }

    private String getUserPhoneNumber(String userId) {
        //Simple implementation here.
        //This might get complicated if the scale of the system demands that the user service be a separate microservice
        return userService.getUserPhoneNumber(userId);
    }

    private String getUserEmail(String userId){
        //Simple  implementation here.
        //This might get complicated if the scale of the system demands that the user service be a separate microservice
        return userService.getUserEmail(userId);
    }

    public void sendNotificationViaSMS(String userPhoneNumber, String messsage) throws UserNotNotifiedError {
        try{
            //Manages the integration with the 3P SMS provider
            StringBuilder request = new StringBuilder();
            request.append(userPhoneNumber);
            request.append(":");
            request.append(messsage);
            log.info("Notify user over SMS " + request);
        }catch (Exception e){
            //Raise a message to the system status monitor that either the partner API is unhealthy or the integration is broken somehow
            throw new UserNotNotifiedError();
        }
        return;
    }

    public void sendNotificationViaEmail(String userEmail, String message) throws UserNotNotifiedError {
        //Manages the integration with the 3P Email provider
        try{
            //Manages the integration with the 3P SMS provider
            StringBuilder request = new StringBuilder();
            request.append(userEmail);
            request.append(":");
            request.append(message);
            log.info("Send Email to user " + request);
        }catch (Exception e){
            //Raise a message to the system status monitor that either the partner API is unhealthy or the integration is broken somehow
            throw new UserNotNotifiedError();
        }
        return;
    }

    public void sendNotificationViaPhoneCall(String userId, String message){
        //Maintain a message queue of requests for phone calls...
        //Include all relevant information in the request, so that the human is best equipped to handle the case


        // Use userId instead of userPhone number to better secure the PII data of the user.
        log.info("Queue Message to phoneCallQueue message - " + userId + " : " + message);
        //messageQueue.publishMessage(String userDetails, String message);

    }

}
