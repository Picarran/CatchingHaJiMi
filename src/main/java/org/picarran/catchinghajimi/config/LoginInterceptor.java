package org.picarran.catchinghajimi.config;

import org.picarran.catchinghajimi.entity.UserDO;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String uri = request.getRequestURI();
        // allow public
        if(uri.startsWith("/user/") || uri.startsWith("/api/game/") && uri.contains("/state")) return true;
        HttpSession session = request.getSession(false);
        if(session==null){ response.sendRedirect("/user/login"); return false; }
        UserDO u = (UserDO) session.getAttribute("loginUser");
        if(u==null){ response.sendRedirect("/user/login"); return false; }
        return true;
    }
}
