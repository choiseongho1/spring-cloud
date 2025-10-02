package com.msa.store.service;

import com.msa.store.domain.Store;
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

    /**
     * 신규 매장 정보를 저장한다.
     * 
     * @param store 매장 정보
     * @return 저장된 매장 정보
     */
    @Transactional
    public Store createStore(Store store) {
        return storeRepository.save(store);
    }


}
