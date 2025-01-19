package giybat.uz.usernameHistory.controller;


import giybat.uz.exceptionHandler.AppBadException;
import giybat.uz.usernameHistory.entiy.SmsHistoryEntity;
import giybat.uz.usernameHistory.service.SmsHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sms-history")
public class SmsHistoryController {
    @Autowired
    SmsHistoryService smsHistoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/phone/{number}")
    public ResponseEntity<?> getEmailHistoryService(@PathVariable String number) {
        List<SmsHistoryEntity> entity =  smsHistoryService.getNumberHistory(number);
        return ResponseEntity.ok(entity);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/date/{date}")
    public ResponseEntity<?> getEmailHistoryService(@PathVariable LocalDate date) {
        return ResponseEntity.ok(smsHistoryService.getAllGiven(date));
    }

    @ExceptionHandler(AppBadException.class)
    public ResponseEntity<?> handle(AppBadException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
