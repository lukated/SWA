// Die Klasse gehört zum payment-Paket, weil Accounts die Sender und Empfänger einer Zahlung darstellen.
package payment;

// Objects wird verwendet, um zwingend benötigte Konstruktorwerte früh und verständlich zu prüfen.
import java.util.Objects;

// Ein Account besitzt eine Identität, einen Eigentümer und einen veränderlichen Kontostand.
public class Account {
    // Die Account-ID identifiziert das Konto eindeutig bei Auswahl und Ausgabe.
    private final String accountId;

    // Der Eigentümername macht sichtbar, wem der Account gehört.
    private final String ownerName;

    // Der Kontostand ist veränderlich, weil Zahlungen ihn durch debit und credit anpassen.
    private CurrencyAmount balance;

    // Der Konstruktor verlangt alle Daten, die für einen verwendbaren Account notwendig sind.
    public Account(String accountId, String ownerName, CurrencyAmount balance) {
        // Eine leere ID würde eine zuverlässige Auswahl des Accounts verhindern.
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Die Account-ID darf nicht leer sein.");
        }

        // Ein fehlender Eigentümer würde die fachliche Zuordnung des Accounts verlieren.
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("Der Eigentümername darf nicht leer sein.");
        }

        // Die geprüfte ID wird dauerhaft gespeichert, weil sie sich nach der Kontoerstellung nicht ändern soll.
        this.accountId = accountId;

        // Der geprüfte Eigentümername wird ebenfalls als fester Bestandteil des Accounts gespeichert.
        this.ownerName = ownerName;

        // Jeder Account benötigt einen Anfangsbestand, damit Abbuchungen und Gutschriften möglich sind.
        this.balance = Objects.requireNonNull(balance, "Der Kontostand darf nicht null sein.");
    }

    // debit bildet die Belastung des Senderkontos während einer Zahlung ab.
    public void debit(CurrencyAmount amount) {
        // Ohne Betrag kann keine Abbuchung berechnet werden.
        Objects.requireNonNull(amount, "Der abzubuchende Betrag darf nicht null sein.");

        // Vor der Subtraktion wird geprüft, ob ausreichend Guthaben vorhanden ist.
        if (balance.getAmount().compareTo(amount.getAmount()) < 0) {
            throw new IllegalStateException("Das Guthaben des Senderkontos reicht nicht aus.");
        }

        // CurrencyAmount.subtract prüft zusätzlich die Währung und liefert einen neuen Kontostand.
        balance = balance.subtract(amount);
    }

    // credit bildet die Gutschrift des Zahlungsbetrags auf dem Empfängerkonto ab.
    public void credit(CurrencyAmount amount) {
        // Ohne Betrag kann keine Gutschrift berechnet werden.
        Objects.requireNonNull(amount, "Der gutzuschreibende Betrag darf nicht null sein.");

        // CurrencyAmount.add prüft die Währung und liefert den neuen erhöhten Kontostand.
        balance = balance.add(amount);
    }

    // Die ID wird für die Auswahl und verständliche Ausgabe eines Accounts benötigt.
    public String getAccountId() {
        return accountId;
    }

    // Der Eigentümername wird für die Anzeige des Accounts benötigt.
    public String getOwnerName() {
        return ownerName;
    }

    // Der aktuelle Kontostand wird für Prüfungen, Zahlungen und Konsolenausgaben bereitgestellt.
    public CurrencyAmount getBalance() {
        return balance;
    }
}
