package com.arminzheng.inflation.util;

import com.arminzheng.inflation.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor  implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        if(user == null) {
            response.setStatus(401);
            return false;
        }
        UserHolder.saveUser((UserDTO) user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
