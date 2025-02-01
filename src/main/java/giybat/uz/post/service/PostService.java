package giybat.uz.post.service;

import giybat.uz.attach.dto.AttachDTO;
import giybat.uz.attach.service.AttachService;
import giybat.uz.exceptionHandler.AppBadException;
import giybat.uz.post.dto.*;
import giybat.uz.post.entity.PostEntity;
import giybat.uz.post.repository.CustomRepository;
import giybat.uz.post.repository.PostRepository;
import giybat.uz.profile.dto.ProfileShortInfo;
import giybat.uz.profile.enums.ProfileRole;
import giybat.uz.profile.service.ProfileService;
import giybat.uz.util.PageImplUtil;
import giybat.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public PostDTO AddedPost(CreatePostDTO dto, MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppBadException("file is empty");
        } else {
            AttachDTO upload = attachService.upload(file);

            PostEntity postEntity = new PostEntity();
            postEntity.setContent(dto.getContent());
            postEntity.setPhotoId(upload.getId());
            postEntity.setVisible(Boolean.TRUE);
            postEntity.setTitle(dto.getTitle());
            postEntity.setUser(profileService.getByIdProfile(SpringSecurityUtil.getCurrentUser().getId()));
            postEntity.setCreatedDate(LocalDateTime.now());
            postRepository.save(postEntity);
            return toDTO(postEntity);
        }

    }

    public PostDTO update(CreatePostDTO dto, Integer id, MultipartFile file) {
        Optional<PostEntity> byId = postRepository.findById(id);
        if (byId.isPresent()) {
            if (byId.get().getUser().getId().equals(SpringSecurityUtil.getCurrentUser().getId())) {
                if (file.isEmpty()) {
                    byId.get().setContent(dto.getContent());
                    byId.get().setTitle(dto.getTitle());
                    byId.get().setCreatedDate(LocalDateTime.now());
                    postRepository.save(byId.get());
                    return toDTO(byId.get());
                } else {
                    AttachDTO upload = attachService.upload(file);
                    byId.get().setContent(dto.getContent());
                    byId.get().setPhotoId(upload.getId());
                    byId.get().setTitle(dto.getTitle());
                    byId.get().setCreatedDate(LocalDateTime.now());
                    postRepository.save(byId.get());
                    return toDTO(byId.get());
                }
            } else throw new AppBadException("User is not authorized to update this post");
        } else {
            throw new AppBadException("Id not found");
        }
    }

    private PostDTO toDTO(PostEntity postEntity) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(postEntity.getId());
        postDTO.setCreatedDate(postEntity.getCreatedDate());
        postDTO.setTitle(postEntity.getTitle());
        postDTO.setContent(postEntity.getContent());
        postDTO.setPhoto(attachService.getUrl(postEntity.getPhotoId()));
        return postDTO;
    }

    public Boolean delete(Integer id) {
        Optional<PostEntity> byIdAndVisibleTrue = postRepository.findByIdAndVisibleTrue(id);
        if (byIdAndVisibleTrue.isPresent()) {
            if (SpringSecurityUtil.getCurrentUser().getId().equals(byIdAndVisibleTrue.get().getUser().getId()) || SpringSecurityUtil.getCurrentUser().getRole().equals(ProfileRole.ROLE_ADMIN)) {
                byIdAndVisibleTrue.get().setVisible(Boolean.FALSE);
                postRepository.save(byIdAndVisibleTrue.get());
                return true;
            } else throw new AppBadException("User is not authorized to delete this post");
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

    public PageImplUtil<PostInfoDTO> postAll(int page, int size) {
        if (page < 0) {
            throw new AppBadException("page must be greater than 0");
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<PostEntity> entityList = postRepository.getAll(pageRequest);
        Long total = entityList.getTotalElements();
        List<PostInfoDTO> dtoList = new LinkedList<>();
        for (PostEntity entity : entityList) {
            dtoList.add(toDTOInfo(entity));
        }
        PageImpl page1 = new PageImpl<>(dtoList, pageRequest, total);
        PageImplUtil<PostInfoDTO> pageImplUtil = new PageImplUtil<>(dtoList, page1.getNumber() + 1, page1.getSize(), page1.getTotalElements(), page1.getTotalPages());
        return pageImplUtil;
    }

    public PageImplUtil<PostInfoDTO> myPosts(int page, int size) {
        if (page < 0) {
            throw new AppBadException("page must be greater than 0");
        }
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<PostEntity> entityList = postRepository.getPosts(pageRequest, currentUserId);
        Long total = entityList.getTotalElements();
        List<PostInfoDTO> dtoList = new LinkedList<>();
        for (PostEntity entity : entityList) {
            dtoList.add(toDTOInfo(entity));
        }
        PageImpl page1 = new PageImpl<>(dtoList, pageRequest, total);
        PageImplUtil<PostInfoDTO> pageImplUtil = new PageImplUtil<>(dtoList, page1.getNumber() + 1, page1.getSize(), page1.getTotalElements(), page1.getTotalPages());
        return pageImplUtil;
    }

    private PostInfoDTO toDTOInfo(PostEntity postEntity) {
        LocalDateTime createdDate = postEntity.getCreatedDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = createdDate.format(formatter);

        PostInfoDTO postDTO = new PostInfoDTO();
        postDTO.setId(postEntity.getId());
        postDTO.setCreatedDate(formattedDate);
        postDTO.setTitle(postEntity.getTitle());
        postDTO.setContent(postEntity.getContent());
        postDTO.setPhoto(attachService.getUrl(postEntity.getPhotoId()));
        postDTO.setUser(new ProfileShortInfo(postEntity.getUser().getId(),postEntity.getUser().getName(), postEntity.getUser().getPhotoId()));
        return postDTO;
    }

}
