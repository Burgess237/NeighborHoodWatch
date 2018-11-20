package com.example.daniel.neighbourhoodwatch;

public class ScheduleResponse {

    private String name;
    private String date;
    private String startTime;
    private String endTime;

    public ScheduleResponse(){}

    ScheduleResponse(String name, String date, String startTime, String endTime) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    String getName() {
        return name;
    }

    String getDate() {
        return date;
    }

    String getStartTime() {
        return startTime;
    }

    String getEndTime() {
        return endTime;
    }

}



