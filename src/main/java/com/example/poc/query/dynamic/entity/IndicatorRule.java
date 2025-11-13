package com.example.poc.query.dynamic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "indicator_rule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "rule_key", nullable = false, unique = true, length = 100)
    private String ruleKey;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "icon", nullable = false, length = 100)
    private String icon;

    @Column(name = "context", nullable = false, length = 50)
    private String context;

    @Column(name = "condition_field", nullable = false, length = 100)
    private String conditionField;

    @Column(name = "condition_operator", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ConditionOperator conditionOperator;

    @Column(name = "condition_value", length = 255)
    private String conditionValue;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
        if (displayOrder == null) {
            displayOrder = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ConditionOperator {
        EQUALS,
        NOT_EQUALS,
        IS_TRUE,
        IS_FALSE,
        IS_NULL,
        IS_NOT_NULL,
        GREATER_THAN,
        LESS_THAN,
        CONTAINS
    }
}

