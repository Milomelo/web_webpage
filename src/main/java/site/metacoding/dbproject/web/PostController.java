package site.metacoding.dbproject.web;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import site.metacoding.dbproject.domain.user.User;
import site.metacoding.dbproject.service.PostService;
import site.metacoding.dbproject.web.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import site.metacoding.dbproject.domain.post.Post;

@RequiredArgsConstructor // final이 붙은 애들에 대한 생성자를 만들어준다.
@Controller
public class PostController {

    private final HttpSession session;
    private final PostService postService;

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

        Page<Post> pagePosts = postService.글목록보기(page);
        // 1. POSTREPOSITORY의 FINDALL() 호출

        // 2. 모델에 담기
        // model.addAttribute("posts",
        // postRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));

        model.addAttribute("posts", pagePosts);
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

    // GET 글 상세보기 페이지 / post/{id} (삭제버튼 만들어 두면됨. 수정버튼 만들어 두면됨) - 인증 X
    @GetMapping("/post/{id}") // 인증이 필요없기 때문에 주소는 이대로 놔둬야 한다. 따라서 주소는 놔두고 Get요청시에는 필터에서 /post 거르는거를 제외를 시켜준다.
    public String detail(@PathVariable Integer id, Model model) {

        User principal = (User) session.getAttribute("principal");

        Post postEntity = postService.글상세보기(id);

        // 게시불이 없으면 error 페이지로 이동
        if (principal != null) {
            // 권한 확인해서 view로 값을 넘김.
            if (principal.getId() == postEntity.getUser().getId()) { // 권한 있음
                model.addAttribute("pageOwner", true);
            } else {
                model.addAttribute("pagrOwner", false);
            }
        }
        String rawContent = postEntity.getContent();
        String encContent = rawContent.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        postEntity.setContent(encContent);

        model.addAttribute("post", postEntity);
        return "post/detail";

    }

    // GET 글수정 페이지 /post/{id}/updateForm - 인증 필요
    @GetMapping("/s/post/{id}/updateForm")
    public String updateForm(@PathVariable Integer id, Model model) {

        // 인증
        User principal = (User) session.getAttribute("principal");
        if (principal == null) {
            return "error/page1";
        }

        // 권한
        Post postEntity = postService.글상세보기(id);

        if (postEntity.getUser().getId() != principal.getId()) {
            return "error/page1";
        }

        model.addAttribute("post", postEntity);

        return "post/updateForm"; // ViewResolver 도움 받음.
    }

    // DELETE 글삭제 /post/{id} - 글목록으로 가기 - 인증 필요
    @DeleteMapping("/s/post/{id}")
    public @ResponseBody ResponseDto<String> delete(@PathVariable Integer id, HttpServletResponse response) {

        User principal = (User) session.getAttribute("principal");

        if (principal == null) {
            return new ResponseDto<String>(-1, "로그인안됨 ", null);
        }

        Post postEntity = postService.글상세보기(id);
        if (principal.getId() != postEntity.getUser().getId()) {
            return new ResponseDto<String>(-1, "해당 글을 삭제할 권한이 없습니다", null);
            // 심풀 이즈 붸스트
        }
        postService.글삭제하기(id); // 내부적으로 exception이 터지면 무조건 스택 트레이스를 리턴한다.

        return new ResponseDto<String>(1, "성공", null);
    }

    // UPDATE 글수정 /post/{id} - 글상세보기 페이지가기 - 인증 필요
    @PutMapping("/s/post/{id}")
    public @ResponseBody ResponseDto<String> update(@PathVariable Integer id, @RequestBody Post post) {
        // 인증
        User principal = (User) session.getAttribute("principal");
        if (principal == null) {
            return new ResponseDto<String>(-1, "로그인이 되지 않았습니다.", null);
        }

        // 권한
        Post postEntity = postService.글상세보기(id);

        if (postEntity.getUser().getId() != principal.getId()) {
            return new ResponseDto<String>(-1, "해당 게시글을 수정할 권한이 없습니다.", null);
        }

        postService.글수정하기(post, id);

        return new ResponseDto<String>(1, "수정 성공.", null);

    }

    // POST 글쓰기 /post - 글목록으로 가기 - 인증 필요
    @PostMapping("/s/post")
    public String write(Post post) {

        if (session.getAttribute("principal") == null) {
            return "redirect:/loginForm";
        }
        User principal = (User) session.getAttribute("principal");
        postService.글쓰기(post, principal);

        return "redirect:/";
    }
}