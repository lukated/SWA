// Die Klasse liegt im command-Paket, weil sie das Löschen eines Accounts als rückgängig machbare Aktion kapselt.
package command;

// Account ist das konkrete Fachobjekt, das entfernt und bei einem Undo wiederhergestellt wird.
import payment.Account;
// Der PaymentStore ist der Empfänger, dessen Account-Liste durch das Command verändert wird.
import payment.PaymentStore;

// Dieses Command verpackt das Löschen eines Accounts einschließlich der passenden Undo-Aktion.
public class DeleteAccountCommand implements Command {
    // Der Store wird für die ursprüngliche Löschung und für das spätere Wiederherstellen benötigt.
    private final PaymentStore store;

    // Der Account bleibt im Command referenziert, damit undo genau dieses Objekt wiederherstellen kann.
    private final Account account;

    // Der Konstruktor erhält den Empfänger der Aktion und den zu löschenden Account.
    public DeleteAccountCommand(PaymentStore store, Account account) {
        // Der übergebene PaymentStore wird für beide Richtungen der Aktion gespeichert.
        this.store = store;

        // Der Account bleibt auch nach seiner Entfernung für ein mögliches Undo verfügbar.
        this.account = account;
    }

    // Override kennzeichnet die Umsetzung der eigentlichen Command-Aktion.
    @Override
    public void execute() {
        // Die Löschung wird an den PaymentStore delegiert, der den Account aus seiner Liste entfernt.
        store.removeAccount(account);
    }

    // Override kennzeichnet die Umsetzung der Gegenaktion zur Löschung.
    @Override
    public void undo() {
        // Der zuvor entfernte Account wird wieder in denselben PaymentStore eingefügt.
        store.addAccount(account);
    }
}
