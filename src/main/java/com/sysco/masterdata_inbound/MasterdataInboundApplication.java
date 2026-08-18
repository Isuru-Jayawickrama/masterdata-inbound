package com.sysco.masterdata_inbound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MasterdataInboundApplication {

	public static void main(String[] args) {
		SpringApplication.run(MasterdataInboundApplication.class, args);
	}

}