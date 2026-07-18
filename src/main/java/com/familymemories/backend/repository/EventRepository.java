package com.familymemories.backend.repository;

import com.familymemories.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate fromDate);
    List<Event> findAllByOrderByEventDateAsc();
}