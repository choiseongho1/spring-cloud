package com.msa.store.service;

import com.msa.store.domain.Store;
import com.msa.store.kafka.dto.AdminEventDto;
import com.msa.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public void createAdmin(AdminEventDto adminEvent) {
        // 이미 존재하는 관리자인지 확인
        if (storeRepository.existsById(adminEvent.getAdminId())) {
            log.warn("[관리자 등록] 이미 존재하는 관리자: {}", adminEvent.getUsername());
            return;
        }
        
        Store store = adminEvent.toEntity();
        
        storeRepository.save(store);
    }


}
