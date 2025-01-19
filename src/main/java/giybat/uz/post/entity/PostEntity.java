package giybat.uz.post.entity;

import giybat.uz.profile.entity.ProfileEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "photo_id")
    private String photoId;
    @Column(name = "content",columnDefinition = "TEXT")
    private String content;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "visible")
    private Boolean visible;
    @ManyToOne
    private ProfileEntity user;
}
