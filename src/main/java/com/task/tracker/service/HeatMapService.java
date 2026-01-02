package com.task.tracker.service;

import com.task.tracker.model.Heatmap;
import com.task.tracker.repository.HeatMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class HeatMapService {

    @Autowired
    private HeatMapRepository heatMapRepository;

    private Heatmap createEmptyHeatmap(String userId, int year, int month) {

        int days = YearMonth.of(year, month).lengthOfMonth();

        return Heatmap.builder()
                .userId(userId)
                .year(year)
                .month(month)
                .activity(new ArrayList<>(Collections.nCopies(days, 0)))
                .build();
    }


    public void updateHeatmap(String userId, LocalDate date, boolean increment) {

        int year = date.getYear();
        int month = date.getMonthValue();
        int dayIndex = date.getDayOfMonth() - 1;

        Heatmap heatmap = heatMapRepository
                .findByUserIdAndYearAndMonth(userId, year, month)
                .orElseGet(() -> createEmptyHeatmap(userId, year, month));

        ensureMonthSize(heatmap);

        int current = heatmap.getActivity().get(dayIndex);

        if (increment) {

            heatmap.getActivity().set(dayIndex, current + 1);
        } else {
            int updated = Math.max(0, current - 1);
            heatmap.getActivity().set(dayIndex, updated);
        }

        heatMapRepository.save(heatmap);
    }



    private void ensureMonthSize(Heatmap heatmap) {

        int daysInMonth = YearMonth.of(
                heatmap.getYear(),
                heatmap.getMonth()
        ).lengthOfMonth();

        if (heatmap.getActivity() == null) {
            heatmap.setActivity(
                    new ArrayList<>(Collections.nCopies(daysInMonth, 0))
            );
            return;
        }

        int currentSize = heatmap.getActivity().size();

        if (currentSize < daysInMonth) {
            for (int i = currentSize; i < daysInMonth; i++) {
                heatmap.getActivity().add(0);
            }
        }

        if (currentSize > daysInMonth) {
            heatmap.setActivity(
                    new ArrayList<>(heatmap.getActivity().subList(0, daysInMonth))
            );
        }
    }

    public Heatmap getOrCreateMonth(String userId, int year, int month) {

        return heatMapRepository
                .findByUserIdAndYearAndMonth(userId, year, month)
                .orElseGet(() -> {
                    Heatmap created = createEmptyHeatmap(userId, year, month);
                    return heatMapRepository.save(created);
                });
    }

    public List<Heatmap> getYearHeatmap(String userId, int year) {

        return heatMapRepository.findByUserIdAndYear(userId, year);
    }


}
