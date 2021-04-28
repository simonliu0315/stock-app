package com.stock.app.repository;

import com.stock.app.domain.DailyPrice;
import com.stock.app.domain.MainStock;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the MainStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DailyPriceRepository extends JpaRepository<DailyPrice, Long> {

    //@Query("SELECT s.cardNumber FROM DailyPrice s where s.no =:no and s.date = :date   ")
    public DailyPrice findByNoAndDate(String no, String date);


}
