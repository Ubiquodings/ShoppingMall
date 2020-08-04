package com.ubic.shop.web;

import com.ubic.shop.config.LoginUser;
import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.domain.Role;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.repository.UserRepository;
import com.ubic.shop.service.ProductService;
import com.ubic.shop.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final RecommendService recommendService;
    private final UbicConfig ubicConfig;
    private final UserRepository userRepository;
//    private

    @GetMapping("/products")
    public String list(Model model, @LoginUser SessionUser user,
                       @RequestParam(name = "page", defaultValue = "0") String page, HttpServletRequest request){ // 화면 :: 윤진

//        User nonMember = getTempUser(request);

        // 사용 (age, offset=page, limit=40)
        // 정렬은 어떻게 하지 ? name 디폴트로 하고 정렬 라디오 박스 추가하면 되겠다 TODO
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page), ubicConfig.productListPageSize, Sort.by(Sort.Direction.DESC, "name"));
//        productService.findPagingProducts(pageRequest);

//        model.addAttribute("products", productService.findAllProducts(/*PageRequest.of(0,20)*/));
        model.addAttribute("products", productService.findPagingProducts(pageRequest)); // 40개씩 페이징

        if(user != null){
            model.addAttribute("userName", user.getName());
        }else{ // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0,5));
        }

        return "product-list";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model, @LoginUser SessionUser user,
                         @RequestParam(name = "page", defaultValue = "0") String page,
                         HttpServletRequest request){

        model.addAttribute("product", productService.findById(id));


        String clientId = null;
        long userId = -1L;

        if(user != null){
            model.addAttribute("userName", user.getName());
            clientId = user.getId().toString();
            userId = user.getId();
        }else{ // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0,5));
            clientId = nonMember.getName();
            userId = nonMember.getId();
        }

        model.addAttribute("userId", userId);
                model.addAttribute("recommendedList", recommendService.getRecommendList(clientId, page));
        return "product-detail";
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
