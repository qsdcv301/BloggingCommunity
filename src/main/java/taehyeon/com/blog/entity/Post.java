package taehyeon.com.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blog_id")
    private Long blogId;

    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column(name = "title")
    private String title;

    @Column(name = "summary")
    private String summary;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToMany
    @JoinColumn(name = "blog_id", referencedColumnName = "id")
    private List<Blog> blog;

}
