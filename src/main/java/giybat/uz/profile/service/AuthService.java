package giybat.uz.profile.service;


import giybat.uz.attach.entity.AttachEntity;
import giybat.uz.exceptionHandler.AppBadException;
import giybat.uz.securityConfig.config.CustomUserDetails;
import giybat.uz.usernameHistory.dto.SmsConfirmDTO;
import giybat.uz.usernameHistory.entiy.SmsHistoryEntity;
import giybat.uz.usernameHistory.repository.SmsHistoryRepository;
import giybat.uz.usernameHistory.service.EmailHistoryService;
import giybat.uz.usernameHistory.service.EmailSendingService;
import giybat.uz.usernameHistory.service.SmsHistoryService;
import giybat.uz.usernameHistory.service.SmsService;
import giybat.uz.profile.dto.AuthDTO;
import giybat.uz.profile.dto.ProfileDTO;
import giybat.uz.profile.dto.RegistrationDTO;
import giybat.uz.profile.entity.ProfileEntity;
import giybat.uz.profile.enums.ProfileRole;
import giybat.uz.profile.enums.ProfileStatus;
import giybat.uz.profile.enums.UsernameEnum;
import giybat.uz.profile.repository.ProfileRepository;
import giybat.uz.util.JwtUtil;
import giybat.uz.util.MD5Util;
import giybat.uz.util.UsernameValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    SmsService smsService;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private EmailHistoryService emailHistoryService;
    @Autowired
    SmsHistoryService historyService;
    @Autowired
    SmsHistoryRepository smsHistoryRepository;
    @Autowired
    AuthenticationManager authenticationManager;

    public String registration(RegistrationDTO dto) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent() && optional.get().getStatus().equals(ProfileStatus.ACTIVE)) {
            throw new AppBadException("Email or Phone already exists");
        }
        UsernameValidationUtil util = new UsernameValidationUtil();
        UsernameEnum usernameEnum = util.identifyInputType(dto.getUsername());
        switch (usernameEnum) {
            case PHONE_NUMBER:
                ProfileEntity profilePhone = registerConvert(dto);
                Integer code = smsService.sendRegistrationSms(dto.getUsername());
                System.out.println("Code == " + code);
                historyService.smsHistory(profilePhone.getUsername(), code, profilePhone.getCreated_date());
                return "Confirm sent sms";
            case EMAIL:
                ProfileEntity profileEmail = registerConvert(dto);
                StringBuilder sb = new StringBuilder();
                sb.append("<h1 style=\"text-align: center\"> Complete Registration</h1>");
                sb.append("<br>");
                sb.append("<p>Click the link below to complete registration</p>\n");
                sb.append("<p><a style=\"padding: 5px; background-color: indianred; color: white\"  href=http://localhost:8080/api/auth/registration/confirm/")
                        .append(profileEmail.getId()).append(" target=_blank >Click Th</a></p>\n");

                emailSendingService.sendSimpleMessage(dto.getUsername(), "Complite Registration", sb.toString());
                emailSendingService.sendMimeMessage(dto.getUsername(), "Tasdiqlash", sb.toString());
                emailHistoryService.addEmailHistory(dto.getUsername(), "Tasdiqlash", profileEmail.getCreated_date());
                return "Confirm sent email";
            case UNKNOWN:
                throw new AppBadException("Invalid username");
        }
        throw new AppBadException("Invalid username");
    }

    public ProfileEntity registerConvert(RegistrationDTO dto) {
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setUsername(dto.getUsername());
        entity.setPassword(MD5Util.md5(dto.getPassword()));
        entity.setSurname(dto.getSurname());
        entity.setRole(ProfileRole.ROLE_USER);
        entity.setStatus(ProfileStatus.IN_REGISTRATION);
        entity.setVisible(Boolean.TRUE);
        entity.setCreated_date(LocalDateTime.now());
        profileRepository.save(entity);
        return entity;
    }

    public String registrationConfirm(Integer id) {
        Optional<ProfileEntity> optional = profileRepository.findByIdAndVisibleTrue(id);
        if (optional.isEmpty()) {
            throw new AppBadException("Not Completed");
        }
        ProfileEntity entity = optional.get();
        if (!entity.getStatus().equals(ProfileStatus.IN_REGISTRATION)) {
            throw new AppBadException("Not Completed");
        }
        entity.setStatus(ProfileStatus.ACTIVE);
        profileRepository.save(entity);
        return "Completed";
    }

    public String smsConfirm(SmsConfirmDTO dto, LocalDateTime dateTime) {
        boolean b = profileRepository.existsByUsername(dto.getPhone());
        if (b) {
            SmsHistoryEntity latestCodeByPhone = smsHistoryRepository.findLatestCodeByPhone(dto.getPhone());
            Duration duration = Duration.between(dateTime, latestCodeByPhone.getCreatedData());
            if (duration.abs().getSeconds() >= 120) {
                throw new AppBadException("SMS timed out");
            }
            if (latestCodeByPhone.getCode().equals(dto.getCode())) {
                profileRepository.updateStatus(dto.getPhone());
                return "Active account";
            } else {
                throw new AppBadException("the code is incorrect");
            }
        } else {
            throw new AppBadException("The phone number could not be found or it has already been registered");
        }
    }

    public ProfileDTO login(AuthDTO dto) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            throw new AppBadException("Email or Password wrong");
        }
        ProfileEntity entity = optional.get();
        if (!entity.getPassword().equals(MD5Util.md5(dto.getPassword()))) {
            throw new AppBadException("Email or Password wrong");
        }
        if (!entity.getStatus().equals(ProfileStatus.ACTIVE)) {
            throw new AppBadException("User Not Active");
        }
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setName(entity.getName());
        profileDTO.setSurname(entity.getSurname());
        profileDTO.setUsername(entity.getUsername());
        profileDTO.setRole(entity.getRole());
        String accessToken = JwtUtil.encode(entity.getUsername(), entity.getRole().toString());
        String refreshToken = JwtUtil.generateRefreshToken(entity.getUsername());
        profileDTO.setJwtToken(accessToken);
        profileDTO.setRefreshToken(refreshToken);
        return profileDTO;
    }


}
