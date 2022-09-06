package com.agprogramming.seom_v2_bff.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SeomClientInterceptor {
	private static final String AUTHORIZATION_HEADER = "Authorization";

    public static String getBearerTokenHeader() {
      return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader(AUTHORIZATION_HEADER);
    }
}
