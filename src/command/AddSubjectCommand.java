// Die Klasse liegt im command-Paket, weil sie eine Aktion als eigenständiges Command-Objekt kapselt.
package command;

// Der AuthenticationStore ist der Empfänger der Aktion und verwaltet die gespeicherten Subjects.
import authentication.AuthenticationStore;
// Subject ist das konkrete Fachobjekt, das durch dieses Command hinzugefügt werden soll.
import authentication.Subject;

// Dieses Command verpackt das Hinzufügen eines Subjects einschließlich der passenden Undo-Aktion.
public class AddSubjectCommand implements Command {
    // Der Store wird gespeichert, damit execute und undo auf denselben Datenbestand zugreifen.
    private final AuthenticationStore store;

    // Das Subject wird gespeichert, damit undo genau das zuvor hinzugefügte Objekt entfernen kann.
    private final Subject subject;

    // Der Konstruktor erhält alle Informationen, die das Command für Ausführung und Undo benötigt.
    public AddSubjectCommand(AuthenticationStore store, Subject subject) {
        // Der übergebene AuthenticationStore wird zum Empfänger dieses Commands.
        this.store = store;

        // Das übergebene Subject wird als Gegenstand der Aktion gespeichert.
        this.subject = subject;
    }

    // Override kennzeichnet die Umsetzung der im Command-Interface vorgegebenen Ausführungsmethode.
    @Override
    public void execute() {
        // Die eigentliche Aktion wird an den Store delegiert, der das Subject im Hauptspeicher ablegt.
        store.addSubject(subject);
    }

    // Override kennzeichnet die Umsetzung der zum Command gehörenden Gegenaktion.
    @Override
    public void undo() {
        // Das zuvor hinzugefügte Subject wird wieder aus demselben Store entfernt.
        store.removeSubject(subject);
    }
}
