package com.lkksoftdev.registrationservice.otp;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AwsClientService {
    private final AmazonSNS snsClient;
    private final AmazonSimpleEmailService sesClient;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final Map<String, MessageAttributeValue> smsAttributes;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    public AwsClientService(AmazonSNS snsClient, AmazonSimpleEmailService sesClient) {
        this.snsClient = snsClient;
        this.sesClient = sesClient;

        this.smsAttributes = Map.of("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("VSBBS") // The sender ID shown on the device.
                .withDataType("String"));
    }

    public void sendOtpToMobile(String mobile, String snsTextMessage) {
        LOGGER.info("Sending OTP to mobile: {}", mobile);

        snsClient.publish(new PublishRequest().withMessage(snsTextMessage)
                .withPhoneNumber(mobile)
                .withMessageAttributes(smsAttributes));
    }

    public void sendOtpToEmail(String toEmail, String subject, String body) {
        LOGGER.info("Sending OTP to email: {}", toEmail);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(toEmail))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(body)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(subject)))
                .withSource(fromEmail);

        sesClient.sendEmail(request);
    }
}
