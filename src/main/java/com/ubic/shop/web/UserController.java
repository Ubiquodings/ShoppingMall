package com.ubic.shop.web;

import com.ubic.shop.config.LoginUser;
import com.ubic.shop.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UserController {

    @GetMapping("/mypage")
    public String detail(Model model, @LoginUser SessionUser user){

        if(user != null){
            model.addAttribute("userName", user.getName());
        }

        return "mypage";
    }

}
