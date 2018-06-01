package com.itdreamworks.dataoutput;

import com.itdreamworks.security.DeCoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.itdreamworks.dataoutput")
public class DataoutputApplication extends WebMvcConfigurerAdapter {
	@Value("${web.app.gl}")
	private String uploadPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
		registry.addResourceHandler("/app/gl/**").addResourceLocations(
				"file:"+uploadPath);
	}
	public static void main(String[] args) throws Exception {
		SpringApplication.run(DataoutputApplication.class, args);
		//System.out.println(DeCoder.DeCode("858878e888"));
	}
}