package com.msa.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @Id
    @Column(nullable = false)
    private String token;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private Instant expiryDate;
    
    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }
}
