package giybat.uz.profile.service;


import giybat.uz.attach.service.AttachService;
import giybat.uz.exceptionHandler.AppBadException;
import giybat.uz.profile.dto.JwtDTO;
import giybat.uz.profile.dto.ProfileInfoDTO;
import giybat.uz.profile.enums.ProfileRole;
import giybat.uz.securityConfig.config.CustomUserDetails;
import giybat.uz.usernameHistory.repository.SmsHistoryRepository;
import giybat.uz.usernameHistory.service.SmsHistoryService;
import giybat.uz.usernameHistory.service.SmsService;
import giybat.uz.profile.dto.ProfileDTO;
import giybat.uz.profile.dto.UpdateProfileDetailDTO;
import giybat.uz.profile.entity.ProfileEntity;
import giybat.uz.profile.repository.ProfileRepository;
import giybat.uz.util.ApiResponse;
import giybat.uz.util.JwtUtil;
import giybat.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    SmsService smsService;
    @Autowired
    SmsHistoryService historyService;
    @Autowired
    SmsHistoryRepository smsHistoryRepository;
    @Autowired
    AttachService attachService;

    public ProfileDTO createProfile(ProfileDTO profile) {
        boolean b = profileRepository.existsByUsername(profile.getUsername());
        if (b) {
            throw new AppBadException("Email already exists");
        } else {
            ProfileEntity save = profileRepository.save(profile.mapToEntity());
            profile.setId(save.getId());
            return profile;
        }
    }

    public Page<ProfileEntity> profileAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("created_date").descending());
        Page<ProfileEntity> entityList = profileRepository.findAllPage(pageRequest);
        Long total = entityList.getTotalElements();
        List<ProfileDTO> dtoList = new LinkedList<>();
        for (ProfileEntity entity : entityList) {
            dtoList.add(entity.convertToDTO());
        }
        PageImpl page1 = new PageImpl<>(dtoList, pageRequest, total);

        return page1;
    }

    public boolean updateDetail(UpdateProfileDetailDTO requestDTO) {
        ProfileEntity profile = getById(SpringSecurityUtil.getCurrentUserId());
        if (profile == null) {
            throw new AppBadException("Profile not found");
        }
        profile.setName(requestDTO.getName());
        profile.setSurname(requestDTO.getSurname());
        profile.setPhotoId(requestDTO.getPhotoId());
        profileRepository.save(profile);
        return true;
    }

    private ProfileEntity getById(Integer currentUserId) {
        return profileRepository.findById(currentUserId).orElse(null);
    }


    public ProfileEntity getByIdProfile(Integer id) {
        ProfileEntity result;
        Optional<ProfileEntity> byIdAndVisibleTrue = profileRepository.findByIdAndVisibleTrue(id);
        if (byIdAndVisibleTrue.isPresent()){
            result = byIdAndVisibleTrue.get();
        }else {
            result = null;
        }
        return result;
    }
    public ProfileInfoDTO getByUsername() {
        CustomUserDetails currentUser = SpringSecurityUtil.getCurrentUser();
        if (currentUser == null || !currentUser.getRole().equals(ProfileRole.ROLE_USER)) {
            throw new AppBadException("Invalid token");
        }
        if (currentUser.getUsername() == null || currentUser.getUsername().isEmpty() || currentUser.getId() == null) {
            throw new AppBadException("Invalid username");
        }
        Optional<ProfileEntity> byUsernameAndVisibleTrue = profileRepository.findById(currentUser.getId());
        if (byUsernameAndVisibleTrue.isPresent()){
            ProfileEntity profileEntity = byUsernameAndVisibleTrue.get();
            ProfileInfoDTO profileDTO = new ProfileInfoDTO();
            profileDTO.setId(profileEntity.getId());
            profileDTO.setUsername(profileEntity.getUsername());
            profileDTO.setSurname(profileEntity.getSurname());
            profileDTO.setName(profileEntity.getName());
            profileDTO.setPhoto(attachService.getUrl(profileEntity.getPhotoId()));
            return profileDTO;
        }else throw new AppBadException("Bunday foydalanuchi topilmadi");
    }

    public Integer deleted(Integer id) {
        return profileRepository.deleted(id);
    }

}
