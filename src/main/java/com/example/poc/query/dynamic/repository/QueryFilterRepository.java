package com.example.poc.query.dynamic.repository;

import com.example.poc.query.dynamic.entity.QueryFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QueryFilterRepository extends JpaRepository<QueryFilter, UUID> {

    List<QueryFilter> findByContextAndActiveTrue(String context);

    Optional<QueryFilter> findByNameUniqueAndActiveTrue(String nameUnique);
}

