package com.ubic.shop.web;

import com.ubic.shop.config.LoginUser;
import com.ubic.shop.domain.Role;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.repository.UserRepository;
import com.ubic.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @GetMapping("/orders")
    public String list(Model model, @LoginUser SessionUser user, HttpServletRequest request){ // 화면 :: 채민

//        if(user != null){
//            model.addAttribute("userName", user.getName());
//            model.addAttribute("shopLists", // List<Order> -- Order :: createdDate
////                    orderService.findAllOrders(user.getId()) // ordered/canceled 모두 포함
//                    orderService.findAllOrdered(user.getId()) // ordered 만 포함
//            ); // order status 가 order 인 것만 가져와야 겠는데 ?
//        }

        Long clientId = -1L;
        if(user != null){
            model.addAttribute("userName", user.getName());
            clientId = user.getId();
        }else{ // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0,5));
            clientId = nonMember.getId();
        }
        model.addAttribute("shopLists", // List<ShopList>
                orderService.findAllOrdered(clientId) );


        return "order-list";
    }

    //TODO
    @GetMapping("/orders/{id}") /*아마 지금 이거 사용안할걸*/
    public String detail(@PathVariable int id, Model model, @LoginUser SessionUser user){
        if(user != null){
            model.addAttribute("userName", user.getName());
        }
        return "order-detail";
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
