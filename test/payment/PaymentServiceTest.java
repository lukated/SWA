// Der Test liegt im payment-Paket, damit seine fachliche Zuordnung direkt erkennbar bleibt.
package payment;

// assertEquals vergleicht erwartete Bestätigungen und Kontostände mit den tatsächlichen Ergebnissen.
import static org.junit.jupiter.api.Assertions.assertEquals;

// BigDecimal erzeugt exakte Testbeträge ohne Rundungsfehler.
import java.math.BigDecimal;

// Test kennzeichnet Methoden, die vom JUnit-5-Testlauf automatisch ausgeführt werden.
import org.junit.jupiter.api.Test;

// AuthenticationService wird benötigt, weil jede Zahlung mit der Authentifizierung beginnt.
import authentication.AuthenticationService;
// Credential stellt sowohl das gespeicherte als auch das eingegebene Passwort dar.
import authentication.Credential;
// CredentialType kennzeichnet die Test-Credentials als Passwort.
import authentication.CredentialType;
// Subject bezeichnet den authentifizierten Auftraggeber der Testzahlung.
import authentication.Subject;
// SubjectType ordnet den Auftraggeber als natürliche Person ein.
import authentication.SubjectType;
// UserNamePasswordStrategy führt die Authentifizierung innerhalb der Testzahlung aus.
import authentication.strategy.UserNamePasswordStrategy;

// Diese Testklasse überprüft den vollständigen öffentlichen payAmount-Use-Case des PaymentService.
class PaymentServiceTest {

    // Der Test prüft den bestätigten Apple-Pay-Ablauf einschließlich beider Kontostandsänderungen.
    @Test
    void transfersAmountWithApplePayAfterSuccessfulAuthentication() {
        // Der AuthenticationService erhält die Strategie, die vor der Zahlung verwendet werden soll.
        AuthenticationService authenticationService = new AuthenticationService();
        authenticationService.setStrategy(new UserNamePasswordStrategy());

        // Der Auftraggeber besitzt das gültige Passwort, das den ersten Template-Schritt erfolgreich macht.
        Subject subject = new Subject("Carl", SubjectType.NATURAL_PERSON);
        subject.addCredential(new Credential(CredentialType.PASSWORD, "geheim123"));

        // Das Senderkonto beginnt mit 100 EUR und besitzt ausreichend Guthaben für die Testzahlung.
        Account sender = new Account(
                "S-1",
                "Carl",
                new CurrencyAmount(new BigDecimal("100.00"), "EUR"));

        // Das Empfängerkonto beginnt mit 20 EUR und verwendet dieselbe Währung.
        Account receiver = new Account(
                "E-1",
                "Autovermietung",
                new CurrencyAmount(new BigDecimal("20.00"), "EUR"));

        // Der PaymentService verwendet denselben AuthenticationService wie der spätere Programmablauf.
        PaymentService paymentService = new PaymentService(authenticationService);

        // Die Methode wird mit der von der Gruppe bestätigten Zahlungsart APPLE_PAY ausgeführt.
        String confirmation = paymentService.payAmount(
                PaymentType.APPLE_PAY,
                subject,
                new Credential(CredentialType.PASSWORD, "geheim123"),
                sender,
                receiver,
                new CurrencyAmount(new BigDecimal("25.00"), "EUR"));

        // Schritt 3 des Templates muss die erwartete gemeinsame Zahlungsbestätigung liefern.
        assertEquals("Zahlung über 25.00 EUR wurde erfolgreich ausgeführt.", confirmation);

        // Vom Sender müssen exakt 25 EUR abgezogen worden sein.
        assertEquals(new BigDecimal("75.00"), sender.getBalance().getAmount());

        // Dem Empfänger müssen dieselben 25 EUR gutgeschrieben worden sein.
        assertEquals(new BigDecimal("45.00"), receiver.getBalance().getAmount());
    }

    // Der Test stellt sicher, dass eine fehlgeschlagene Authentifizierung keinerlei Geld bewegt.
    @Test
    void leavesBalancesUnchangedWhenAuthenticationFails() {
        // Auch dieser Test verwendet die Passwortstrategie, aber später einen falschen Eingabewert.
        AuthenticationService authenticationService = new AuthenticationService();
        authenticationService.setStrategy(new UserNamePasswordStrategy());

        // Nur das gespeicherte Passwort geheim123 ist für dieses Subject gültig.
        Subject subject = new Subject("Carl", SubjectType.NATURAL_PERSON);
        subject.addCredential(new Credential(CredentialType.PASSWORD, "geheim123"));

        // Beide Konten erhalten feste Anfangswerte, die nach der Ablehnung unverändert bleiben müssen.
        Account sender = new Account(
                "S-1",
                "Carl",
                new CurrencyAmount(new BigDecimal("100.00"), "EUR"));
        Account receiver = new Account(
                "E-1",
                "Autovermietung",
                new CurrencyAmount(new BigDecimal("20.00"), "EUR"));

        // Der PaymentService wird wie im erfolgreichen Fall mit Authentication verbunden.
        PaymentService paymentService = new PaymentService(authenticationService);

        // Das falsche Passwort muss den Ablauf vor dem variablen Buchungsschritt beenden.
        String result = paymentService.payAmount(
                PaymentType.APPLE_PAY,
                subject,
                new Credential(CredentialType.PASSWORD, "falsch"),
                sender,
                receiver,
                new CurrencyAmount(new BigDecimal("25.00"), "EUR"));

        // Die Rückgabe muss die fehlgeschlagene Authentifizierung eindeutig mitteilen.
        assertEquals("Zahlung abgelehnt: Authentifizierung fehlgeschlagen.", result);

        // Ohne erfolgreiche Authentifizierung darf das Senderkonto nicht belastet werden.
        assertEquals(new BigDecimal("100.00"), sender.getBalance().getAmount());

        // Ebenso darf das Empfängerkonto keine Gutschrift erhalten.
        assertEquals(new BigDecimal("20.00"), receiver.getBalance().getAmount());
    }
}
