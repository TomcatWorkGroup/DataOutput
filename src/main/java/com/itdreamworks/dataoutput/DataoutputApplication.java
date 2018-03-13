package com.itdreamworks.dataoutput;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.itdreamworks.dataoutput")
@EnableFeignClients
public class DataoutputApplication extends SpringBootServletInitializer {

//	@Bean
//	public EmbeddedServletContainerCustomizer containerCustomizer(){
//		return new EmbeddedServletContainerCustomizer() {
//			@Override
//			public void customize(ConfigurableEmbeddedServletContainer container) {
//				container.setSessionTimeout(1800);//单位为S
//			}
//		};
//	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(com.itdreamworks.dataoutput.DataoutputApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(com.itdreamworks.dataoutput.DataoutputApplication.class);
		//application.addListeners(new ApplicationStartup());
		application.run(args);
	}
}