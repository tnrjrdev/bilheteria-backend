package com.rk.bilheteria.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rk")
public class RkProps {
    public Payments payments = new Payments();
    public Pricing pricing = new Pricing();

    public static class Payments { public String pixKey; public String merchant; public String city; }
    public static class Pricing  { public double male; public double female; }
}