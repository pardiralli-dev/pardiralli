package ee.pardiralli.service;

import ee.pardiralli.domain.ExportFile;
import ee.pardiralli.dto.RaceDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public interface StatisticsService {

    List<Object> getDefaultDates();

    List<List<Object>> createDataByDates(Calendar calendar, Date endDate);

    File createCSVFile(String name, ExportFile exportFile) throws FileNotFoundException;

    int getNoOfSoldDucks(Date startDate, Date endDate);

    int getAmountOfDonatedMoney(Date startDate, Date endDate);

    List<List<Object>> createDataByRace(Date startDate, Date endDate);
}
