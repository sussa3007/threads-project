package com.challenge.project.generatedata.repository;

import com.challenge.project.generatedata.entity.Counting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountingRepository extends JpaRepository<Counting, Long> {
}
