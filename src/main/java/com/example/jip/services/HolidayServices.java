package com.example.jip.services;

import com.example.jip.entity.Holiday;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HolidayServices {

    private final String API_KEY = "YB3MpA2Ac8Md4T6nXREAjv7bd8i0G3Pg";
    private final String API_URL = "https://calendarific.com/api/v2/holidays";

    public List<Holiday> fetchHolidays(String country, String year, String startDate, String endDate) throws JSONException {
        List<Holiday> holidays = new ArrayList<>();
        String url = API_URL + "?api_key=" + API_KEY + "&country=" + country + "&year=" + year;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray holidayArray = jsonResponse.getJSONObject("response").getJSONArray("holidays");

        for (int i = 0; i < holidayArray.length(); i++) {
            JSONObject holidayObj = holidayArray.getJSONObject(i);
            String holidayDate = holidayObj.getJSONObject("date").getString("iso");
            String holidayName = holidayObj.getString("name");

            // Validate the date
            if (holidayDate != null && !holidayDate.isEmpty()) {
                try {
                    java.sql.Date sqlDate = java.sql.Date.valueOf(holidayDate);

                    // Filter based on the start and end dates
                    if (sqlDate.toString().compareTo(startDate) >= 0 && sqlDate.toString().compareTo(endDate) <= 0) {
                        Holiday holiday = new Holiday();
                        holiday.setDate(sqlDate);
                        holiday.setName(holidayName);
                        holidays.add(holiday);
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid date format: " + holidayDate);
                }
            }
        }
        return holidays;
    }

}
