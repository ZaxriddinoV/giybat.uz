package giybat.uz.post.repository;

import giybat.uz.post.dto.FilterDTO;
import giybat.uz.post.dto.FilterResultDTO;
import giybat.uz.post.entity.PostEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class CustomRepository {

    @Autowired
    private EntityManager entityManager;

    public FilterResultDTO<PostEntity> filter(FilterDTO filter, int page, int size) {
        StringBuilder conditionBuilder = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        if (filter.getName() != null) {
            conditionBuilder.append("and p.name ilike :name ");
            params.put("name", "%" + filter.getName() + "%");
        }
        if (filter.getSurname() != null) {
            conditionBuilder.append("and p.surname ilike :surname ");
            params.put("surname", "%" + filter.getSurname() + "%");
        }
        if (filter.getContent() != null) {
            conditionBuilder.append("and s.content ilike :content ");
            params.put("content", "%" + filter.getContent() + "%");
        }
        StringBuilder selectBuilder = new StringBuilder(
                "From PostEntity as s join s.user as p where 1=1 "
        );
        selectBuilder.append(conditionBuilder);

        StringBuilder countBuilder = new StringBuilder("select count(s) From PostEntity as s join s.user as p where 1=1 ");
        countBuilder.append(conditionBuilder);

        Query selectQuery = entityManager.createQuery(selectBuilder.toString(), PostEntity.class);
        Query countQuery = entityManager.createQuery(countBuilder.toString());

        selectQuery.setFirstResult(page * size);
        selectQuery.setMaxResults(size);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            selectQuery.setParameter(entry.getKey(), entry.getValue());
            countQuery.setParameter(entry.getKey(), entry.getValue());
        }
        List<PostEntity> entityList = selectQuery.getResultList();
        Long totalCount = (Long) countQuery.getSingleResult();
        return new FilterResultDTO<>(entityList, totalCount);
    }


}

