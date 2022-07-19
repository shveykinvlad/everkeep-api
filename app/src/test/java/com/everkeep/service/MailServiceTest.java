package com.everkeep.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.everkeep.AbstractTest;
import com.everkeep.config.properties.IntegrationProperties;
import com.everkeep.config.properties.MailProperties;

@SpringBootTest(classes = {
        MailService.class,
        MessageSourceAutoConfiguration.class
})
class MailServiceTest extends AbstractTest {

    @Autowired
    private MailService mailService;
    @Autowired
    private MessageSource messageSource;
    @MockBean
    private MailProperties mailProperties;
    @MockBean
    private IntegrationProperties integrationProperties;
    @MockBean
    private JavaMailSender javaMailSender;
    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailCaptor;

    @Test
    void sendConfirmationMail() {
        var mailTo = "mailTo@example.com";
        var mailFrom = "mailFrom@example.com";
        var uiUrl = "http://localhost:4200";
        var tokenValue = UUID.randomUUID().toString();
        when(integrationProperties.uiUrl()).thenReturn(uiUrl);
        when(mailProperties.username()).thenReturn(mailFrom);

        mailService.sendConfirmationMail(mailTo, tokenValue);

        verify(javaMailSender).send(mailCaptor.capture());
        var sentMail = mailCaptor.getValue();
        assertAll("Should capture confirmation mail",
                () -> assertEquals(mailFrom, sentMail.getFrom()),
                () -> assertEquals("Email confirmation", sentMail.getSubject()),
                () -> assertEquals(MessageFormat.format("{0}/users/confirm?token={1}", uiUrl, tokenValue), sentMail.getText()),
                () -> assertArrayEquals(new String[]{mailTo}, sentMail.getTo())
        );
    }

    @Test
    void sendResetPasswordMail() {
        var mailTo = "mailTo@example.com";
        var mailFrom = "mailFrom@example.com";
        var uiUrl = "http://localhost:4200";
        var tokenValue = UUID.randomUUID().toString();
        when(integrationProperties.uiUrl()).thenReturn(uiUrl);
        when(mailProperties.username()).thenReturn(mailFrom);

        mailService.sendResetPasswordMail(mailTo, tokenValue);

        verify(javaMailSender).send(mailCaptor.capture());
        var sentMail = mailCaptor.getValue();
        assertAll("Should capture reset password mail",
                () -> assertEquals(mailFrom, sentMail.getFrom()),
                () -> assertEquals("Password reset", sentMail.getSubject()),
                () -> assertEquals(MessageFormat.format("{0}/users/password/update?email={1}&token={2}", uiUrl, mailTo, tokenValue),
                        sentMail.getText()),
                () -> assertArrayEquals(new String[]{mailTo}, sentMail.getTo())
        );
    }
}
