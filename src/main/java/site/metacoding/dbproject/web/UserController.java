package site.metacoding.dbproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class UserController {

    // 회원가입 페이지 (정적) -로그인x
    @GetMapping("joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    // 회원가입 - 로그인 x
    @PostMapping("/join")
    public String join() {

        return "redirection/user/loginForm"; // 로그인 페이지로 이동해주는 컨트롤러 메서드를 재활용
    }

    // 로그인 페이지 (정적) - 로그인 x
    @GetMapping("loginForm")
    public String loginForm() {
        return "user/loginForm";

    }

    // select * from user where username=? and passweord=?
    // 원래 select는 무조건 get 요청
    // 그런데 로그인만 예외(post)
    // 이유: 주소에 페스워들르 남길 수 없으니까!!

    // 회원가입 - 로그인x
    @PostMapping("/login")
    public String login() {
        return "메인피이지 돌려주면 된다"; // PostController 만들고 수정하자!
    }

    // 유저 정보 상세 페이지(동적) - 로그인 O 인증 필요
    @GetMapping("/user/{id}")
    public String detail(@PathVariable Integer id) {
        return "user/detail";

    }

    // 유저 정보 수정 페이지(동적) - 로그인 O 인증 필요
    @GetMapping("/user/{id}/updateForm")
    public String updateForm(@PathVariable Integer id) {
        return "user/detail";

    }

    // 유저 수정 - 로그인 O 인증 필요
    @PutMapping("/user/{id}")
    public String update(@PathVariable Integer id) {
        return "redirect:/user/" + id;

    }

    // 로그아웃 - 로그인 O
    @GetMapping("/logout")
    public String logout() {
        return " 메인 페이지로 돌려주면 됨";

    }

}