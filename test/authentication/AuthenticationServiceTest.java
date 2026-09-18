// Der Test liegt im authentication-Paket, damit seine fachliche Zuordnung direkt erkennbar bleibt.
package authentication;

// assertFalse prüft ausdrücklich den erwarteten Ablehnungsfall einer Authentifizierung.
import static org.junit.jupiter.api.Assertions.assertFalse;
// assertTrue prüft ausdrücklich den erwarteten Erfolgsfall einer Authentifizierung.
import static org.junit.jupiter.api.Assertions.assertTrue;

// Test kennzeichnet Methoden, die vom JUnit-5-Testlauf automatisch ausgeführt werden.
import org.junit.jupiter.api.Test;

// FingerPrintStrategy wird verwendet, um den Austausch der Strategy im Service zu überprüfen.
import authentication.strategy.FingerPrintStrategy;
// UserNamePasswordStrategy stellt den normalen Passwortfall des AuthenticationService bereit.
import authentication.strategy.UserNamePasswordStrategy;

// Diese Testklasse überprüft das öffentliche Verhalten des geforderten AuthenticationService-Use-Cases.
class AuthenticationServiceTest {

    // Der Test prüft gemeinsam den erfolgreichen und den abgelehnten Passwortversuch für dasselbe Subject.
    @Test
    void acceptsMatchingPasswordAndRejectsWrongPassword() {
        // Der Service wird ohne feste Strategie erzeugt, weil diese zur Laufzeit austauschbar sein soll.
        AuthenticationService service = new AuthenticationService();

        // Für diesen Test wird die konkrete Passwortstrategie ausgewählt.
        service.setStrategy(new UserNamePasswordStrategy());

        // Das Subject stellt den Benutzer dar, dessen gespeichertes Passwort später geprüft wird.
        Subject subject = new Subject("Carl", SubjectType.NATURAL_PERSON);

        // Dieses Credential repräsentiert das gültige, im Subject hinterlegte Passwort.
        Credential storedPassword = new Credential(CredentialType.PASSWORD, "geheim123");

        // Das gültige Passwort wird mit dem Subject verknüpft und bildet die Vergleichsgrundlage.
        subject.addCredential(storedPassword);

        // Ein neues Credential mit gleichem Typ und Wert simuliert die korrekte Benutzereingabe.
        Credential correctInput = new Credential(CredentialType.PASSWORD, "geheim123");

        // Ein abweichender Wert simuliert einen realen fehlgeschlagenen Anmeldeversuch.
        Credential wrongInput = new Credential(CredentialType.PASSWORD, "falsch");

        // Die korrekte Eingabe muss vom AuthenticationService akzeptiert werden.
        assertTrue(service.authenticateSubject(subject, correctInput));

        // Die falsche Eingabe muss abgelehnt werden, obwohl Subject und CredentialType gleich bleiben.
        assertFalse(service.authenticateSubject(subject, wrongInput));
    }

    // Dieser Test zeigt, dass derselbe Service ohne eigene Änderung eine andere Strategy verwenden kann.
    @Test
    void authenticatesWithFingerprintStrategy() {
        // Derselbe Servicetyp wird für eine zweite Authentifizierungsart verwendet.
        AuthenticationService service = new AuthenticationService();

        // Die FingerPrintStrategy ersetzt die Passwortstrategie über denselben Setter.
        service.setStrategy(new FingerPrintStrategy());

        // Das Subject erhält für diesen Test einen gespeicherten Fingerabdruck.
        Subject subject = new Subject("Software-Terminal", SubjectType.SOFTWARE_SYSTEM);

        // Der String simuliert den gespeicherten Fingerabdruckwert im Hauptspeicher.
        subject.addCredential(new Credential(CredentialType.FINGERPRINT, "fingerprint-4711"));

        // Die Eingabe besitzt denselben Typ und Wert wie das hinterlegte Credential.
        Credential fingerprintInput = new Credential(
                CredentialType.FINGERPRINT,
                "fingerprint-4711");

        // Der erfolgreiche Test beweist, dass der Service gegen das Strategy-Interface arbeitet.
        assertTrue(service.authenticateSubject(subject, fingerprintInput));
    }
}
