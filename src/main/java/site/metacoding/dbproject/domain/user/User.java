package site.metacoding.dbproject.domain.user;

import java.time.LocalDateTime;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// model과 repositroy를 구분하는건 옜날모델
// 지금은 domain으로 만듬
//JPA 라이브러리는 Java(자바 언어)로 Persistenece(영구적인 저장)을 제공하는 API(노출되어 있는 메서드)
//1. CRUD 메서드를 기본 제공
//2. 자바코드로 DB를 자동 생성 기능 제공
//3. ORM 제공!! == 이제부터 한다!!

@Entity // 서버 실행시 해당 클래스로 테이블을 생성하는 어노테이션
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class User {
    // IDENTITY 전략은 DB에 번호증가 전략을 위임하는 것!! - 알아서 디비에 맞게 해줌.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // primary key

    @Column(length = 20, unique = true)
    private String username;// ssar 아이디
    @Column(length = 12, nullable = false)
    private String password;

    @Column(length = 16000000)
    private String email;

    private LocalDateTime createDate;

}
