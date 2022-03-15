package site.metacoding.dbproject.web;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import site.metacoding.dbproject.domain.user.User;
import lombok.RequiredArgsConstructor;
import site.metacoding.dbproject.domain.post.Post;
import site.metacoding.dbproject.domain.post.PostRepository;

@RequiredArgsConstructor // final이 붙은 애들에 대한 생성자를 만들어준다.
@Controller
public class PostController {

    private final HttpSession session;
    private final PostRepository postRepository;

    // GET 글쓰기 페이지 /post/writeForm - 인증 필요
    @GetMapping("/s/post/writeForm")
    public String writeForm() {

        if (session.getAttribute("principal") == null) {
            return "redirect:/loginForm";
        }

        return "post/writeForm";
    }

    // 메인페이지
    // GET 글목록 페이지 /post/list, / - 인증 필요x
    // @GetMapping({"/", "/post/list"})
    @GetMapping({ "/", "/post/list" })
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {

        // 1. POSTREPOSITORY의 FINDALL() 호출

        // 2. 모델에 담기
        // model.addAttribute("posts",
        // postRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        PageRequest pq = PageRequest.of(page, 3);
        model.addAttribute("posts", postRepository.findAll(pq));
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        return "post/list";

    }

    // 테스트 용
    // @GetMapping("/test/post/list")
    // public @ResponseBody Page<Post> listTest(@RequestParam(defaultValue = "0")
    // int page) {

    // // if (page==null){
    // // page=0; } 비추

    // PageRequest pq = PageRequest.of(page, 3);
    // return postRepository.findAll(pq);
    // }

    // GET 글상세보기 페이지 /post/{id} (삭제버튼 만들어 두면됨, 수정버튼 만들어 두면됨) - 인증 필요 x
    @GetMapping("/post/{id}") // get 요청에 /post 제외 시키기
    public String detail(@PathVariable Integer id, Model model) {
        Optional<Post> postOp = postRepository.findById(id);

        // if 보단 try catch가 좋음 commit log도 뜨게 해야함.

        if (postOp.isPresent()) {
            Post postEntity = postOp.get();
            model.addAttribute("post", postEntity);
            return "post/detail";

        } else {
            return "post/detail";
        }

    }

    // GET 글수정 페이지 /post/{id}/updateForm - 인증 필요
    @GetMapping("/s/post/{id}/updateForm")
    public String updateForm(@PathVariable Integer id) {
        return "post/updateForm"; // ViewResolver 도움 받음.
    }

    // DELETE 글삭제 /post/{id} - 글목록으로 가기 - 인증 필요
    @DeleteMapping("/s/post/{id}")
    public String delete(@PathVariable Integer id) {
        return "redirect:/";
    }

    // UPDATE 글수정 /post/{id} - 글상세보기 페이지가기 - 인증 필요
    @PutMapping("/s/post/{id}")
    public String update(@PathVariable Integer id) {
        return "redirect:/post/" + id;
    }

    // POST 글쓰기 /post - 글목록으로 가기 - 인증 필요
    @PostMapping("/s/post")
    public String write(Post post) {

        if (session.getAttribute("principal") == null) {
            return "redirect:/loginForm";
        }
        User principal = (User) session.getAttribute("principal");
        post.setUser(principal);
        // insert into post(title, content, userid) values(사용자, 사용자,세션오브젝트의 pk)
        postRepository.save(post);
        return "redirect:/";
    }
}