// Die Klasse gehört zum payment-Paket, weil sie Geldbeträge für Konten und Zahlungen beschreibt.
package payment;

// BigDecimal wird verwendet, weil Gleitkommazahlen wie double bei Geld zu Rundungsfehlern führen können.
import java.math.BigDecimal;
// Locale.ROOT sorgt dafür, dass Währungscodes unabhängig von der Computersprache großgeschrieben werden.
import java.util.Locale;
// Objects stellt eine verständliche Prüfung für zwingend benötigte Werte bereit.
import java.util.Objects;

// CurrencyAmount verbindet einen exakten Zahlenbetrag mit seinem Währungscode zu einem unveränderlichen Wertobjekt.
public class CurrencyAmount {
    // BigDecimal speichert den Geldbetrag ohne die für double typischen binären Rundungsfehler.
    private final BigDecimal amount;

    // Der Währungscode verhindert, dass beispielsweise Euro und US-Dollar unbemerkt verrechnet werden.
    private final String currencyCode;

    // Der Konstruktor verlangt Betrag und Währung, weil nur beide Angaben zusammen einen Geldwert ergeben.
    public CurrencyAmount(BigDecimal amount, String currencyCode) {
        // Ein Geldbetrag darf nicht fehlen, weil damit weder gerechnet noch gebucht werden könnte.
        this.amount = Objects.requireNonNull(amount, "Der Betrag darf nicht null sein.");

        // Ein leerer Währungscode würde sichere Additionen und Subtraktionen unmöglich machen.
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new IllegalArgumentException("Der Währungscode darf nicht leer sein.");
        }

        // Negative CurrencyAmount-Objekte werden verhindert; Abbuchungen werden stattdessen über Account.debit ausgeführt.
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Der Betrag darf nicht negativ sein.");
        }

        // Einheitliche Großschreibung sorgt dafür, dass beispielsweise eur und EUR als dieselbe Währung gelten.
        this.currencyCode = currencyCode.toUpperCase(Locale.ROOT);
    }

    // add erzeugt einen neuen Geldwert, damit das bestehende CurrencyAmount-Objekt unverändert bleibt.
    public CurrencyAmount add(CurrencyAmount other) {
        // Vor der Addition wird geprüft, ob beide Beträge dieselbe Währung besitzen.
        ensureSameCurrency(other);

        // Das Ergebnis erhält die gemeinsame Währung und die Summe der beiden exakten BigDecimal-Werte.
        return new CurrencyAmount(amount.add(other.amount), currencyCode);
    }

    // subtract erzeugt einen neuen Geldwert mit der Differenz der beiden Beträge.
    public CurrencyAmount subtract(CurrencyAmount other) {
        // Auch eine Subtraktion ist nur zwischen Geldbeträgen derselben Währung sinnvoll.
        ensureSameCurrency(other);

        // Der Konstruktor verhindert automatisch ein negatives Ergebnis und schützt damit vor ungültigen Geldwerten.
        return new CurrencyAmount(amount.subtract(other.amount), currencyCode);
    }

    // Diese Hilfsmethode bündelt die Währungsprüfung, damit add und subtract dieselbe Regel verwenden.
    private void ensureSameCurrency(CurrencyAmount other) {
        // Ein fehlender zweiter Betrag kann nicht mit diesem Objekt verrechnet werden.
        Objects.requireNonNull(other, "Der andere Betrag darf nicht null sein.");

        // Unterschiedliche Währungen dürfen ohne Wechselkurs nicht direkt miteinander verrechnet werden.
        if (!currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Die Währungen müssen übereinstimmen.");
        }
    }

    // Der Getter wird für Vergleiche und die Ausgabe benötigt, ohne den unveränderlichen Betrag freizugeben.
    public BigDecimal getAmount() {
        return amount;
    }

    // Der Getter stellt den normalisierten Währungscode für Konten, Services und Konsolenausgaben bereit.
    public String getCurrencyCode() {
        return currencyCode;
    }

    // toString erzeugt eine verständliche Darstellung, die direkt in Bestätigungen und Menüs verwendet werden kann.
    @Override
    public String toString() {
        // toPlainString vermeidet eine wissenschaftliche Schreibweise wie 1E+2 bei Geldbeträgen.
        return amount.toPlainString() + " " + currencyCode;
    }
}
