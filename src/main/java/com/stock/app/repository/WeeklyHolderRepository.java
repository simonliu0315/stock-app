package com.stock.app.repository;

import com.stock.app.domain.DailyPrice;
import com.stock.app.domain.WeeklyHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Spring Data  repository for the MainStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WeeklyHolderRepository extends JpaRepository<WeeklyHolder, Long> {

    public WeeklyHolder findByNoAndDateAndLevel(String no, String date, BigDecimal level);
}
