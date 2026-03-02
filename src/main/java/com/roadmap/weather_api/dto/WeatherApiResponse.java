package com.roadmap.weather_api.dto;

import java.util.List;

import lombok.Data;

@Data
public class WeatherApiResponse {
  private String address;
  private String description;
  private CurrentConditions currentConditions;
  private List<DaySummary> forecast;

  @Data
  public static class CurrentConditions {
    private double temp;
    private double feelslike;
    private double humidity;
    private double windspeed;
    private double uvindex;
    private String conditions;
    private String icon;
    private String sunrise;
    private String sunset;
  }

  @Data
  public static class DaySummary {
    private String datetime;
    private double tempmax;
    private double tempmin;
    private double temp;
    private double humidity;
    private double precipprob;
    private String conditions;
    private String description;
    private String icon;
  }
}
