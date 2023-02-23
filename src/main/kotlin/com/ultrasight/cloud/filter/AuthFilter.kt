package com.ultrasight.cloud.filter

import com.ultrasight.cloud.dao.UserDao
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import jakarta.inject.Inject
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment

private const val AI_SERVER_USERNAME = "quality-results@ultrasight.com"
@Component
@Order(1)
class AuthFilter @Inject constructor(private val userDao: UserDao, private val environment: Environment): Filter {
    companion object {
        private val logger = LoggerFactory.getLogger(AuthFilter::class.java)
    }

    override fun init(filterConfig: FilterConfig?) {
        logger.info("Initiating Auth filter")
    }

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val request = servletRequest as HttpServletRequest
        val response = servletResponse as HttpServletResponse
        if (isLocal()) {
            request.setAttribute("userId", 0)
            filterChain.doFilter(request, response)
            return
        }

        if (request.requestURI.startsWith("/health")) {
            filterChain.doFilter(request, response)
            return
        }

        val username = request.getParameter("username") ?: request.getHeader("x-username")
        if (username.isNullOrEmpty()) {
            logger.error("username wasn't sent to the server.")
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Username wasn't sent to the server")
            return
        }

        if (username == AI_SERVER_USERNAME){
            request.setAttribute("userId", -41)
            filterChain.doFilter(request, response)
            return
        }

        val user = userDao.getUserByEmail(username)
        if (user?.isActive == true) {
            request.setAttribute("userId", user.id)
            filterChain.doFilter(request, response)
        }
        else {
            logger.warn("User with email '$username' doesn't exit or is not active")
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User with email '$username' doesn't exit or is not active")
        }
    }

    override fun destroy() {}

    private fun isLocal() = "local" in environment.activeProfiles
}