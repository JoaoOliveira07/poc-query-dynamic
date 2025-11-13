package com.example.poc.query.dynamic.service;

import com.example.poc.query.dynamic.dto.IndicatorDTO;
import com.example.poc.query.dynamic.dto.IndicatorRuleDTO;
import com.example.poc.query.dynamic.entity.IndicatorRule;
import com.example.poc.query.dynamic.repository.IndicatorRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço genérico para avaliação de indicadores
 * Trabalha com qualquer entidade através do conceito de CONTEXT
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorService {

    private final IndicatorRuleRepository indicatorRuleRepository;

    /**
     * Avalia todos os indicadores ativos de um contexto para uma entidade
     *
     * @param entity A entidade a ser avaliada (Customer, Order, Product, etc)
     * @param context O contexto das regras (CUSTOMER, ORDER, PRODUCT, etc)
     * @return Lista de indicadores avaliados
     */
    public List<IndicatorDTO> evaluateIndicators(Object entity, String context) {
        List<IndicatorRule> activeRules = indicatorRuleRepository
                .findByContextAndActiveTrueOrderByDisplayOrderAsc(context);
        List<IndicatorDTO> indicators = new ArrayList<>();

        for (IndicatorRule rule : activeRules) {
            try {
                Boolean value = evaluateCondition(entity, rule);

                IndicatorDTO indicator = IndicatorDTO.builder()
                        .key(rule.getRuleKey())
                        .name(rule.getName())
                        .value(value)
                        .icon(rule.getIcon())
                        .description(rule.getDescription())
                        .build();

                indicators.add(indicator);
            } catch (Exception e) {
                log.error("Erro ao avaliar indicador '{}' para contexto '{}': {}",
                         rule.getRuleKey(), context, e.getMessage());
            }
        }

        return indicators;
    }

    /**
     * Avalia a condição de um indicador para uma entidade qualquer usando reflection
     */
    private Boolean evaluateCondition(Object entity, IndicatorRule rule) throws Exception {
        String fieldName = rule.getConditionField();
        Object fieldValue = getFieldValue(entity, fieldName);

        return switch (rule.getConditionOperator()) {
            case IS_TRUE -> Boolean.TRUE.equals(fieldValue);
            case IS_FALSE -> Boolean.FALSE.equals(fieldValue);
            case IS_NULL -> fieldValue == null;
            case IS_NOT_NULL -> fieldValue != null;
            case EQUALS -> {
                if (fieldValue == null) yield false;
                String expectedValue = rule.getConditionValue();
                yield fieldValue.toString().equals(expectedValue);
            }
            case NOT_EQUALS -> {
                if (fieldValue == null) yield true;
                String expectedValue = rule.getConditionValue();
                yield !fieldValue.toString().equals(expectedValue);
            }
            case CONTAINS -> {
                if (fieldValue == null) yield false;
                String searchValue = rule.getConditionValue();
                yield fieldValue.toString().toLowerCase().contains(searchValue.toLowerCase());
            }
            case GREATER_THAN -> {
                if (fieldValue == null) yield false;
                try {
                    double numValue = Double.parseDouble(fieldValue.toString());
                    double compareValue = Double.parseDouble(rule.getConditionValue());
                    yield numValue > compareValue;
                } catch (NumberFormatException e) {
                    log.warn("Não foi possível comparar valores numéricos para o campo {}", fieldName);
                    yield false;
                }
            }
            case LESS_THAN -> {
                if (fieldValue == null) yield false;
                try {
                    double numValue = Double.parseDouble(fieldValue.toString());
                    double compareValue = Double.parseDouble(rule.getConditionValue());
                    yield numValue < compareValue;
                } catch (NumberFormatException e) {
                    log.warn("Não foi possível comparar valores numéricos para o campo {}", fieldName);
                    yield false;
                }
            }
        };
    }

    /**
     * Obtém o valor de um campo da entidade usando reflection
     * Genérico - funciona com qualquer classe
     */
    private Object getFieldValue(Object entity, String fieldName) throws Exception {
        Class<?> entityClass = entity.getClass();
        Field field = entityClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(entity);
    }

    // ===== CRUD de Regras de Indicadores =====

    public List<IndicatorRuleDTO> getAllRules() {
        return indicatorRuleRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(IndicatorRuleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<IndicatorRuleDTO> getRulesByContext(String context) {
        return indicatorRuleRepository.findByContextOrderByDisplayOrderAsc(context)
                .stream()
                .map(IndicatorRuleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<IndicatorRuleDTO> getActiveRules() {
        return indicatorRuleRepository.findByActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(IndicatorRuleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<IndicatorRuleDTO> getActiveRulesByContext(String context) {
        return indicatorRuleRepository.findByContextAndActiveTrueOrderByDisplayOrderAsc(context)
                .stream()
                .map(IndicatorRuleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public IndicatorRuleDTO getRuleById(UUID id) {
        return indicatorRuleRepository.findById(id)
                .map(IndicatorRuleDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Regra de indicador não encontrada com ID: " + id));
    }

    public IndicatorRuleDTO createRule(IndicatorRuleDTO dto) {
        IndicatorRule rule = dto.toEntity();
        IndicatorRule savedRule = indicatorRuleRepository.save(rule);
        return IndicatorRuleDTO.fromEntity(savedRule);
    }

    public IndicatorRuleDTO updateRule(UUID id, IndicatorRuleDTO dto) {
        IndicatorRule existingRule = indicatorRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regra de indicador não encontrada com ID: " + id));

        existingRule.setRuleKey(dto.getKey());
        existingRule.setName(dto.getName());
        existingRule.setDescription(dto.getDescription());
        existingRule.setIcon(dto.getIcon());
        existingRule.setContext(dto.getContext());
        existingRule.setConditionField(dto.getConditionField());
        existingRule.setConditionOperator(IndicatorRule.ConditionOperator.valueOf(dto.getConditionOperator()));
        existingRule.setConditionValue(dto.getConditionValue());
        existingRule.setActive(dto.getActive());
        existingRule.setDisplayOrder(dto.getDisplayOrder());

        IndicatorRule updatedRule = indicatorRuleRepository.save(existingRule);
        return IndicatorRuleDTO.fromEntity(updatedRule);
    }

    public void deleteRule(UUID id) {
        indicatorRuleRepository.deleteById(id);
    }
}

