package ee.pardiralli.service;

import ee.pardiralli.db.SystemParamRepository;
import ee.pardiralli.model.Duck;
import ee.pardiralli.model.SysParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SystemPropertyService {
    private static final String DEFAULT_PRICE = "sys_param_def_price";
    private final SystemParamRepository paramRepository;

    public Integer getDefaultDuckPrice() {
        return paramRepository.existsById(DEFAULT_PRICE) ? paramRepository.getOne(DEFAULT_PRICE).getParam() : Duck.MINIMUM_PRICE;
    }

    public void setDefaultDuckPrice(Integer price) {
        log.info("Setting global duck price to {}", price);
        paramRepository.save(new SysParam(DEFAULT_PRICE, price));
    }
}
