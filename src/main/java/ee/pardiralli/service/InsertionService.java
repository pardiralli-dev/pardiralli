package ee.pardiralli.service;

import ee.pardiralli.db.*;
import ee.pardiralli.dto.InsertionDTO;
import ee.pardiralli.dto.PurchaseInfoDTO;
import ee.pardiralli.exceptions.IllegalTransactionException;
import ee.pardiralli.exceptions.RaceNotFoundException;
import ee.pardiralli.model.*;
import ee.pardiralli.util.BanklinkUtil;
import ee.pardiralli.util.GeneralUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class InsertionService {
    private final DuckRepository duckRepository;
    private final RaceRepository raceRepository;
    private final OwnerRepository ownerRepository;
    private final BuyerRepository buyerRepository;
    private final TransactionRepository transactionRepository;
    private final MailService mailService;
    private final SerialNumberService numberService;
    private final PaymentService paymentService;

    /**
     * Save data provided by dto into the database
     *
     * @return saved ducks
     */
    public List<Duck> saveInsertion(InsertionDTO insertionDTO, Principal principal) throws RaceNotFoundException, MessagingException {
        log.info("Inserting ducks from {}", insertionDTO.toString());
        List<Duck> duckList = new ArrayList<>();

        Race race = raceRepository.findRaceByIsOpen(true);
        if (race == null) throw new RaceNotFoundException();

        DuckBuyer duckBuyer = new DuckBuyer();
        duckBuyer.setIdentificationCode(insertionDTO.getIdentificationCode());
        duckBuyer.setEmail(insertionDTO.getBuyerEmail());
        duckBuyer = buyerRepository.save(duckBuyer);

        DuckOwner duckOwner = new DuckOwner();
        duckOwner.setFirstName(insertionDTO.getOwnerFirstName());
        duckOwner.setLastName(insertionDTO.getOwnerLastName());
        duckOwner.setPhoneNumber(GeneralUtil.cleanPhoneNumber(insertionDTO.getOwnerPhoneNumber()));
        duckOwner = ownerRepository.save(duckOwner);

        Transaction transaction = new Transaction();
        transaction.setIsPaid(true);
        transaction.setTimeOfPayment(BanklinkUtil.getCurrentTimestamp());
        transaction.setInserter(principal.getName());
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

        try {
            PurchaseInfoDTO purchaseInfoDTO = new PurchaseInfoDTO(
                    BanklinkUtil.ducksToDTO(duckList),
                    duckBuyer.getEmail(),
                    paymentService.transactionAmount(transaction.getId()),
                    String.valueOf(transaction.getId()));
            mailService.sendConfirmationEmail(purchaseInfoDTO);
        } catch (IllegalTransactionException e) {
            log.error("Manual insertion of ducks failed with transaction ID {}", transaction.getId());
            throw new RuntimeException(e);
        }

        return duckList;
    }
}
