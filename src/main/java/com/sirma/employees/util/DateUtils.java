package com.sirma.employees.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DateUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

    private final List<DateTimeFormatter> formatters = new ArrayList<>();

    public DateUtils() {
        // Support for multiple date formats
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("yyyyMMdd"));
        formatters.add(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    public LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.equalsIgnoreCase("NULL")) {
            return null;
        }

        String trimmedDate = dateStr.trim();

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(trimmedDate, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }

        LOGGER.error("Unable to parse date: {}", dateStr);
        return null;
    }

    public long calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) return 0;

        LocalDate effectiveEndDate = endDate != null ? endDate : LocalDate.now();

        if (effectiveEndDate.isBefore(startDate)) {
            return 0;
        }

        return startDate.datesUntil(effectiveEndDate.plusDays(1)).count();
    }
}