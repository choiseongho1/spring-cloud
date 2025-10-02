package com.msa.member.kafka.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long adminId;
    private String username;
    private String name;
    private String email;
    private String role;
    private String eventType; // "CREATED", "UPDATED", "DELETED" 등
}
