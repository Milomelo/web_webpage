package site.metacoding.dbproject.web;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.qos.logback.core.joran.conditional.ElseAction;
import site.metacoding.dbproject.domain.user.User;
import site.metacoding.dbproject.domain.user.UserRepository;

@Controller
public class UserController {

    // 컴포지션 (의존성 연결)
    private UserRepository userRepository;
    private HttpSession session;

    // DI 받는 코드!!
    public UserController(UserRepository userRepository, HttpSession session) {
        this.userRepository = userRepository;
        this.session = session;
    }

    // 회원가입 페이지 (정적) - 로그인X
    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    // username=ssar&password=12347email=ssar@nate.com (x-www-form)
    // 회원가입 - 로그인X
    @PostMapping("/join")
    public String join(User user) {

        // 1. null 값 먼저 확인
        // 2, 공백 넣는거 방지
        System.out.println("User : " + user);

        // 밑에 꺼는 들어오기 전에 차단하는 방법 근데 이건 사실상 힘들다. 이유: 마트에 강도가 들어올 때 소지품 검사도 다 했지만 강도가 주먹으로
        // 강도짓을 할 수 있음.
        // try catch 를 통해 터진 후에 잡는 것이 훨씬 좋다.
        if (user.getPassword() == null || user.getEmail() == null || user.getUsername() == null) {
            System.out.println("제대로 입력되지 않았습니다.");

            return "redirect:/joinForm";
        }
        if (user.getPassword().length() > 12) {
            System.out.println("비밀번호가 12자리가 초과되었습니다.");

            return "redirect:/joinForm";
        }
        if (user.getPassword().equals(" ") || user.getEmail().equals(" ") || user.getUsername().equals(" ")) {
            return "redirect:/joinForm";
        } else {
            User userEntity = userRepository.save(user);
            System.out.println("userEntity : " + userEntity);
            return "redirect:/loginForm";// 로그인페이지 이동해주는 컨트롤러 메서드를 재활용

        }
    }

    // 로그인 페이지 (정적) - 로그인X
    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    // SELECT * FROM user WHERE username=? AND password=?
    // 원래 SELECT는 무조건 get요청
    // 그런데 로그인만 예외(POST)
    // 이유 : 주소에 패스워드를 남길 수 없으니까!!
    // 로그인 - 로그인X
    @PostMapping("/login")
    public String login(HttpServletRequest request, User user) {

        System.out.println("사용자로 부터 받은 username. password:" + user);
        HttpSession session = request.getSession(); // 쿠키에 85를 들고 오면 85 영역을 찾아감!!
        User userEntity = userRepository.mLogin(user.getUsername(), user.getPassword());

        if (userEntity == null) {
            System.out.println("아이디 혹은 패스워드가 틀렸습니다.");

        } else {
            System.out.println("로그인이 되었습니다.");
            session.setAttribute("principal", userEntity);
        }
        // 1. db연결해서 username, password 있는지 확인
        // 2. 있으면 session 영역에 인증됨 이라고 메세지 하나 남겨두기
        return "redirect:/";// PostController 만들고 수정하자.
    }

    // http://localhost:8080/user/1
    // 유저상세 페이지 (동적) - 로그인O

    @GetMapping("/user/{id}")
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

        // 3. 핵심로직
        Optional<User> userOp = userRepository.findById(id);
        // 경고창 같은게 있으면 좋다!!

        if (userOp.isPresent()) {
            User userEntity = userOp.get();
            model.addAttribute("user", userEntity);
            return "user/detail";

        } else {

            return "error   /page1";
        }
    }

    // 유저수정 페이지 (동적) - 로그인O
    @GetMapping("/user/{id}/updateForm")
    public String updateForm(@PathVariable Integer id) {
        return "user/updateForm";
    }

    // 유저수정 - 로그인O
    @PutMapping("/user/{id}")
    public String update(@PathVariable Integer id) {
        return "redirect:/user/" + id;
    }

    // 로그아웃 - 로그인O
    @GetMapping("/logout")
    public String logout() {
        return "메인페이지를 돌려주면 됨";// PostController 만들고 수정하자.
    }
}