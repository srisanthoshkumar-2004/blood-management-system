package com.bms.service;

import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

    public String getResponse(String userQuery) {
        String lowerQuery = userQuery.toLowerCase();

        if (lowerQuery.contains("eligib") || lowerQuery.contains("who can donate")) {
            return "To be eligible to donate blood, you must be 18-40 years old, have a weight above 50kg, a normal hemoglobin level, and not have donated blood in the past 90 days.";
        }
        if (lowerQuery.contains("how to donate") || lowerQuery.contains("process")) {
            return "To donate blood: 1. Register as a Donor. 2. Request an eligibility check. 3. Ensure your availability is true. You will receive an SMS/Call when your blood is needed in an emergency.";
        }
        if (lowerQuery.contains("emergency") || lowerQuery.contains("need blood")) {
            return "If you need blood urgently, go to the 'Request Blood' tab, fill in the patient details, and our automated matching system will contact eligible donors in your location immediately.";
        }
        if (lowerQuery.contains("gap") || lowerQuery.contains("often")) {
            return "There must be a minimum gap of 90 days between blood donations to ensure your health and safety.";
        }
        if (lowerQuery.contains("hello") || lowerQuery.contains("hi") || lowerQuery.contains("hey")) {
            return "Hello! I am the Blood Management System Assistant. How can I help you today?";
        }

        return "I'm sorry, I don't understand your question. Please ask about eligibility, donation process, emergencies, or required gaps between donations.";
    }
}
