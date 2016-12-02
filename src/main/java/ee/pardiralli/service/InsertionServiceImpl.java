package ee.pardiralli.service;

import ee.pardiralli.db.*;
import ee.pardiralli.domain.*;
import ee.pardiralli.dto.InsertionDTO;
import ee.pardiralli.util.BanklinkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final SerialNumberService numberService;

    @Autowired
    public InsertionServiceImpl(DuckRepository duckRepository,
                                RaceRepository raceRepository,
                                OwnerRepository ownerRepository,
                                BuyerRepository buyerRepository,
                                TransactionRepository transactionRepository,
                                MailService mailService, SerialNumberService numberService) {

        this.duckRepository = duckRepository;
        this.raceRepository = raceRepository;
        this.ownerRepository = ownerRepository;
        this.buyerRepository = buyerRepository;
        this.transactionRepository = transactionRepository;
        this.mailService = mailService;
        this.numberService = numberService;
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
            duck.setRace(race);
            duck.setTransaction(transaction);
            duck.setPriceCents(insertionDTO.getPriceOfOneDuck() * 100);
            duck.setTimeOfPurchase(BanklinkUtils.getCurrentTimeStamp());
            duck.setSerialNumber(numberService.getSerial());

            duckList.add(duckRepository.save(duck));
        }
        return mailService.sendConfirmationEmail(duckBuyer, duckList);
    }

    @Override
    public boolean OpenedRaceExists() {
        return raceRepository.countOpenedRaces() == 1;
    }
}
