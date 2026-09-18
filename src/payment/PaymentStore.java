// Der Store gehört zum payment-Paket, weil er die dort erzeugten Accounts verwaltet.
package payment;

// ArrayList ist die konkrete Datenstruktur, die Accounts während der Programmlaufzeit speichert.
import java.util.ArrayList;
// List wird als allgemeiner Attribut- und Rückgabetyp verwendet, damit die konkrete Listenart austauschbar bleibt.
import java.util.List;

// Der PaymentStore ersetzt eine Datenbank und hält alle Accounts ausschließlich im Hauptspeicher.
public class PaymentStore {
    // Die Liste enthält alle Accounts, die während der aktuellen Programmausführung angelegt wurden.
    private final List<Account> accounts;

    // Beim Erstellen des Stores wird eine eigene leere Account-Liste vorbereitet.
    public PaymentStore() {
        // ArrayList eignet sich für das einfache Hinzufügen, Entfernen und Durchlaufen der Accounts.
        this.accounts = new ArrayList<>();
    }

    // Diese Methode stellt den kontrollierten Zugang zum Speichern eines Accounts bereit.
    public void addAccount(Account account) {
        // null wird nicht gespeichert, weil es keinen verwendbaren Account repräsentiert.
        if (account != null) {
            // Der Account wird bis zum Programmende oder bis zu seiner Löschung im Hauptspeicher gehalten.
            accounts.add(account);
        }
    }

    // Diese Methode entfernt einen Account wieder aus dem Hauptspeicher.
    public void removeAccount(Account account) {
        // List.remove entfernt das übergebene Objekt, wenn es in der Liste enthalten ist.
        accounts.remove(account);
    }

    // Diese Methode stellt die gespeicherten Accounts für Auswahl und Ausgabe zur Verfügung.
    public List<Account> getAccounts() {
        // Eine Kopie verhindert, dass anderer Code die interne Liste am Command-Muster vorbei verändert.
        return new ArrayList<>(accounts);
    }
}
