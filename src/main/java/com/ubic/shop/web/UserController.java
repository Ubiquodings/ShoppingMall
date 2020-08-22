package com.ubic.shop.web;

import com.ubic.shop.config.LoginUser;
import com.ubic.shop.domain.Role;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.repository.CouponRepository;
import com.ubic.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @GetMapping("/mypage")
    public String detail(Model model, @LoginUser SessionUser user, HttpServletRequest request){

        if(user != null){
            model.addAttribute("userName", user.getName());
        }else{ // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0,5));
        }

        return "mypage";
    }

    @GetMapping("/my-coupons")
    public String coupons(Model model, @LoginUser SessionUser user, HttpServletRequest request){

        Long userId=-1L;
        if(user != null){
            model.addAttribute("userName", user.getName());
            userId = user.getId();
        }else{ // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0,5));
            userId = nonMember.getId();
        }

//        TODO 전달해야 한다!
        model.addAttribute("couponList", couponRepository.findByUserId(userId));

        return "mycoupons";
    }


    private User getTempUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User nonMember=null;
        if(session.isNew()){
            log.info("\nsession is new : "+session.getId());
            // user 생성
            nonMember = User.builder()
                    .name(session.getId())
                    .email(null)
                    .picture(null)
                    .role(Role.GUEST)
                    .build();
            userRepository.save(nonMember);
        }else{ // 새로운 세션이 아니라면 기존 세션이 있다는 말이니까!
            nonMember = userRepository.findByName(session.getId());
        }
        return nonMember;
    }

}