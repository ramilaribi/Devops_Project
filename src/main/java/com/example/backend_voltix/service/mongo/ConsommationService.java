package com.example.backend_voltix.service.mongo;

import com.example.backend_voltix.dto.mongo.ConsumptionDataResponseDto;
import com.example.backend_voltix.dto.mongo.ConsumptionGroupResponseDto;
import com.example.backend_voltix.dto.mongo.ConsumptionPeriodResponseDto;
import com.example.backend_voltix.dto.mongo.ConsumptionResponseDto;
import com.example.backend_voltix.model.Area;
import com.example.backend_voltix.model.mongo.Consommation;
import com.example.backend_voltix.repository.AreaRepository;
import com.example.backend_voltix.repository.mongo.ConsommationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class ConsommationService {

    @Autowired
    private ConsommationRepository consommationRepository;
    @Autowired
    private AreaRepository areaRepository;

    public ConsumptionResponseDto calculateConsumptions(LocalDate date, String serialNumber) {

        LocalDate specificDate = (date != null) ? date : LocalDate.now();
        YearMonth currentMonth = YearMonth.from(specificDate);

        // Daily consumption
        Instant startOfDay = specificDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = startOfDay.plusSeconds(86399);
        double dailyConsumption = consommationRepository.findByDateAndSerialNumberBetween(startOfDay.toEpochMilli(), endOfDay.toEpochMilli(), serialNumber)
                .stream()
                .mapToDouble(Consommation::getConsommation_diff)
                .sum();

        // Monthly consumption
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        Instant startOfMonthInstant = startOfMonth.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfMonthInstant = endOfMonth.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        double monthlyConsumption = consommationRepository.findByDateAndSerialNumberBetween(startOfMonthInstant.toEpochMilli(), endOfMonthInstant.toEpochMilli(), serialNumber)
                .stream()
                .mapToDouble(Consommation::getConsommation_diff)
                .sum();

        String formattedMonthlyConsumption = String.format("%.2f", monthlyConsumption);
        String formattedDailyConsumption = String.format("%.2f", dailyConsumption);

        log.info("Total daily consumption for {} is {}", specificDate, formattedMonthlyConsumption);
        log.info("Total monthly consumption for {} is {}", currentMonth, formattedDailyConsumption);

        return new ConsumptionResponseDto(formattedDailyConsumption,formattedMonthlyConsumption);
    }

    public ConsumptionPeriodResponseDto getWeeklyConsumption(String serialNumber) {
       // LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate today = LocalDate.of(2024, 7, 21);
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<Double> consumption = IntStream.rangeClosed(0, 6) // Monday to Sunday
                .mapToObj(startOfWeek::plusDays)
                .map(date -> calculateDailyConsumption(date, serialNumber))
                .map(value -> round(value, 2))
                .collect(Collectors.toList());

        return new ConsumptionPeriodResponseDto(consumption);
    }
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException("Decimal places must be non-negative.");
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private double calculateDailyConsumption(LocalDate date, String serialNumber) {
        Instant startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = startOfDay.plusSeconds(86399); // End of the day

        return consommationRepository.findByDateAndSerialNumberBetween(
                        startOfDay.toEpochMilli(), endOfDay.toEpochMilli(), serialNumber)
                .stream()
                .mapToDouble(Consommation::getConsommation_diff)
                .sum();
    }


    public ConsumptionPeriodResponseDto getCurrentMonthConsumption(String serialNumber) {
       // LocalDate today = LocalDate.now();
        LocalDate today = LocalDate.of(2024, 7, 21);
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        // Adjust the start of the first week to be the first Monday of the month or the first day if it's Monday
        LocalDate firstMonday = firstDayOfMonth.getDayOfWeek().equals(DayOfWeek.MONDAY)
                ? firstDayOfMonth
                : firstDayOfMonth.with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        List<Double> weeklyConsumptions = new ArrayList<>();

        for (int week = 0; week < 5; week++) {
            LocalDate startOfWeek = firstMonday.plusWeeks(week);
            LocalDate endOfWeek = startOfWeek.plusDays(6);

            // Ensure the weeks do not extend beyond the current month
            if (startOfWeek.isAfter(lastDayOfMonth)) {
                break; // If the start of the week is beyond the last day, break the loop
            }

            if (endOfWeek.isAfter(lastDayOfMonth)) {
                endOfWeek = lastDayOfMonth; // Adjust the end of the week to the last day of the month
            }

            double weeklyConsumption = calculateWeeklyConsumption(startOfWeek, endOfWeek, serialNumber);
            weeklyConsumptions.add(round(weeklyConsumption, 2));

            // Logging for debugging
            log.info("Week {}: Start of week is {}, End of week is {}, Consumption is {}",
                    week + 1, startOfWeek, endOfWeek, weeklyConsumption);
        }

        return new ConsumptionPeriodResponseDto(weeklyConsumptions);
    }

    private double calculateWeeklyConsumption(LocalDate startOfWeek, LocalDate endOfWeek, String serialNumber) {
        Instant startOfPeriod = startOfWeek.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfPeriod = endOfWeek.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

        return consommationRepository.findByDateAndSerialNumberBetween(
                        startOfPeriod.toEpochMilli(), endOfPeriod.toEpochMilli(), serialNumber)
                .stream()
                .mapToDouble(Consommation::getConsommation_diff)
                .sum();
    }
    public List<Double> getYearlyConsumption(String serialNumber) {
        List<Double> monthlyConsumptions = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();

        for (int month = 1; month <= 12; month++) {
            LocalDate firstDayOfMonth = LocalDate.of(currentYear, month, 1);
            LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());


            double monthlyConsumption = calculateMonthlyConsumption(firstDayOfMonth, lastDayOfMonth, serialNumber);
            monthlyConsumptions.add(round(monthlyConsumption, 2));

            // Log the calculated consumption for auditing
            log.info("Month {}: Consumption is {}", month, monthlyConsumption);
        }

        return monthlyConsumptions;
    }

    private double calculateMonthlyConsumption(LocalDate startOfMonth, LocalDate endOfMonth, String serialNumber) {
        Instant startOfPeriod = startOfMonth.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfPeriod = endOfMonth.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

        return consommationRepository.findByDateAndSerialNumberBetween(
                        startOfPeriod.toEpochMilli(), endOfPeriod.toEpochMilli(), serialNumber)
                .stream()
                .mapToDouble(Consommation::getConsommation_diff)
                .sum();
    }

    public ConsumptionGroupResponseDto calculateGroupedConsumptions(String serialNumber) {
        ConsumptionGroupResponseDto dto = consommationRepository.findGroupedConsumptionBySerialNumber(serialNumber);

        if (dto != null && dto.getChannelId() != null) {
            // Fetch the area name from MySQL database using the channelId
            Optional<Area> area = areaRepository.findById(Long.valueOf(dto.getChannelId()));
            area.ifPresent(value -> dto.setAreaName(value.getName()));
        }

          Double TotalConsumptionArround =  Math.round(dto.getTotalConsumption() * 100.0) / 100.0;
            dto.setTotalConsumption(TotalConsumptionArround);
        return dto;
    }

    public double getTotalConsumption(String serialNumber) {
        Double totalConsumption = consommationRepository.findTotalConsumptionBySerialNumber(serialNumber);
        return totalConsumption != null ? Math.round(totalConsumption * 100.0) / 100.0 : 0;
    }


    public List<ConsumptionDataResponseDto> getHistoricDailyConsumptions(String serialNumber, int channelId) {
        // Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS);
        //  Instant now = Instant.now();
        // List<Consommation> consumptions = consommationRepository.findByDateSerialNumberAndChannelId(
        //  startOfDay.toEpochMilli(), now.toEpochMilli(), serialNumber, channelId);

        LocalDate today = LocalDate.of(2024, 7, 21);
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();  // Start of July 21st
        ZonedDateTime tenAM = LocalDateTime.of(today, LocalTime.of(13, 0)).atZone(ZoneId.systemDefault());
        Instant endOfPeriod = tenAM.toInstant();
        List<Consommation> consumptions = consommationRepository.findByDateSerialNumberAndChannelId(
                startOfDay.toEpochMilli(), endOfPeriod.toEpochMilli(), serialNumber, channelId);

        return consumptions.stream()
                .map(cons -> new ConsumptionDataResponseDto(
                        cons.getConsommation_diff(),
                        DateTimeFormatter.ISO_LOCAL_TIME.format(
                                LocalDateTime.ofInstant(Instant.ofEpochMilli((long) cons.getDate()), ZoneId.systemDefault())))
                )
                .collect(Collectors.toList());
    }

}