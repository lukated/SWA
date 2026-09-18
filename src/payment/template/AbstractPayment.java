// Das Unterpaket template enthält den festen Zahlungsablauf und seine konkreten Varianten.
package payment.template;

// Objects wird verwendet, um zwingend erforderliche Abhängigkeiten und Eingaben früh zu prüfen.
import java.util.Objects;

// AuthenticationService führt den ersten gemeinsamen Schritt jeder Zahlung aus.
import authentication.AuthenticationService;
// Credential enthält das Authentifizierungsmerkmal, das vor der Buchung geprüft wird.
import authentication.Credential;
// Subject bezeichnet den Auftraggeber, der die Zahlung auslösen möchte.
import authentication.Subject;
// Account stellt Sender- und Empfängerkonto der Buchung bereit.
import payment.Account;
// CurrencyAmount enthält den exakten Betrag und die Währung der Zahlung.
import payment.CurrencyAmount;

// Die abstrakte Klasse legt den unveränderlichen Ablauf jeder Online-Zahlung als Template Method fest.
public abstract class AbstractPayment {
    // Alle Zahlungsarten verwenden denselben AuthenticationService für den gemeinsamen ersten Schritt.
    protected final AuthenticationService authenticationService;

    // Der Konstruktor erzwingt, dass jede konkrete Zahlungsart einen AuthenticationService erhält.
    protected AbstractPayment(AuthenticationService authenticationService) {
        // Eine Zahlung ohne AuthenticationService könnte den vorgeschriebenen ersten Schritt nicht ausführen.
        this.authenticationService = Objects.requireNonNull(
                authenticationService,
                "Der AuthenticationService darf nicht null sein.");
    }

    // final verhindert, dass konkrete Zahlungsarten die Reihenfolge der drei vorgeschriebenen Schritte verändern.
    public final String payAmount(
            Subject subject,
            Credential credential,
            Account sender,
            Account receiver,
            CurrencyAmount amount) {

        // Vor dem fachlichen Ablauf werden die notwendigen Zahlungsdaten auf Vollständigkeit geprüft.
        validatePaymentData(sender, receiver, amount);

        // Schritt 1 ist für alle Zahlungsarten gleich: Der Auftraggeber muss authentifiziert werden.
        if (!authenticate(subject, credential)) {
            return "Zahlung abgelehnt: Authentifizierung fehlgeschlagen.";
        }

        // Schritt 2 wird an die konkrete Zahlungsart delegiert und ist der variable Teil des Templates.
        bookAmount(sender, receiver, amount);

        // Schritt 3 ist wieder gemeinsam: Nach erfolgreicher Buchung wird eine Bestätigung erstellt.
        return createConfirmation(amount);
    }

    // Diese gemeinsame Hilfsmethode delegiert die Authentifizierung an den AuthenticationService.
    protected boolean authenticate(Subject subject, Credential credential) {
        // Der Service verwendet intern die zuvor ausgewählte AuthenticationStrategy.
        return authenticationService.authenticateSubject(subject, credential);
    }

    // Jede konkrete Zahlungsart muss ihren eigenen Buchungsschritt bereitstellen.
    protected abstract void bookAmount(Account sender, Account receiver, CurrencyAmount amount);

    // Die Bestätigung ist laut Aufgabenstellung für alle Zahlungsarten gleich aufgebaut.
    protected String createConfirmation(CurrencyAmount amount) {
        // Der Geldwert nutzt seine toString-Methode, damit Betrag und Währung gemeinsam erscheinen.
        return "Zahlung über " + amount + " wurde erfolgreich ausgeführt.";
    }

    // Diese Prüfung verhindert ungültige Eingaben, bevor Kontostände verändert werden.
    private void validatePaymentData(Account sender, Account receiver, CurrencyAmount amount) {
        // Sender, Empfänger und Betrag sind für jede Zahlung zwingend erforderlich.
        Objects.requireNonNull(sender, "Das Senderkonto darf nicht null sein.");
        Objects.requireNonNull(receiver, "Das Empfängerkonto darf nicht null sein.");
        Objects.requireNonNull(amount, "Der Zahlungsbetrag darf nicht null sein.");

        // Eine Überweisung auf dasselbe Account-Objekt hätte keine sinnvolle fachliche Wirkung.
        if (sender == receiver) {
            throw new IllegalArgumentException("Sender- und Empfängerkonto müssen verschieden sein.");
        }

        // Ein Zahlungsbetrag muss größer als null sein, damit tatsächlich eine Buchung stattfindet.
        if (amount.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Der Zahlungsbetrag muss größer als null sein.");
        }

        // Die Senderwährung muss zum Zahlungsbetrag passen, weil kein Wechselkurs implementiert ist.
        if (!sender.getBalance().getCurrencyCode().equals(amount.getCurrencyCode())) {
            throw new IllegalArgumentException("Senderkonto und Zahlungsbetrag müssen dieselbe Währung besitzen.");
        }

        // Auch das Empfängerkonto muss dieselbe Währung verwenden, bevor irgendein Konto verändert wird.
        if (!receiver.getBalance().getCurrencyCode().equals(amount.getCurrencyCode())) {
            throw new IllegalArgumentException("Empfängerkonto und Zahlungsbetrag müssen dieselbe Währung besitzen.");
        }
    }
}
