package com.challenge.project.generatedata.repository;

import com.challenge.project.generatedata.entity.GenerateData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenerateDataRepository extends JpaRepository<GenerateData, Long> {

}
