package ee.pardiralli.service;

import ee.pardiralli.db.*;
import ee.pardiralli.domain.*;
import ee.pardiralli.dto.InsertionDTO;
import ee.pardiralli.util.BanklinkUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j
public class InsertionServiceImpl implements InsertionService {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;
    private final OwnerRepository ownerRepository;
    private final BuyerRepository buyerRepository;
    private final TransactionRepository transactionRepository;
    private final MailService mailService;
    private final SerialNumberService numberService;


    @Override
    //TODO: will be void
    public Boolean saveInsertion(InsertionDTO insertionDTO) {
        log.info("Inserting ducks from " + insertionDTO);
        List<Duck> duckList = new ArrayList<>();

        Race race = raceRepository.findRaceByIsOpen(true);
        //TODO: if null throw exception

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
        transaction.setTimeOfPayment(BanklinkUtil.getCurrentTimeStamp());
        transaction = transactionRepository.save(transaction);

        for (int i = 0; i < insertionDTO.getNumberOfDucks(); i++) {
            Duck duck = new Duck();
            duck.setDateOfPurchase(BanklinkUtil.getCurrentDate());
            duck.setDuckBuyer(duckBuyer);
            duck.setDuckOwner(duckOwner);
            duck.setRace(race);
            duck.setTransaction(transaction);
            duck.setPriceCents(insertionDTO.getPriceOfOneDuck() * 100);
            duck.setTimeOfPurchase(BanklinkUtil.getCurrentTimeStamp());
            duck.setSerialNumber(numberService.getSerial());

            log.info("Saving duck " + duck);
            duckList.add(duckRepository.save(duck));
        }

        //TODO: if false throw excpetion, also change controller.
        Boolean sentMail = mailService.sendConfirmationEmail(duckBuyer, duckList);
        if (sentMail) {
            log.info("Confirmation email sent");
        } else {
            log.error("Failed to send confirmation email");
        }

        return sentMail;
    }
}
