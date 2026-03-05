package com.spring.semi.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * InterceptorConfiguration - 공통 처리(AOP).
 */
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer
{
	@Autowired
	private AdminInterceptor adminInterceptor;
	@Autowired
	private BoardViewInterceptor boardViewInterceptor;
	@Autowired
	private MemberLoginInterceptor memberLoginInterceptor;
	@Autowired
	private NotificationCountInterceptor notificationCountInterceptor;


	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		registry.addInterceptor(notificationCountInterceptor)
		.addPathPatterns("/**")
		.excludePathPatterns(
				"/css/**", "/js/**", "/image/**", "/summernote/**",
				"/media/**", "/rest/**", "/error/**", "/favicon.ico"
		)
		.order(20);

		registry.addInterceptor(boardViewInterceptor)
		.addPathPatterns("/board/**/detail")
		.order(10);

		registry.addInterceptor(memberLoginInterceptor)
		.addPathPatterns("/board/**", "/member/**", "/rest/**", "/animal/**", "/notification/**")
		.excludePathPatterns(
				"/board/**/detail", "/board/**/list",
				"/member/detail", "/**/login",
				"/member/join**", "/member/find**",
				"/**/profile", "/rest/main/**",
				"/**/image"
				)
		.order(2);

		registry.addInterceptor(adminInterceptor)
		.addPathPatterns("/admin/**")
		.order(1);
	}
}
