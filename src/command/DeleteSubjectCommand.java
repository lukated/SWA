// Die Klasse liegt im command-Paket, weil sie eine löschbare und rückgängig machbare Aktion kapselt.
package command;

// Der AuthenticationStore ist der Empfänger, aus dem das Subject entfernt beziehungsweise wieder eingefügt wird.
import authentication.AuthenticationStore;
// Subject ist das konkrete Fachobjekt, auf das sich dieses Command bezieht.
import authentication.Subject;

// Dieses Command verpackt das Löschen eines Subjects einschließlich der passenden Undo-Aktion.
public class DeleteSubjectCommand implements Command {
    // Der Store wird für die ursprüngliche Löschung und für das spätere Wiederherstellen benötigt.
    private final AuthenticationStore store;

    // Das Subject bleibt im Command referenziert, damit undo genau dieses Objekt wiederherstellen kann.
    private final Subject subject;

    // Der Konstruktor erhält den Empfänger der Aktion und das zu löschende Subject.
    public DeleteSubjectCommand(AuthenticationStore store, Subject subject) {
        // Der übergebene Store wird für beide Richtungen der Aktion gespeichert.
        this.store = store;

        // Das übergebene Subject wird auch nach seiner Entfernung für ein mögliches Undo behalten.
        this.subject = subject;
    }

    // Override kennzeichnet die Umsetzung der eigentlichen Command-Aktion.
    @Override
    public void execute() {
        // Die Löschung wird an den Store delegiert, der das Subject aus seiner Liste entfernt.
        store.removeSubject(subject);
    }

    // Override kennzeichnet die Umsetzung der Gegenaktion zur Löschung.
    @Override
    public void undo() {
        // Das zuvor entfernte Subject wird wieder in denselben Store eingefügt.
        store.addSubject(subject);
    }
}
