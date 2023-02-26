package com.issahar.expenses.filter

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletResponse

@Component
@Order(3)
class CorsFilter : Filter {
    override fun doFilter(req: ServletRequest?, res: ServletResponse, chain: FilterChain) {
        val response: HttpServletResponse = res as HttpServletResponse
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, HEAD")
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Max-Age", "3600")
        chain.doFilter(req, res)
    }

    override fun init(filterConfig: FilterConfig?) {}
    override fun destroy() {}
}