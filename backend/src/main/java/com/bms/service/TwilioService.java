package com.bms.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isEmpty() && authToken != null && !authToken.isEmpty()) {
            Twilio.init(accountSid, authToken);
        }
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        if (isConfigured()) {
            String formattedNumber = formatPhoneNumber(toPhoneNumber);
            try {
                Message.creator(
                        new PhoneNumber(formattedNumber),
                        new PhoneNumber(fromPhoneNumber),
                        messageBody
                ).create();
            } catch (Exception e) {
                System.err.println("Failed to send Twilio SMS to " + formattedNumber + ": " + e.getMessage());
            }
        } else {
            System.out.println("MOCK SMS to " + toPhoneNumber + ": " + messageBody);
        }
    }

    public void makeEmergencyCall(String toPhoneNumber, String messageBody, java.util.UUID requestId, java.util.UUID donorId, String serverUrl) {
        if (isConfigured()) {
            String formattedNumber = formatPhoneNumber(toPhoneNumber);
            try {
                // TwiML with Gather for DTMF input
                String actionUrl = serverUrl + "/api/twilio/callback?requestId=" + requestId + "&donorId=" + donorId;
                String twiml = "<Response>" +
                        "<Gather action=\"" + actionUrl + "\" numDigits=\"1\" timeout=\"10\">" +
                        "<Say voice=\"Polly.Aditi\" language=\"en-IN\">" + escapeXml(messageBody) + "</Say>" +
                        "</Gather>" +
                        "<Say voice=\"Polly.Aditi\" language=\"en-IN\">We did not receive any input. Goodbye.</Say>" +
                        "</Response>";
                
                Call.creator(
                        new PhoneNumber(formattedNumber),
                        new PhoneNumber(fromPhoneNumber),
                        new com.twilio.type.Twiml(twiml)
                ).create();
            } catch (Exception e) {
                System.err.println("Failed to make Twilio Emergency Call to " + formattedNumber + ": " + e.getMessage());
            }
        } else {
            System.out.println("MOCK EMERGENCY CALL to " + toPhoneNumber + " for Request: " + requestId + " saying: " + messageBody);
        }
    }

    public void makeCall(String toPhoneNumber, String messageBody) {
        if (isConfigured()) {
            String formattedNumber = formatPhoneNumber(toPhoneNumber);
            try {
                String twiml = "<Response><Say voice=\"Polly.Aditi\" language=\"en-IN\">" + escapeXml(messageBody) + "</Say></Response>";
                
                Call.creator(
                        new PhoneNumber(formattedNumber),
                        new PhoneNumber(fromPhoneNumber),
                        new com.twilio.type.Twiml(twiml)
                ).create();
            } catch (Exception e) {
                System.err.println("Failed to make Twilio Call to " + formattedNumber + ": " + e.getMessage());
            }
        } else {
            System.out.println("MOCK CALL to " + toPhoneNumber + " saying: " + messageBody);
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        String cleaned = phoneNumber.replaceAll("[^0-9+]", "");
        
        // If it's a standard 10-digit number, prepend +91 (assuming Indian context based on logs)
        if (cleaned.length() == 10 && !cleaned.startsWith("+")) {
            return "+91" + cleaned;
        }
        
        // If it doesn't start with +, add it (assuming user provided country code without +)
        if (!cleaned.startsWith("+")) {
            return "+" + cleaned;
        }
        
        return cleaned;
    }

    /** Sanitize user-supplied text before embedding in TwiML XML */
    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                     .replace("<", "&lt;")
                     .replace(">", "&gt;")
                     .replace("\"", "&quot;")
                     .replace("'", "&apos;");
    }

    private boolean isConfigured() {
        return accountSid != null && !accountSid.isEmpty() &&
                authToken != null && !authToken.isEmpty() &&
                fromPhoneNumber != null && !fromPhoneNumber.isEmpty();
    }
}
