package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;


class AppReplanDeliveryTest {

    private DataGenerator.UserInfo validUser;

    @BeforeEach
    void setup() {
        Configuration.browserCapabilities = new ChromeOptions().setBrowserVersion("115");
        open("http://localhost:9999");
        validUser = DataGenerator.Registration.generateUser("ru");

    }

    @Test
    void shouldSuccessfulPlanAndReplanMeeting() {
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        fillAndSubmitForm(firstMeetingDate);
        checkSuccessNotification(firstMeetingDate);

        updateAndSubmitForm(secondMeetingDate);
        confirmReplanning();
        checkSuccessNotification(secondMeetingDate);
    }

    private void fillAndSubmitForm(String meetingDate) {
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(meetingDate);
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
    }

    private void updateAndSubmitForm(String newDate) {
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(newDate);
        $(".button").click();
    }

    private void checkSuccessNotification(String date) {
        $("[data-test-id=success-notification] .notification__content")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.exactText("Встреча успешно запланирована на " + date));
    }

    private void confirmReplanning() {
        $("[data-test-id=replan-notification] .notification__content")
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $("[data-test-id=replan-notification] button").click();
    }
}