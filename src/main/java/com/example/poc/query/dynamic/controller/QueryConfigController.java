package com.example.poc.query.dynamic.controller;

import com.example.poc.query.dynamic.dto.*;
import com.example.poc.query.dynamic.service.QueryConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/queries")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class QueryConfigController {

    private final QueryConfigService queryConfigService;

    // QueryBase endpoints
    @GetMapping("/bases")
    public ResponseEntity<List<QueryBaseDTO>> getAllQueryBases() {
        return ResponseEntity.ok(queryConfigService.getAllQueryBases());
    }

    @GetMapping("/bases/{id}")
    public ResponseEntity<QueryBaseDTO> getQueryBaseById(@PathVariable UUID id) {
        return queryConfigService.getQueryBaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bases")
    public ResponseEntity<QueryBaseDTO> createQueryBase(@RequestBody QueryBaseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(queryConfigService.createQueryBase(dto));
    }

    @PutMapping("/bases/{id}")
    public ResponseEntity<QueryBaseDTO> updateQueryBase(@PathVariable UUID id, @RequestBody QueryBaseDTO dto) {
        try {
            return ResponseEntity.ok(queryConfigService.updateQueryBase(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/bases/{id}")
    public ResponseEntity<Void> deleteQueryBase(@PathVariable UUID id) {
        queryConfigService.deleteQueryBase(id);
        return ResponseEntity.noContent().build();
    }

    // QueryFilter endpoints
    @GetMapping("/filters")
    public ResponseEntity<List<QueryFilterDTO>> getAllQueryFilters() {
        return ResponseEntity.ok(queryConfigService.getAllQueryFilters());
    }

    @GetMapping("/filters/{id}")
    public ResponseEntity<QueryFilterDTO> getQueryFilterById(@PathVariable UUID id) {
        return queryConfigService.getQueryFilterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/filters")
    public ResponseEntity<QueryFilterDTO> createQueryFilter(@RequestBody QueryFilterDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(queryConfigService.createQueryFilter(dto));
    }

    @PutMapping("/filters/{id}")
    public ResponseEntity<QueryFilterDTO> updateQueryFilter(@PathVariable UUID id, @RequestBody QueryFilterDTO dto) {
        try {
            return ResponseEntity.ok(queryConfigService.updateQueryFilter(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/filters/{id}")
    public ResponseEntity<Void> deleteQueryFilter(@PathVariable UUID id) {
        queryConfigService.deleteQueryFilter(id);
        return ResponseEntity.noContent().build();
    }

    // QueryOrder endpoints
    @GetMapping("/orders")
    public ResponseEntity<List<QueryOrderDTO>> getAllQueryOrders() {
        return ResponseEntity.ok(queryConfigService.getAllQueryOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<QueryOrderDTO> getQueryOrderById(@PathVariable UUID id) {
        return queryConfigService.getQueryOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/orders")
    public ResponseEntity<QueryOrderDTO> createQueryOrder(@RequestBody QueryOrderDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(queryConfigService.createQueryOrder(dto));
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<QueryOrderDTO> updateQueryOrder(@PathVariable UUID id, @RequestBody QueryOrderDTO dto) {
        try {
            return ResponseEntity.ok(queryConfigService.updateQueryOrder(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteQueryOrder(@PathVariable UUID id) {
        queryConfigService.deleteQueryOrder(id);
        return ResponseEntity.noContent().build();
    }

    // Query Preview endpoint
    @PostMapping("/preview")
    public ResponseEntity<QueryPreviewResponse> previewQuery(@RequestBody QueryPreviewRequest request) {
        return ResponseEntity.ok(queryConfigService.previewQuery(request));
    }
}

