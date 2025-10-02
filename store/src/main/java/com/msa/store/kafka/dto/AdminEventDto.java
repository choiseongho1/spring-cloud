package com.msa.store.kafka.dto;


import com.msa.store.domain.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventDto {

    private Long adminId;
    private String username;
    private String name;
    private String email;
    private String role;
    private String eventType; // "CREATED", "UPDATED", "DELETED" 등


    public Store toEntity(){
        return Store.builder()
                .ownerMemberName(username)
                .name(name)
                .status("ACTIVE")
                .feeRate("10")
                .build();
    }
}
