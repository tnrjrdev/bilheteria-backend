package com.rk.bilheteria;

import com.rk.bilheteria.config.PaymentsProps;
import com.rk.bilheteria.config.PricingProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BilheteriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BilheteriaApplication.class, args);
	}

}
