package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import takehomeassignments.aspire.mockaspireloanapplication.enums.NotificationChannel;
import takehomeassignments.aspire.mockaspireloanapplication.exceptions.UserNotNotifiedError;
import takehomeassignments.aspire.mockaspireloanapplication.services.NotificationsService;
import takehomeassignments.aspire.mockaspireloanapplication.services.UserService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class NotificationsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationsService notificationsService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendNotificationEmail() throws UserNotNotifiedError {
        String userId = "userId";
        String message = "message";
        NotificationChannel channel = NotificationChannel.EMAIL;
        String userEmail = "useremail@website.com";

        when(userService.getUserEmail(userId)).thenReturn(userEmail);

        notificationsService.sendNotification(message, userId, channel);

        verify(userService, times(1)).getUserEmail(userId);
    }

    @Test
    public void testSendNotificationTextMessage() throws UserNotNotifiedError {
        String userId = "userId";
        String message = "message";
        NotificationChannel channel = NotificationChannel.TEXT_MESSAGE;
        String userPhone = "9876543210";

        when(userService.getUserPhoneNumber(userId)).thenReturn(userPhone);
        notificationsService.sendNotification(message, userId, channel);
        verify(userService, times(1)).getUserPhoneNumber(userId);
    }

    @Test
    public void testSendNotificationPhoneCall() {
        String userId = "userId";
        String message = "message";
        NotificationChannel channel = NotificationChannel.PHONE_CALL;
        String userPhone = "9876543210";

        notificationsService.sendNotification(message, userId, channel);

        //When the channel is PHONE_CALL, the method should publish the message to the Queue that drives phone calls to the user.

    }

    @Test
    public void testGetUserPhoneNumber() {
        String userId = "userId";
        String phoneNumber = "1234567890";

        when(userService.getUserPhoneNumber(userId)).thenReturn(phoneNumber);

        String returnedPhoneNumber = userService.getUserPhoneNumber(userId);

        assertEquals(phoneNumber, returnedPhoneNumber);
    }

    @Test
    public void testGetUserEmail() {
        String userId = "userId";
        String email = "user@example.com";

        when(userService.getUserEmail(userId)).thenReturn(email);

        String returnedEmail = userService.getUserEmail(userId);

        assertEquals(email, returnedEmail);
    }

    @Test
    public void testSendNotificationViaSMS() {
        String phoneNumber = "1234567890";
        String message = "message";

        assertDoesNotThrow(() -> notificationsService.sendNotificationViaSMS(phoneNumber, message));
    }

    @Test
    public void testSendNotificationViaEmail() {
        String email = "user@example.com";
        String message = "message";

        assertDoesNotThrow(() -> notificationsService.sendNotificationViaEmail(email, message));
    }

    @Test
    public void testSendNotificationViaPhoneCall() {
        String userId = "userId";
        String message = "message";

        assertDoesNotThrow(() -> notificationsService.sendNotificationViaPhoneCall(userId, message));
    }
}