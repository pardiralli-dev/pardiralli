package ee.pardiralli.service;

import ee.pardiralli.db.SystemParamRepository;
import ee.pardiralli.model.Duck;
import ee.pardiralli.model.SysParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SystemPropertyService {
    private final SystemParamRepository paramRepository;

    public Integer getDefaultDuckPrice() {
        return Integer.valueOf(getProperty(SysKey.DEFAULT_PRICE).orElse(String.valueOf(Duck.MINIMUM_PRICE)));
    }

    public void setDefaultDuckPrice(Integer price) {
        log.info("Setting global duck price to {}", price);
        setProperty(SysKey.DEFAULT_PRICE, String.valueOf(price));
    }

    public Optional<String> getProperty(SysKey sysKey) {
        Optional<SysParam> param = paramRepository.findById(sysKey.value);
        return param.map(SysParam::getValue);
    }

    public void setProperty(SysKey sysKey, String value) {
        log.info("Setting {} to {}", sysKey.value, value);
        paramRepository.save(new SysParam(sysKey.value, value));
    }

    public enum SysKey {
        DEFAULT_PRICE("duck-default-price"),
        FRONT_PAGE_INFO("front-page-info");

        private final String value;

        SysKey(String value) {
            this.value = value;
        }
    }
}