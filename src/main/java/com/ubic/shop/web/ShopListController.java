package com.ubic.shop.web;

import com.ubic.shop.config.LoginUser;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.service.ShopListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class ShopListController {

    private final ShopListService shopListService;

    @GetMapping("/carts")
    public String list(Model model, @LoginUser SessionUser user){

        if(user != null){
            model.addAttribute("userName", user.getName());
            model.addAttribute("shopLists", // List<ShopList>
                    shopListService.findAllShopLists(user.getId()));
        }
        return "cart-list";
    }

//    @GetMapping("/carts/{id}")
//    public String detail(@PathVariable int id, Model model, @LoginUser SessionUser user){
//        if(user != null){
//            model.addAttribute("userName", user.getName());
//        }
//        return "cart-detail";
//    }



}
