package com.ubic.shop.web;

import com.ubic.shop.config.LoginUser;
import com.ubic.shop.domain.Order;
import com.ubic.shop.domain.Role;
import com.ubic.shop.domain.ShopList;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.repository.CouponRepository;
import com.ubic.shop.repository.UserRepository;
import com.ubic.shop.service.OrderService;
import com.ubic.shop.service.ShopListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ShopListService shopListService;
    private final CouponRepository couponRepository;

    @GetMapping("/orders")
    public String list(Model model, @LoginUser SessionUser user, HttpServletRequest request) { // 화면 :: 채민

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
                orderService.findAllOrdered(clientId));


        return "order-list";
    }

    //TODO
    @GetMapping("/orders/{id}") /*아마 지금 이거 사용안할걸*/
    public String detail(@PathVariable int id, Model model, @LoginUser SessionUser user) {
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "order-detail";
    }

    @RequestMapping(value="/payment", method = {RequestMethod.GET, RequestMethod.POST})
    public String payment(Model model, @LoginUser SessionUser user, HttpServletRequest request) {
        Long clientId = -1L;
        if (user != null) {
            model.addAttribute("userName", user.getName());
            clientId = user.getId();
        } else { // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0, 5));
            clientId = nonMember.getId();
        }

        //주문 누른 상품 모두 가져오기.
        List<Order> allOrdered = orderService.findAllOrdered(clientId);
        model.addAttribute("shopLists", // List<ShopList>
                allOrdered);

        /*List<Long> idList = allOrdered.stream()
                .map(m -> m.getOrderProducts().getId()) // 각 장바구니 아이템의 상품 아이디 가져오기
                .collect(Collectors.toList());

        // 상품 아이디 기반으로 쿠폰 가져오기
        model.addAttribute("couponList", couponRepository.findByIds(idList));*/

        return "payment";
    }

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