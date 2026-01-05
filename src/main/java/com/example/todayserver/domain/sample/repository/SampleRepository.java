package com.example.todayserver.domain.sample.repository;

import com.example.todayserver.domain.sample.entity.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 샘플 Repository
 */
public interface SampleRepository extends JpaRepository<SampleEntity, Long> {
}