package org.picarran.catchinghajimi.controller;

import org.picarran.catchinghajimi.entity.UserDO;
import org.picarran.catchinghajimi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/user/login")
    public String doLogin(HttpServletRequest req, String username, String password, Model model) {
        UserDO u = userService.loginByUsername(username, password);
        if (u == null) {
            model.addAttribute("error", "用户名或密码错误");

            return "login";
        }
        HttpSession session = req.getSession();
        session.setAttribute("loginUser", u);
        return "redirect:/dashboard";
    }

    @GetMapping("/user/register")
    public String regPage() {
        return "register";
    }

    @PostMapping("/user/register")
    public String doRegister(String username, String password, String nickname) {
        userService.register(username, password, nickname);
        return "redirect:/user/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        UserDO u = (UserDO) session.getAttribute("loginUser");
        model.addAttribute("user", u);
        return "dashboard";
    }
}
