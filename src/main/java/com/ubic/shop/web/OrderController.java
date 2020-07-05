package com.ubic.shop.web;

import com.ubic.shop.config.LoginUser;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public String list(Model model, @LoginUser SessionUser user){ // 화면 :: 채민
        if(user != null){
            model.addAttribute("userName", user.getName());
            model.addAttribute("shopLists", // List<Order> -- Order :: createdDate
//                    orderService.findAllOrders(user.getId()) // ordered/canceled 모두 포함
                    orderService.findAllOrdered(user.getId()) // ordered 만 포함
            ); // order status 가 order 인 것만 가져와야 겠는데 ?
        }
        return "order-list";
    }

    @GetMapping("/orders/{id}")
    public String detail(@PathVariable int id, Model model, @LoginUser SessionUser user){
        if(user != null){
            model.addAttribute("userName", user.getName());
        }
        return "order-detail";
    }

}
