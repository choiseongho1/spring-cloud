package com.msa.store.kafka.consumer;

import com.msa.store.kafka.dto.AdminEventDto;
import com.msa.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class AdminEventConsumer {

    private final StoreService storeService;

    @KafkaListener(
            topics = "${kafka.topic.admin-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "adminEventKafkaListenerContainerFactory"
    )
    public void consumeAdminEvent(AdminEventDto adminEvent) {
        log.info("[Kafka] 관리자 이벤트 수신: {}", adminEvent);

        try {
            switch (adminEvent.getEventType()) {
                case "CREATED":
                    storeService.createAdmin(adminEvent);
                    break;
                case "UPDATED":
//                    adminService.updateAdmin(adminEvent);
                    break;
                case "DELETED":
//                    adminService.deleteAdmin(adminEvent.getAdminId());
                    break;
                default:
                    log.warn("[Kafka] 알 수 없는 이벤트 타입: {}", adminEvent.getEventType());
            }
        } catch (Exception e) {
            log.error("[Kafka] 관리자 이벤트 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
