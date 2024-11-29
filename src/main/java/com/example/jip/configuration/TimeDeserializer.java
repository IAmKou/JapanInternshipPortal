package com.example.jip.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.sql.Time;

public class TimeDeserializer extends JsonDeserializer<Time> {

    @Override
    public Time deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String timeString = p.getText().trim();

        // Handle case where the time string is empty or null
        if (timeString.isEmpty() || timeString.equals("null")) {
            return null; // Return null if the time field is empty or not set
        }

        // Check if the string length is valid for HH:mm format
        if (timeString.length() == 5) {
            timeString += ":00";  // Add seconds if missing
        }

        try {
            // Return a Time object after validation
            return Time.valueOf(timeString);
        } catch (IllegalArgumentException e) {
            // Log or handle invalid time format
            throw new IOException("Invalid time format: " + timeString, e);
        }
    }
}
