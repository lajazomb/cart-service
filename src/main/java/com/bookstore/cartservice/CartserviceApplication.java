package com.bookstore.cartservice;

import com.bookstore.authentication.JwtRoleInterceptor;
import com.bookstore.authentication.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CartserviceApplication {

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(CartserviceApplication.class, args);
	}


	@Bean
	public WebMvcConfigurer interceptorConfiguration() {
		return new WebMvcConfigurer() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(new JwtRoleInterceptor(environment))
						.addPathPatterns("/**");
			}

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:3000/")
						.allowCredentials(true)
						.allowedHeaders("*")
						.allowedMethods("*");
			}

		};
	}

}
