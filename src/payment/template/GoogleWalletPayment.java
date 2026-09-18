// Die Klasse liegt im template-Paket, weil sie den variablen Schritt des Zahlungs-Templates umsetzt.
package payment.template;

// AuthenticationService wird für den gemeinsamen Authentifizierungsschritt an die Oberklasse weitergereicht.
import authentication.AuthenticationService;
// Account stellt Sender und Empfänger für den Google-Wallet-Buchungsschritt bereit.
import payment.Account;
// CurrencyAmount enthält den zu übertragenden Betrag und seine Währung.
import payment.CurrencyAmount;

// Diese Klasse repräsentiert die konkrete Template-Method-Variante für Google Wallet.
public class GoogleWalletPayment extends AbstractPayment {

    // Der Konstruktor verbindet die Google-Wallet-Variante mit dem gemeinsamen AuthenticationService.
    public GoogleWalletPayment(AuthenticationService authenticationService) {
        // super speichert den Service in AbstractPayment, damit der feste Ablauf ihn verwenden kann.
        super(authenticationService);
    }

    // Override implementiert den Google-Wallet-spezifischen zweiten Schritt des Templates.
    @Override
    protected void bookAmount(Account sender, Account receiver, CurrencyAmount amount) {
        // Die Simulation belastet das Senderkonto über die geschützte Account-Methode.
        sender.debit(amount);

        // Nach erfolgreicher Belastung wird der Betrag dem Empfängerkonto gutgeschrieben.
        receiver.credit(amount);
    }
}
