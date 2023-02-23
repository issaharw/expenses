package com.ultrasight.cloud.filter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order

@Component
@Order(2)
class LoggingFilter: Filter {
    companion object {
        private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)
    }

    override fun init(filterConfig: FilterConfig?) {
        logger.info("Initiating Logging filter")
    }

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val request = servletRequest as HttpServletRequest
        val response = servletResponse as HttpServletResponse
        if (!request.requestURI.startsWith("/health")) {
            logger.info("(${request.getParameter("username")}) ${request.method} ${request.requestURI} (Received)")
        }
        filterChain.doFilter(request, response)
        if (!request.requestURI.startsWith("/health")) {
            logger.info("(${request.getParameter("username")}) ${request.method} ${request.requestURI} ${response.status}")
        }
    }

    override fun destroy() {}
}