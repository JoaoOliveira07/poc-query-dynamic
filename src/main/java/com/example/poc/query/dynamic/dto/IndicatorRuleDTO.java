package com.example.poc.query.dynamic.dto;

import com.example.poc.query.dynamic.entity.IndicatorRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorRuleDTO {

    private UUID id;
    private String key;
    private String name;
    private String description;
    private String icon;
    private String context;
    private String conditionField;
    private String conditionOperator;
    private String conditionValue;
    private Boolean active;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static IndicatorRuleDTO fromEntity(IndicatorRule rule) {
        return IndicatorRuleDTO.builder()
                .id(rule.getId())
                .key(rule.getRuleKey())
                .name(rule.getName())
                .description(rule.getDescription())
                .icon(rule.getIcon())
                .context(rule.getContext())
                .conditionField(rule.getConditionField())
                .conditionOperator(rule.getConditionOperator().name())
                .conditionValue(rule.getConditionValue())
                .active(rule.getActive())
                .displayOrder(rule.getDisplayOrder())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }

    public IndicatorRule toEntity() {
        return IndicatorRule.builder()
                .id(this.id)
                .ruleKey(this.key)
                .name(this.name)
                .description(this.description)
                .icon(this.icon)
                .context(this.context)
                .conditionField(this.conditionField)
                .conditionOperator(IndicatorRule.ConditionOperator.valueOf(this.conditionOperator))
                .conditionValue(this.conditionValue)
                .active(this.active)
                .displayOrder(this.displayOrder)
                .build();
    }
}
