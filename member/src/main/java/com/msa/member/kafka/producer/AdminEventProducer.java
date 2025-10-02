package com.msa.member.kafka.producer;

import com.msa.member.kafka.dto.AdminEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.admin-events:admin-events}")
    private String adminEventsTopic;

    public void publishAdminEvent(AdminEventDto adminEvent) {
        try {
            // 키는 adminId를 사용하여 동일 관리자의 이벤트가 같은 파티션으로 가도록 함
            String key = adminEvent.getAdminId().toString();

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(adminEventsTopic, key, adminEvent);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("[Kafka] 관리자 이벤트 발행 성공: topic={}, key={}, event={}",
                            adminEventsTopic, key, adminEvent);
                } else {
                    log.error("[Kafka] 관리자 이벤트 발행 실패: topic={}, key={}, event={}, error={}",
                            adminEventsTopic, key, adminEvent, ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("[Kafka] 관리자 이벤트 발행 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
