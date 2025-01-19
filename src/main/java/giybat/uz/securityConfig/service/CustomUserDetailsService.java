package giybat.uz.securityConfig.service;


import giybat.uz.securityConfig.config.CustomUserDetails;
import giybat.uz.profile.entity.ProfileEntity;
import giybat.uz.profile.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(username);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        ProfileEntity profile = optional.get();
        return new CustomUserDetails(profile) ;
    }

}
