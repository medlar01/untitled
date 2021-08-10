package com.xxx.consumer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordDAO extends JpaRepository<Record, Integer> {
}
