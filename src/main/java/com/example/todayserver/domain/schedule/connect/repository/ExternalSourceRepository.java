package com.example.todayserver.domain.schedule.connect.repository;

import com.example.todayserver.domain.schedule.connect.entity.ExternalSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExternalSourceRepository extends JpaRepository<ExternalSource, Long> {
    Optional<ExternalSource> findByExternalAccountIdAndSourceKey(Long externalAccountId, String sourceKey);

    @Query("""
           select s
           from ExternalSource s
           where s.externalAccount.id = :externalAccountId
             and s.syncEnabled = true
           """)
    List<ExternalSource> findEnabledByAccount(Long externalAccountId);
}
