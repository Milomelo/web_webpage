package site.metacoding.dbproject.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//postrepository는 post 오브젝트만 db로부터 리턴받을 수 있다.
@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

}
