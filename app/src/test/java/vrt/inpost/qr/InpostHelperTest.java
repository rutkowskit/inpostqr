package vrt.inpost.qr;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class InpostHelperTest {

    static Stream<Arguments> validSmsMessages() {
        return Stream.of(
                Arguments.of(
                        "Mamy to! Twoja paczka czeka w Appkomacie ŁDZ00APP Łódź, Bałuty 123 do 10/04/2026 21:37. Odbierz ja uzywajac apki InPost. Nie masz apki? Zadzwon na numer +48122133470 i podaj kod: 123456.",
                        "123456"
                ),
                Arguments.of(
                        "Paczka czeka w skrytce w XXXI17X Nibylandia. do 09/03/2026 12:38. Kod odbioru 654321",
                        "654321"
                ),
                Arguments.of(
                        "Zwykły sms bez kodu",
                        null
                ),
                Arguments.of(
                        "Inny kod sześcioznakowy 126654",
                        null
                )
        );
    }

    @ParameterizedTest
    @MethodSource("validSmsMessages")
    void getReceptionCode_WhenSupportedSmsContentIsProvided_ShouldReturnCorrectCode(String sms, String expectedCode) {
        //Arrange
        SmsData data = new SmsData();
        data.Body = sms;

        // Act
        String code = InpostHelper.getReceptionCode(data);

        // Assert
        assertEquals(expectedCode,code);
    }
}