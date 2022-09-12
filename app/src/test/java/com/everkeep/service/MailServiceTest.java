package com.everkeep.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.everkeep.AbstractTest;
import com.everkeep.config.properties.IntegrationProperties;
import com.everkeep.config.properties.MailProperties;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        MailService.class,
        MessageSourceAutoConfiguration.class
})
class MailServiceTest extends AbstractTest {

    @Autowired
    private MailService mailService;
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
        var mailFrom = "sender@localhost";
        var mailTo = "receiver@localhost";
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
                () -> assertEquals(
                        "%s/users/confirmation?token=%s".formatted(uiUrl, tokenValue),
                        sentMail.getText()
                ),
                () -> assertArrayEquals(new String[]{mailTo}, sentMail.getTo())
        );
    }

    @Test
    void sendResetPasswordMail() {
        var mailFrom = "sender@localhost";
        var mailTo = "receiver@localhost";
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
                () -> assertEquals(
                        "%s/users/password/update?email=%s&token=%s".formatted(uiUrl, mailTo, tokenValue),
                        sentMail.getText()
                ),
                () -> assertArrayEquals(new String[]{mailTo}, sentMail.getTo())
        );
    }
}
