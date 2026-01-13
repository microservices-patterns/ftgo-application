package net.chrisrichardson.ftgo.accountingservice.web;

import net.chrisrichardson.ftgo.accountingservice.domain.AccountServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AccountServiceConfiguration.class)
@ComponentScan
public class AccountingWebConfiguration {
}
