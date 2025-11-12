package com.example.poc.query.dynamic.repository;

import com.example.poc.query.dynamic.entity.QueryBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QueryBaseRepository extends JpaRepository<QueryBase, UUID> {

    Optional<QueryBase> findByNameUniqueAndActiveTrue(String nameUnique);

    Optional<QueryBase> findByContextAndActiveTrue(String context);
}

