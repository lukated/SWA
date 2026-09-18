// Die Klasse liegt im command-Paket, weil sie das Hinzufügen eines Accounts als rückgängig machbare Aktion kapselt.
package command;

// Account ist das Fachobjekt, das durch dieses Command gespeichert werden soll.
import payment.Account;
// Der PaymentStore ist der Empfänger, der die Accounts im Hauptspeicher verwaltet.
import payment.PaymentStore;

// Dieses Command verpackt das Hinzufügen eines Accounts einschließlich der passenden Undo-Aktion.
public class AddAccountCommand implements Command {
    // Der Store wird gespeichert, damit execute und undo denselben Datenbestand verändern.
    private final PaymentStore store;

    // Der Account wird gespeichert, damit undo genau den zuvor hinzugefügten Account entfernen kann.
    private final Account account;

    // Der Konstruktor erhält alle Informationen, die später für Ausführung und Undo benötigt werden.
    public AddAccountCommand(PaymentStore store, Account account) {
        // Der übergebene PaymentStore wird zum Empfänger dieses Commands.
        this.store = store;

        // Der übergebene Account wird als Gegenstand der Aktion gespeichert.
        this.account = account;
    }

    // Override kennzeichnet die Umsetzung der eigentlichen Command-Aktion.
    @Override
    public void execute() {
        // Der Account wird über die dafür vorgesehene Methode im PaymentStore abgelegt.
        store.addAccount(account);
    }

    // Override kennzeichnet die Umsetzung der Gegenaktion zum Hinzufügen.
    @Override
    public void undo() {
        // Der zuvor hinzugefügte Account wird wieder aus demselben Store entfernt.
        store.removeAccount(account);
    }
}
