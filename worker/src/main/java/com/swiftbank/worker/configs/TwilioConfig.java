package com.swiftbank.worker.configs;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:application.yml")
public class TwilioConfig {

    @Value("${twilio.account-sid}")
    private String accountSID;

    @Value("${twilio.auth-token}")
    private String authToken;


    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSID, authToken);
    }
}
