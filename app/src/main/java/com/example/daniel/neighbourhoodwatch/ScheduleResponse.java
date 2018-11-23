package com.example.daniel.neighbourhoodwatch;

public class ScheduleResponse {


    private String date;
    private String startTime;
    private String endTime;

    public ScheduleResponse(){}

    ScheduleResponse( String date, String startTime, String endTime) {

        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
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



