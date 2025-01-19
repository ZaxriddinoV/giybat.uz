package giybat.uz.usernameHistory.service;


import giybat.uz.exceptionHandler.AppBadException;
import giybat.uz.usernameHistory.entiy.SmsHistoryEntity;
import giybat.uz.usernameHistory.repository.SmsHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SmsHistoryService {
    @Autowired
    private SmsHistoryRepository smsHistoryRepository;

    public void smsHistory(String phone, Integer message, LocalDateTime createdDate) {
        SmsHistoryEntity smsHistory = new SmsHistoryEntity();
        smsHistory.setPhone(phone);
        smsHistory.setCode(message);
        smsHistory.setCreatedData(createdDate);
        smsHistoryRepository.save(smsHistory);
    }

    public List<SmsHistoryEntity> getAllGiven(LocalDate date) {
        List<SmsHistoryEntity> allByDate = smsHistoryRepository.findAllByDate(date);
        if (allByDate.isEmpty()) {
            throw new AppBadException("History not found");
        } else {
            return allByDate;
        }
    }

    public List<SmsHistoryEntity> getNumberHistory(String number) {
        List<SmsHistoryEntity> byPhone = smsHistoryRepository.findByPhone(number);
        if (byPhone.isEmpty()) {
            throw new AppBadException("History not found");
        } else {
            return byPhone;
        }
    }
}
