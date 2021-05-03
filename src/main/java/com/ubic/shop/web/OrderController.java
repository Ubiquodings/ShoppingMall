package com.ubic.shop.web;

import com.ubic.shop.config.LoginUser;
import com.ubic.shop.domain.Order;
import com.ubic.shop.domain.Role;
import com.ubic.shop.domain.ShopList;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.repository.CouponRepository;
import com.ubic.shop.repository.ProductRepository;
import com.ubic.shop.repository.UserRepository;
import com.ubic.shop.service.OrderService;
import com.ubic.shop.service.ShopListService;
import com.ubic.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    private final ProductRepository productRepository;
    private final UserService userService;

    @GetMapping("/orders")
    public String list(Model model, @LoginUser SessionUser user, HttpServletRequest request) { // 화면 :: 채민

//        if(user != null){
//            model.addAttribute("userName", user.getName());
//            model.addAttribute("shopLists", // List<Order> -- Order :: createdDate
////                    orderService.findAllOrders(user.getId()) // ordered/canceled 모두 포함
//                    orderService.findAllOrdered(user.getId()) // ordered 만 포함
//            ); // order status 가 order 인 것만 가져와야 겠는데 ?
//        }

        Long clientId = -1L;
        if (user != null) {
            model.addAttribute("userName", user.getName());
            clientId = user.getId();
            userService.updateLastActivatedTime(clientId);
        } else { // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0, 5));
            clientId = nonMember.getId();
        }
        model.addAttribute("userId", clientId);

        List<Order> allOrdered = orderService.findAllOrdered(clientId);
        log.info("\nordered size: "+allOrdered.size());
        model.addAttribute("orderList", // List<Order>
                allOrdered);

//        model.addAttribute("buyTogetherList", // List<Order>
//                productRepository.findProductsByLimit(4L));

        return "order-list";
    }

    //TODO
    @GetMapping("/orders/{id}") //아마 지금 이거 사용안할걸
    public String detail(@PathVariable int id, Model model, @LoginUser SessionUser user) {
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "order-detail";
    }

    /*@GetMapping("/payment")
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

        // 장바구니에서 모든 아이템 가져오기
        List<ShopList> allShopList = shopListService.findAllShopLists(clientId);
        model.addAttribute("allShopList", allShopList);

        List<Long> idList = allShopList.stream()
                .map(m -> m.getProduct().getId()) // 각 장바구니 아이템의 상품 아이디 가져오기
                .collect(Collectors.toList());
//        log.info("\nidList: "+)

        // 상품 아이디 기반으로 쿠폰 가져오기
        model.addAttribute("couponList", couponRepository.findByIds(idList));

        return "payment";
    }*/

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
