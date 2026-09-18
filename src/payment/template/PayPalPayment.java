// Die Klasse liegt im template-Paket, weil sie den variablen Schritt des Zahlungs-Templates umsetzt.
package payment.template;

// AuthenticationService wird an die abstrakte Oberklasse für den gemeinsamen ersten Schritt weitergereicht.
import authentication.AuthenticationService;
// Account stellt Sender und Empfänger für den PayPal-Buchungsschritt bereit.
import payment.Account;
// CurrencyAmount enthält den zu übertragenden Betrag und seine Währung.
import payment.CurrencyAmount;

// Diese Klasse repräsentiert die konkrete Template-Method-Variante für PayPal.
public class PayPalPayment extends AbstractPayment {

    // Der Konstruktor verbindet auch die PayPal-Variante mit dem gemeinsamen AuthenticationService.
    public PayPalPayment(AuthenticationService authenticationService) {
        // super ruft den Konstruktor von AbstractPayment auf und speichert dort den Service.
        super(authenticationService);
    }

    // Override implementiert ausschließlich den laut Aufgabenstellung variablen zweiten Schritt.
    @Override
    protected void bookAmount(Account sender, Account receiver, CurrencyAmount amount) {
        // In der Hauptspeicher-Simulation belastet PayPal zuerst das Senderkonto.
        sender.debit(amount);

        // Danach wird derselbe Betrag dem Empfängerkonto gutgeschrieben.
        receiver.credit(amount);
    }
}
