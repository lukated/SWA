// Der Service gehört zum payment-Paket, weil er den geforderten Use Case payAmount bereitstellt.
package payment;

// Objects wird verwendet, um zwingend erforderliche Abhängigkeiten und Eingaben früh zu prüfen.
import java.util.Objects;

// AuthenticationService wird an jeden konkreten Payment-Prozessor weitergegeben.
import authentication.AuthenticationService;
// Credential enthält das Authentifizierungsmerkmal des Auftraggebers.
import authentication.Credential;
// Subject bezeichnet den Auftraggeber, der vor der Zahlung authentifiziert wird.
import authentication.Subject;
// AbstractPayment ist der gemeinsame Typ aller konkreten Template-Method-Varianten.
import payment.template.AbstractPayment;
// ApplePayPayment führt die aktuelle APPLE_PAY-Variante aus dem UML aus.
import payment.template.ApplePayPayment;
// GoogleWalletPayment führt die GOOGLE_WALLET-Variante aus.
import payment.template.GoogleWalletPayment;
// PayPalPayment führt die PAYPAL-Variante aus.
import payment.template.PayPalPayment;

// Der PaymentService wählt anhand des PaymentType den passenden Zahlungsprozessor aus.
public class PaymentService {
    // Derselbe AuthenticationService wird von allen Zahlungsarten für Schritt 1 verwendet.
    private final AuthenticationService authenticationService;

    // Der Konstruktor macht die direkte Abhängigkeit von Payment zu Authentication sichtbar.
    public PaymentService(AuthenticationService authenticationService) {
        // Ohne AuthenticationService könnte keine Zahlung den Auftraggeber prüfen.
        this.authenticationService = Objects.requireNonNull(
                authenticationService,
                "Der AuthenticationService darf nicht null sein.");
    }

    // Diese Methode bildet den geforderten Payment-Use-Case payAmount ab.
    public String payAmount(
            PaymentType type,
            Subject subject,
            Credential credential,
            Account sender,
            Account receiver,
            CurrencyAmount amount) {

        // Der PaymentType bestimmt, welche konkrete Implementierung des variablen Buchungsschritts verwendet wird.
        AbstractPayment paymentProcessor = createPaymentProcessor(type);

        // Der konkrete Prozessor führt den festen Ablauf aus Authentication, Buchung und Bestätigung aus.
        return paymentProcessor.payAmount(subject, credential, sender, receiver, amount);
    }

    // Diese Factory-Hilfsmethode kapselt die Zuordnung von PaymentType zu konkreter Payment-Klasse.
    private AbstractPayment createPaymentProcessor(PaymentType type) {
        // Ohne PaymentType kann keine konkrete Zahlungsart ausgewählt werden.
        Objects.requireNonNull(type, "Der PaymentType darf nicht null sein.");

        // Der switch ordnet jeden erlaubten Enum-Wert genau einer Template-Method-Variante zu.
        switch (type) {
            case PAYPAL:
                return new PayPalPayment(authenticationService);
            case GOOGLE_WALLET:
                return new GoogleWalletPayment(authenticationService);
            case APPLE_PAY:
                return new ApplePayPayment(authenticationService);
            default:
                // Der Default-Zweig schützt den Service bei später ergänzten, aber noch nicht implementierten Typen.
                throw new IllegalArgumentException("Nicht unterstützte Zahlungsart: " + type);
        }
    }
}
