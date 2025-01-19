package giybat.uz.profile.controller;


import giybat.uz.exceptionHandler.AppBadException;
import giybat.uz.profile.dto.JwtDTO;
import giybat.uz.profile.dto.ProfileDTO;
import giybat.uz.profile.dto.UpdateProfileDetailDTO;
import giybat.uz.profile.enums.ProfileRole;
import giybat.uz.profile.service.ProfileService;
import giybat.uz.util.ApiResponse;
import giybat.uz.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile controller", description = "To manage user data")
public class ProfileController {

    @Autowired
    private ProfileService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    @Operation(summary = "Admin add user", description = "Add user via ADMIN")
    public ResponseEntity<?> addProfile(@RequestBody @Valid ProfileDTO requestDTO,
                                        @RequestHeader("Authorization") String token) {

        JwtDTO dto = JwtUtil.decode(token.substring(7));
        if (dto.getRole().equals(ProfileRole.ROLE_ADMIN)) {
            ApiResponse<?> response = new ApiResponse<>(201, "success", service.createProfile(requestDTO));
            return ResponseEntity.status(201).body(response);
        } else {
            ApiResponse<?> response = new ApiResponse<>(403, "error", null);
            return ResponseEntity.status(403).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    @Operation(summary = "Users", description = "Get all users")
    public ResponseEntity<?> getAllProfile(@RequestParam(value = "page", defaultValue = "1") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size,
                                           @RequestHeader("Authorization") String token) {
        JwtDTO dto = JwtUtil.decode(token.substring(7));
        if (dto.getRole().equals(ProfileRole.ROLE_ADMIN)) {
            ApiResponse<?> response = new ApiResponse<>(200, "success", service.profileAll(page - 1, size));
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<?> response = new ApiResponse<>(403, "error", null);
            return ResponseEntity.status(403).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "DELETE", description = "ADMIN -  user deleted")
    public ResponseEntity<?> deleteProfile(@PathVariable("id") Integer id,
                                           @RequestHeader("Authorization") String token) {
        JwtDTO dto = JwtUtil.decode(token.substring(7));
        if (dto.getRole().equals(ProfileRole.ROLE_ADMIN)) {
            return ResponseEntity.ok().body(service.deleted(id));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @Operation(summary = "Update", description = "Update username and surname pictures")
    @PutMapping("/detail")
    public ResponseEntity<?> updateDetail(@RequestBody @Valid UpdateProfileDetailDTO requestDTO) {
        ApiResponse<?> response = new ApiResponse<>(200, "success", service.updateDetail(requestDTO));
        return ResponseEntity.ok().body(response);
    }


    @ExceptionHandler(AppBadException.class)
    public ResponseEntity<?> handle(AppBadException e) {
        ApiResponse<?> response = new ApiResponse<>(400, e.getMessage(), null);
        return ResponseEntity.badRequest().body(response);
    }
}
