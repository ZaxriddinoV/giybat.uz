package giybat.uz.post.service;

import giybat.uz.attach.service.AttachService;
import giybat.uz.exceptionHandler.AppBadException;
import giybat.uz.post.dto.*;
import giybat.uz.post.entity.PostEntity;
import giybat.uz.post.repository.CustomRepository;
import giybat.uz.post.repository.PostRepository;
import giybat.uz.profile.dto.ProfileShortInfo;
import giybat.uz.profile.enums.ProfileRole;
import giybat.uz.profile.service.ProfileService;
import giybat.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private CustomRepository customRepository;

    public PostDTO AddedPost(CreatePostDTO dto) {
        PostEntity postEntity = new PostEntity();
        postEntity.setContent(dto.getContent());
        postEntity.setPhotoId(dto.getPhotoId());
        postEntity.setVisible(Boolean.TRUE);
        postEntity.setUser(profileService.getByIdProfile(SpringSecurityUtil.getCurrentUser().getId()));
        postEntity.setCreatedDate(LocalDateTime.now());
        postRepository.save(postEntity);
        return toDTO(postEntity);
    }

    public PostDTO update(CreatePostDTO dto, Integer id) {
        Optional<PostEntity> byId = postRepository.findById(id);

        if (byId.isPresent()) {
            if (byId.get().getUser().getId().equals(SpringSecurityUtil.getCurrentUser().getId())) {
                byId.get().setContent(dto.getContent());
                byId.get().setPhotoId(dto.getPhotoId());
                byId.get().setCreatedDate(LocalDateTime.now());
                postRepository.save(byId.get());
                return toDTO(byId.get());
            } else throw new AppBadException("User is not authorized to update this post");
        } else {
            throw new AppBadException("Id not found");
        }
    }

    private PostDTO toDTO(PostEntity postEntity) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(postEntity.getId());
        postDTO.setCreatedDate(postEntity.getCreatedDate());
        postDTO.setContent(postEntity.getContent());
        postDTO.setAttachDTO(attachService.getUrl(postEntity.getPhotoId()));
        return postDTO;
    }

    public Boolean delete(Integer id) {
        Optional<PostEntity> byIdAndVisibleTrue = postRepository.findByIdAndVisibleTrue(id);
        if (byIdAndVisibleTrue.isPresent()) {
            if (SpringSecurityUtil.getCurrentUser().getId().equals(byIdAndVisibleTrue.get().getUser().getId()) || SpringSecurityUtil.getCurrentUser().getRole().equals(ProfileRole.ROLE_ADMIN)) {
                byIdAndVisibleTrue.get().setVisible(Boolean.FALSE);
                postRepository.save(byIdAndVisibleTrue.get());
                return true;
            }else throw new AppBadException("User is not authorized to delete this post");
        } else throw new AppBadException("Id not found");
    }


    public PostInfoDTO getPostId(Integer id) {
        Optional<PostEntity> byIdAndVisibleTrue = postRepository.findByIdAndVisibleTrue(id);
        if (byIdAndVisibleTrue.isPresent()) {
            return toDTOInfo(byIdAndVisibleTrue.get());
        } else throw new AppBadException("Id not found");
    }


    public Page<PostInfoDTO> filter(FilterDTO filter, int page, int size) {
        FilterResultDTO<PostEntity> result = customRepository.filter(filter, page, size);
        List<PostInfoDTO> dtoList = new LinkedList<>();
        for (PostEntity entity : result.getContent()) {
            dtoList.add(this.toDTOInfo(entity));
        }
        return new PageImpl<>(dtoList, PageRequest.of(page, size), result.getTotal());
    }
    public Page<PostInfoDTO> postAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<PostEntity> entityList = postRepository.getAll(pageRequest);
        Long total = entityList.getTotalElements();
        List<PostInfoDTO> dtoList = new LinkedList<>();
        for (PostEntity entity : entityList) {
            dtoList.add(toDTOInfo(entity));
        }
        PageImpl page1 = new PageImpl<>(dtoList, pageRequest, total);

        return page1;
    }

    private PostInfoDTO toDTOInfo(PostEntity postEntity) {
        PostInfoDTO postDTO = new PostInfoDTO();
        postDTO.setId(postEntity.getId());
        postDTO.setCreatedDate(postEntity.getCreatedDate());
        postDTO.setContent(postEntity.getContent());
        postDTO.setPhoto(attachService.getUrl(postEntity.getPhotoId()));
        postDTO.setUser(new ProfileShortInfo(postEntity.getUser().getName(),postEntity.getUser().getPhotoId()));
        return postDTO;
    }



}
