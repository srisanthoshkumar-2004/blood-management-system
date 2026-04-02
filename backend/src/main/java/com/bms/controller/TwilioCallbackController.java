package com.bms.controller;

import com.bms.entity.DonorResponse;
import com.bms.service.BloodRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/twilio")
@RequiredArgsConstructor
@Slf4j
public class TwilioCallbackController {

    private final BloodRequestService bloodRequestService;

    @PostMapping(value = "/callback", produces = MediaType.APPLICATION_XML_VALUE)
    public String handleTwilioCallback(
            @RequestParam UUID requestId,
            @RequestParam UUID donorId,
            @RequestParam(required = false) String Digits) {
        
        log.info("Received Twilio callback for Request: {}, Donor: {}, Digits: {}", requestId, donorId, Digits);

        String txmlResponse;
        
        if ("1".equals(Digits)) {
            // YES
            String result = bloodRequestService.handleDonorResponse(requestId, donorId, DonorResponse.ResponseStatus.YES);
            txmlResponse = "<Response><Say voice=\"Polly.Aditi\" language=\"en-IN\">" + escapeXml(result) + "</Say></Response>";
        } else if ("2".equals(Digits)) {
            // NO
            bloodRequestService.handleDonorResponse(requestId, donorId, DonorResponse.ResponseStatus.NO);
            txmlResponse = "<Response><Say voice=\"Polly.Aditi\" language=\"en-IN\">Response recorded. Thank you for your time. Goodbye.</Say></Response>";
        } else {
            txmlResponse = "<Response><Say voice=\"Polly.Aditi\" language=\"en-IN\">Invalid input received. Goodbye.</Say></Response>";
        }

        return txmlResponse;
    }

    /** Sanitize user-supplied text before embedding in TwiML XML to prevent injection */
    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                     .replace("<", "&lt;")
                     .replace(">", "&gt;")
                     .replace("\"", "&quot;")
                     .replace("'", "&apos;");
    }
}
