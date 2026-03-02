package com.roadmap.weather_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.roadmap.weather_api.dto.WeatherApiResponse;
import com.roadmap.weather_api.service.WeatherApiServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/weather")
public class WeatherApiController {

  private final WeatherApiServiceImpl weatherApiService;

  @GetMapping
  ResponseEntity<WeatherApiResponse> getWeather(@RequestParam String city) {
    WeatherApiResponse response = weatherApiService.getWeather(city);

    return ResponseEntity.status(200).body(response);
  }
}
