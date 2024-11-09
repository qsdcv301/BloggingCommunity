package taehyeon.com.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name = "neighbor")
public class Neighbor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blog_id")
    private Long blogId;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany
    @JoinColumn(name = "blog_id", referencedColumnName = "id")
    private List<Blog> blog;

    @OneToMany
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private List<User> user;

}
