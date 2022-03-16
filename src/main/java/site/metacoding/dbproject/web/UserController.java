package site.metacoding.dbproject.web;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import site.metacoding.dbproject.domain.user.User;
import site.metacoding.dbproject.service.UserService;
import site.metacoding.dbproject.web.dto.ResponseDto;

@Controller
@RequiredArgsConstructor
public class UserController {

    // 컴포지션 (의존성 연결)
    private final UserService userService;
    private final HttpSession session;

    // DI 받는 코드

    // http://localhost:8080/api/user/username/same-check?username=?
    // user의 username이 동일한지 확인해줄래? - 응답 (json)
    // api는 데이터를 준다는 프로그래머들 사이에서의 약속
    @GetMapping("/api/user/username/same-check")
    public @ResponseBody ResponseDto<String> sameCheck(String username) {
        String result = userService.유저네임중복검사(username);
        return new ResponseDto<String>(1, "통신성공", result);

    }

    // 회원가입 페이지 (정적) - 로그인X
    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    // username=김수현&password=&email=suhyeon5028@naver.com 패스워드 공백
    // username=김수현&email=suhyeon5028@naver.com 패스워드 null
    // username=김수현&password=1234&email=suhyeon5028@naver.com (x-www-form)
    // 회원가입 - 로그인X
    @PostMapping("/join")
    public String join(User user) {

        // 1. username, password, email 1.null체크, 2.공백체크
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            return "redirect:/joinForm";
        }
        if (user.getUsername().equals("") || user.getPassword().equals("") || user.getEmail().equals("")) {
            return "redirect:/joinForm";
        } // 필터의 역할

        userService.회원가입(user);

        return "redirect:/loginForm"; // 로그인 페이지로 이동해주는 컨트롤러 메서드를 재활용
    }

    // 로그인 페이지 (정적) - 로그인X
    @GetMapping("/loginForm")
    public String loginForm(HttpServletRequest request, Model model) {
        // JSESSIONID=asidaisdjasdi1233;remember=ssar
        // request.getHeader("Cookie");
        Cookie[] cookies = request.getCookies(); // JSESSIONID,remember 2개가 있음. 내부적으로 split해준것.
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("remember")) {
                    model.addAttribute("remember", cookie.getValue());
                }
            }
        }
        return "user/loginForm";
    }

    // SELECT * FROM user WHERE username=? AND password=?
    // 원래 SELECT는 무조건 get요청
    // 그런데 로그인만 예외 (POST)
    // 이유 : 주소에 패스워드를 남길 수 없으니까!
    // 로그인X
    @PostMapping("/login")
    public String login(User user, HttpServletResponse response) {

        User userEntity = userService.로그인(user);

        if (userEntity != null) {
            session.setAttribute("principal", userEntity);
            if (user.getRemember() != null && user.getRemember().equals("on")) {
                response.addHeader("Set-Cookie", "remember=" + user.getUsername());
            }
            return "redirect:/";
        } else {
            return "redirect:/loginForm";
        }

    }

    // 로그아웃 - 로그인O
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/loginForm";
    }

    // http://localhost:8080/user/1
    // 회원정보상세 페이지 (동적) - 로그인O
    @GetMapping("/s/user/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        User principal = (User) session.getAttribute("principal");

        // 1. 인증체크
        if (principal == null) {
            return "error/page1";
        }

        // 2. 권한체크
        if (principal.getId() != id) {
            return "error/page1";
        }

        User userEntity = userService.유저정보보기(id);
        if (userEntity == null) {
            return "error/page1";
        } else {
            model.addAttribute("user", userEntity);
            return "user/detail";
        }

    }

    // 회원정보수정 페이지 (동적) - 로그인O
    @GetMapping("/s/user/updateForm")
    public String updateForm() {
        // 세션값을 출력햇는데, 원래는 디비에서 가져와야함!!
        return "user/updateForm";
    }

    // username(X), password(O), email(O)
    // password=1234&email=ssar@nate.com (x-www-form-urlencoded)
    // { "password" : "1234", "email" : "ssar@nate.com" } (application/json)
    // json을 받을 것이기 때문에 Spring이 데이터 받을 때 파싱전략을 변경!!
    // put 요청은 Http Body가 있다. Http Header의 content- Type에 MIME타입을 알려줘야한다.
    // 회원정보 수정완료 - 로그인O
    // @RequestBody -> BufferReader +Json 파싱
    // @ResponseBody -> Bufferwriter +Json 파싱
    @PutMapping("/s/user/{id}")
    public @ResponseBody ResponseDto<String> update(@PathVariable Integer id, @RequestBody User user) {

        User principal = (User) session.getAttribute("principal");

        // 1. 인증 체크 -컨트롤러권한
        if (principal == null) {
            return new ResponseDto<String>(-1, "인증안됨", null);
        }

        // 2. 권한체크 -컨트롤러권한
        if (principal.getId() != id) {
            return new ResponseDto<String>(-1, "권한없어", null);
        }
        User userEntity = userService.유저수정(id, user);
        session.setAttribute("principal", userEntity);

        return new ResponseDto<String>(1, "성공", null);
    }

}
