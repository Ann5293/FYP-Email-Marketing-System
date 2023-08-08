package com.ms.email.marketing.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

public class CustomHandlerInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(CustomHandlerInterceptor.class);

    public static final String ATTRIBUTE_UUID = "customUUID";
    private final String LOG_VIEW_KEYWORD_START = "REQ  ---> ";
    private final String LOG_VIEW_KEYWORD_END = "RESP ---> ";
    private final String LOG_VIEW_SPLITTER = " ==================== ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //return HandlerInterceptor.super.preHandle(request, response, handler);
        String customUUID = extractUUIDFromRequest(request);
        request.setAttribute(ATTRIBUTE_UUID, customUUID);
        MDC.put(ATTRIBUTE_UUID, customUUID);
        logRequestInfo(request, response, LOG_VIEW_KEYWORD_START);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        logRequestInfo(request, response, LOG_VIEW_KEYWORD_END);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        //log.info("removeUUID: "+MDC.get(ATTRIBUTE_UUID));
        MDC.remove(ATTRIBUTE_UUID);
    }

    private String extractUUIDFromRequest(HttpServletRequest request) {
        String uuid = null;
        try {
            Object existUUID = request.getAttribute(ATTRIBUTE_UUID);
            if (existUUID == null || existUUID.toString().isEmpty()) {
                UUID newUUID = UUID.randomUUID();
                //log.info("newUUID: "+newUUID);
                uuid = newUUID.toString();
            }
        } catch (Exception e) {
            log.error("extractUUIDFromRequest ERROR: " + e);
        }
        return uuid;
    }

    private final void logRequestInfo(HttpServletRequest request, HttpServletResponse response, String logType) {
        if (LOG_VIEW_KEYWORD_START.equalsIgnoreCase(logType)) {
            String requestString = new StringBuilder()
                    .append(LOG_VIEW_SPLITTER)
                    .append(LOG_VIEW_KEYWORD_START)
                    .append(request.getMethod()).append(" ")
                    .append(request.getRequestURI()).append("    ")
                    .append(LOG_VIEW_SPLITTER)
                    .toString();
            log.info(requestString);
        } else if (LOG_VIEW_KEYWORD_END.equalsIgnoreCase(logType)) {
            String responseString = new StringBuilder()
                    .append(LOG_VIEW_SPLITTER)
                    .append(LOG_VIEW_KEYWORD_END)
                    .append(request.getMethod()).append(" ")
                    .append(request.getRequestURI()).append(" ")
                    .append(response.getStatus())
                    .append(LOG_VIEW_SPLITTER)
                    .toString();
            log.info(responseString);
        }
    }
}
