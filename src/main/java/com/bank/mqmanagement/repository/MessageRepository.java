package com.bank.mqmanagement.repository;

import com.bank.mqmanagement.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByMessageId(String messageId);

    Page<Message> findAllByOrderByReceivedAtDesc(Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.processed = :processed ORDER BY m.receivedAt DESC")
    Page<Message> findByProcessed(boolean processed, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.receivedAt BETWEEN :startDate AND :endDate ORDER BY m.receivedAt DESC")
    Page<Message> findByReceivedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.queueName = :queueName ORDER BY m.receivedAt DESC")
    Page<Message> findByQueueName(String queueName, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY m.receivedAt DESC")
    Page<Message> searchByContent(String keyword, Pageable pageable);
}
