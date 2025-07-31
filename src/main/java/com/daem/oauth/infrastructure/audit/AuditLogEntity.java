package com.daem.oauth.infrastructure.audit;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String username;

    @Column(length = 50)
    private String event;

    @Column(length = 50)
    private String ip;

    @Column(name = "event_time")
    private LocalDateTime timestamp;

    @Column(length = 1000)
    private String detail;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
} 