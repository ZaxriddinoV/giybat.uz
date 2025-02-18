package giybat.uz.post.repository;

import giybat.uz.post.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity,Integer>, PagingAndSortingRepository<PostEntity,Integer> {

    Optional<PostEntity> findByIdAndVisibleTrue(Integer id);

    @Query("FROM PostEntity  p WHERE p.visible = TRUE ")
    Page<PostEntity> getAll(PageRequest pageRequest);

    @Query("FROM PostEntity as p WHERE p.user.id = ?1 AND p.visible = TRUE")
    Page<PostEntity> getPosts(PageRequest pageRequest, Integer currentUserId);
}
