package net.chrisrichardson.ftgo.accountingservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.common.json.mapper.JSonMapper;
import net.chrisrichardson.ftgo.accountingservice.domain.AccountServiceConfiguration;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import org.springframework.context.annotation.*;

@Configuration
@Import({AccountServiceConfiguration.class, CommonConfiguration.class})
@ComponentScan
public class AccountingWebConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JSonMapper.objectMapper;
    }
}
