package com.roadmap.weather_api.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.roadmap.weather_api.dto.WeatherApiResponse;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class WeatherApiServiceImpl {

  private final RestTemplate restTemplate;
  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  @Value("${weather.api.key}")
  private String apiKey;

  public WeatherApiResponse getWeather(String city) {

    String cached = redisTemplate.opsForValue().get(city);
    if (cached != null)
      return objectMapper.readValue(cached, WeatherApiResponse.class);
    //
    //
    // cache miss
    String url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"
        + city + "?unitGroup=metric&contentType=json&key=" + apiKey;
    String raw = restTemplate.getForObject(url, String.class);
    WeatherApiResponse response = objectMapper.readValue(raw, WeatherApiResponse.class);

    // stor in cache with 12hour expiration
    redisTemplate.opsForValue().set(city, objectMapper.writeValueAsString(response), 12, TimeUnit.HOURS);
    return response;
  }
}
