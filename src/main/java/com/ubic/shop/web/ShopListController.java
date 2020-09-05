package com.ubic.shop.web;

import com.ubic.shop.config.LoginUser;
import com.ubic.shop.domain.Role;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.repository.UserRepository;
import com.ubic.shop.service.ShopListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ShopListController {

    private final ShopListService shopListService;
    private final UserRepository userRepository;

    @GetMapping("/carts")
    public String list(Model model, @LoginUser SessionUser user, HttpServletRequest request) {

//        if(user != null){
//            model.addAttribute("userName", user.getName());
//            model.addAttribute("shopLists", // List<ShopList>
//                    shopListService.findAllShopLists(user.getId()));
//        }

        Long clientId = -1L;
        if (user != null) {
            model.addAttribute("userName", user.getName());
            clientId = user.getId();
        } else { // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0, 5));
            clientId = nonMember.getId();
        }
        model.addAttribute("shopLists", // List<ShopList>
                shopListService.findAllShopLists(clientId));

        return "cart-list";
    }

//    @GetMapping("/carts/{id}")
//    public String detail(@PathVariable int id, Model model, @LoginUser SessionUser user){
//        if(user != null){
//            model.addAttribute("userName", user.getName());
//        }
//        return "cart-detail";
//    }

    private User getTempUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User nonMember = null;
        if (session.isNew()) {
            log.info("\nsession is new : " + session.getId());
            // user 생성
            nonMember = User.builder()
                    .name(session.getId())
                    .email(null)
                    .picture(null)
                    .role(Role.GUEST)
                    .build();
            userRepository.save(nonMember);
        } else { // 새로운 세션이 아니라면 기존 세션이 있다는 말이니까!
            nonMember = userRepository.findByName(session.getId());
        }
        return nonMember;
    }


}
