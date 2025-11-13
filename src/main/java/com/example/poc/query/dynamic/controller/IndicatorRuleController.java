package com.example.poc.query.dynamic.controller;

import com.example.poc.query.dynamic.dto.IndicatorRuleDTO;
import com.example.poc.query.dynamic.service.IndicatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/indicator-rules")
@RequiredArgsConstructor
public class IndicatorRuleController {

    private final IndicatorService indicatorService;

    @GetMapping
    public ResponseEntity<List<IndicatorRuleDTO>> getAllRules() {
        return ResponseEntity.ok(indicatorService.getAllRules());
    }

    @GetMapping("/active")
    public ResponseEntity<List<IndicatorRuleDTO>> getActiveRules() {
        return ResponseEntity.ok(indicatorService.getActiveRules());
    }

    @GetMapping("/context/{context}")
    public ResponseEntity<List<IndicatorRuleDTO>> getRulesByContext(@PathVariable String context) {
        return ResponseEntity.ok(indicatorService.getRulesByContext(context));
    }

    @GetMapping("/context/{context}/active")
    public ResponseEntity<List<IndicatorRuleDTO>> getActiveRulesByContext(@PathVariable String context) {
        return ResponseEntity.ok(indicatorService.getActiveRulesByContext(context));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndicatorRuleDTO> getRuleById(@PathVariable UUID id) {
        return ResponseEntity.ok(indicatorService.getRuleById(id));
    }

    @PostMapping
    public ResponseEntity<IndicatorRuleDTO> createRule(@RequestBody IndicatorRuleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(indicatorService.createRule(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IndicatorRuleDTO> updateRule(
            @PathVariable UUID id,
            @RequestBody IndicatorRuleDTO dto) {
        return ResponseEntity.ok(indicatorService.updateRule(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable UUID id) {
        indicatorService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}

