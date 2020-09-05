package com.ubic.shop.web;

import com.ubic.shop.config.LoginUser;
import com.ubic.shop.domain.Role;
import com.ubic.shop.domain.User;
import com.ubic.shop.dto.SessionUser;
import com.ubic.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
@Slf4j
public class IndexController {

    //    private final SessionRepository sessionRepository; // 주입해주나 ?
    private final UserRepository userRepository;

//    private final FindByIndexNameSessionRepository sessionRepository;
    // 서비스 계층으로 리포지토리 옮길 때 트랜잭션 처리도 가져가기!
//    private final JdbcIndexedSessionRepository sessionRepository; // 세션을 사용하려면 id 가 있어야 하잖아!
//    String session_id = "BROWSER_ID";

//    @Resource
//    private NonMember nonMember;

    @Transactional
    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user, /*HttpSession session, */
                        HttpServletRequest request) {

        if (user != null) {
            model.addAttribute("userName", user.getName());
        } else { // 해시코드 다섯글자만 추출하기
            User nonMember = getTempUser(request);
            model.addAttribute("clientId", nonMember.getName().substring(0, 5));
        }

        return "index";
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
