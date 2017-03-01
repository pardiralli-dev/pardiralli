package ee.pardiralli.service;

import ee.pardiralli.db.*;
import ee.pardiralli.domain.*;
import ee.pardiralli.dto.InsertionDTO;
import ee.pardiralli.exceptions.RaceNotFoundException;
import ee.pardiralli.util.BanklinkUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class InsertionServiceImpl implements InsertionService {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;
    private final OwnerRepository ownerRepository;
    private final BuyerRepository buyerRepository;
    private final TransactionRepository transactionRepository;
    private final MailService mailService;
    private final SerialNumberService numberService;

    @Override
    public void saveInsertion(InsertionDTO insertionDTO) throws RaceNotFoundException, MessagingException {
        log.info("Inserting ducks from {}", insertionDTO.toString());
        List<Duck> duckList = new ArrayList<>();

        Race race = raceRepository.findRaceByIsOpen(true);
        if (race == null) throw new RaceNotFoundException();
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
        transaction.setTimeOfPayment(BanklinkUtil.getCurrentTimestamp());
        transaction = transactionRepository.save(transaction);

        for (int i = 0; i < insertionDTO.getNumberOfDucks(); i++) {
            Duck duck = new Duck();
            duck.setDateOfPurchase(BanklinkUtil.getCurrentDate());
            duck.setDuckBuyer(duckBuyer);
            duck.setDuckOwner(duckOwner);
            duck.setRace(race);
            duck.setTransaction(transaction);
            duck.setPriceCents(insertionDTO.getPriceOfOneDuck() * 100);
            duck.setTimeOfPurchase(BanklinkUtil.getCurrentTimestamp());
            duck.setSerialNumber(numberService.getSerial());

            log.info("Saving duck {}", duck.toString());
            duckList.add(duckRepository.save(duck));
        }

        mailService.sendConfirmationEmail(duckBuyer, duckList);
    }
}
