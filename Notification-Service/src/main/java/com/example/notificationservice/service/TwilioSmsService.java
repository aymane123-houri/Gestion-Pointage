package com.example.notificationservice.service;

import com.example.notificationservice.model.Employe;
import com.example.notificationservice.model.Horaire;
import com.example.notificationservice.model.Messages;
import com.example.notificationservice.model.Pointage;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService {

   /* @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    private static final Logger logger = LoggerFactory.getLogger(TwilioSmsService.class);
    private final Messages messages;

    public TwilioSmsService(Messages messages) {
        this.messages = messages;
    }

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public void sendSms(String to, String message) {
        if (to == null || to.isBlank() || message == null || message.isBlank()) {
            logger.warn("❌ Numéro de téléphone ou message vide. Envoi annulé.");
            return;
        }

        // Formatage du numéro (si nécessaire)
        String formattedPhone = formatPhoneNumber(to);
        if (!isValidPhoneNumber(formattedPhone)) {
            logger.error("❌ Numéro de téléphone invalide : {}", formattedPhone);
            return;
        }

        try {
            Message.creator(
                    new PhoneNumber(formattedPhone),
                    new PhoneNumber(twilioPhoneNumber),
                    message
            ).create();
            logger.info("✅ SMS envoyé avec succès à {}", formattedPhone);
        } catch (ApiException e) {
            logger.error("❌ Erreur lors de l'envoi du SMS à {} : {}", formattedPhone, e.getMessage());
        }
    }

    // Vérifie si le numéro est au bon format (E.164)
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\+212[5-7]\\d{8}"); // Numéros valides au Maroc
    }

    // Formatage en E.164 (+212)
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+212" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }

    // Envoi du message de notification d'absence à l'employé
    public void sendAbsenceSms(Employe employe, Pointage pointage, Horaire horaire) {
        String smsMessage = messages.getAbsenceSmsNotification(employe, pointage, horaire);
        sendSms(employe.getTelephone(), smsMessage);
    }
*/
}
