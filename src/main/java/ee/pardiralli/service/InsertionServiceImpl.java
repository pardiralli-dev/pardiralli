package ee.pardiralli.service;

import ee.pardiralli.db.*;
import ee.pardiralli.domain.*;
import ee.pardiralli.dto.InsertionDTO;
import ee.pardiralli.util.BanklinkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class InsertionServiceImpl implements InsertionService {

    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;
    private final OwnerRepository ownerRepository;
    private final BuyerRepository buyerRepository;
    private final TransactionRepository transactionRepository;
    private final MailService mailService;

    @Autowired
    public InsertionServiceImpl(DuckRepository duckRepository,
                                RaceRepository raceRepository,
                                OwnerRepository ownerRepository,
                                BuyerRepository buyerRepository,
                                TransactionRepository transactionRepository,
                                MailService mailService) {

        this.duckRepository = duckRepository;
        this.raceRepository = raceRepository;
        this.ownerRepository = ownerRepository;
        this.buyerRepository = buyerRepository;
        this.transactionRepository = transactionRepository;
        this.mailService = mailService;
    }


    @Override
    public Boolean saveInsertion(InsertionDTO insertionDTO) {
        List<Duck> duckList = new ArrayList<>();

        Race race = raceRepository.findRaceByIsOpen(true);

        DuckBuyer duckBuyer = new DuckBuyer();

        duckBuyer.setEmail(insertionDTO.getBuyerEmail());
        duckBuyer = buyerRepository.save(duckBuyer);


        DuckOwner duckOwner = new DuckOwner();
        duckOwner.setFirstName(insertionDTO.getOwnerFirstName());
        duckOwner.setLastName(insertionDTO.getOwnerLastName());
        duckOwner.setPhoneNumber(insertionDTO.getOwnerPhoneNumber());
        duckOwner = ownerRepository.save(duckOwner);

        Transaction transaction = new Transaction();
        transaction.setIsPaid(true);
        transaction.setTimeOfPayment(BanklinkUtils.getCurrentTimeStamp());
        transaction = transactionRepository.save(transaction);


        for (int i = 0; i < insertionDTO.getNumberOfDucks(); i++) {
            Duck duck = new Duck();
            duck.setDateOfPurchase(BanklinkUtils.getCurrentDate());
            duck.setDuckBuyer(duckBuyer);
            duck.setDuckOwner(duckOwner);
            duck.setTransaction(transaction);
            duck.setPriceCents(insertionDTO.getPriceOfOneDuck());
            duck.setTimeOfPurchase(BanklinkUtils.getCurrentTimeStamp());


            duck.setSerialNumber(duckRepository.addDuckReturnId(
                    duck.getDateOfPurchase(),
                    duckOwner.getFirstName(),
                    duckOwner.getLastName(),
                    duckOwner.getPhoneNumber(),
                    duckBuyer.getEmail(),
                    duckOwner.getPhoneNumber(),
                    race.getId(),
                    duck.getTimeOfPurchase(),
                    duck.getPriceCents(),
                    duck.getTransaction().getId()));

            duckList.add(duck);
        }
        return mailService.sendConfirmationEmail(duckBuyer,duckList);
    }

    @Override
    public boolean OpenedRaceExists() {
        return raceRepository.countOpenedRaces() == 1;
    }
}
