package com.ubic.shop.web;

import com.ubic.shop.config.LoginUser;
import com.ubic.shop.domain.Payment;
import com.ubic.shop.domain.Role;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.repository.CouponRepository;
import com.ubic.shop.repository.UserRepository;
import com.ubic.shop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @GetMapping("/payment")
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

        List<Payment> allPayments = paymentService.findAllPayments(clientId);

        model.addAttribute("allPayments", allPayments);

        List<Long> idList = allPayments.stream()
                .map(m -> m.getProduct().getId()) // 각 장바구니 아이템의 상품 아이디 가져오기
                .collect(Collectors.toList());
//        log.info("\nidList: "+)

        // 상품 아이디 기반으로 쿠폰 가져오기
        model.addAttribute("couponList", couponRepository.findByIds(idList));

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
