package giybat.uz.profile.controller;


import giybat.uz.exceptionHandler.AppBadException;
import giybat.uz.usernameHistory.dto.SmsConfirmDTO;
import giybat.uz.profile.dto.AuthDTO;
import giybat.uz.profile.dto.ProfileDTO;
import giybat.uz.profile.dto.RegistrationDTO;
import giybat.uz.profile.service.AuthService;
import giybat.uz.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authorization controller", description = "For users to be authorized")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Operation(summary = "Api for authorization", description = "This api for users to register")
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@Valid @RequestBody RegistrationDTO dto) {
        ApiResponse<?> response = new ApiResponse<>(200, authService.registration(dto), null);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Api for authorization", description = "to confirm the user's email")
    @GetMapping("/registration/confirm/{id}")
    public ResponseEntity<String> registration(@PathVariable Integer id) {
        return ResponseEntity.ok(authService.registrationConfirm(id));
    }

    @Operation(summary = "Api for authorization", description = "to confirm the user's sms")
    @PostMapping("/registration/confirm/code")
    public ResponseEntity<?> registrationConfirmCode(@Valid @RequestBody SmsConfirmDTO dto) {
        ApiResponse<?> response = new ApiResponse<>(200, authService.smsConfirm(dto, LocalDateTime.now()), null);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Api for authorization", description = "login")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDTO dto) {
        ApiResponse<?> response = new ApiResponse<>(200, "success", authService.login(dto));
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler({AppBadException.class, IllegalArgumentException.class})
    public ResponseEntity<?> handle(AppBadException e) {
        ApiResponse response = new ApiResponse<>(400, e.getMessage(), null);
        return ResponseEntity.badRequest().body(response);
    }
}
