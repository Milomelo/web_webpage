package site.metacoding.dbproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class PostController {

    // GET 글쓰기 페이지 /post/writeForm - 인증 필요
    @GetMapping("/s/post/writeForm")
    public String writeForm() {
        return "post/writeForm";
    }

    // 메인페이지
    // GET 글목록 페이지 /post/list, / - 인증 필요x
    // @GetMapping({"/", "/post/list"})
    @GetMapping({ "/", "/post/list" })
    public String list() {
        return "post/list";
    }

    // GET 글상세보기 페이지 /post/{id} (삭제버튼 만들어 두면됨, 수정버튼 만들어 두면됨) - 인증 필요 x
    @GetMapping("/post/{id}") // get 요청에 /post 제외 시키기
    public String detail(@PathVariable Integer id) {
        return "post/detail";
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
    public String write() {
        return "redirect:/";
    }
}