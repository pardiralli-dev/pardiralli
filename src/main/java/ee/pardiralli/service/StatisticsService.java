package ee.pardiralli.service;

import ee.pardiralli.domain.ExportFile;
import ee.pardiralli.dto.RaceDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public interface StatisticsService {

    public List<Object> getDefaultDates();

    public List<List<Object>> createDataByDates(Calendar calendar, Date endDate);

    public File createCSVFile(String name, ExportFile exportFile) throws FileNotFoundException;

    public int getNoOfSoldDucks(Date startDate, Date endDate);

    public int getAmountOfDonatedMoney(Date startDate, Date endDate);

    public List<List<Object>> createDataByRace(RaceDTO race);
}
