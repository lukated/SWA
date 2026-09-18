// Der CommandManager liegt im command-Paket, weil er die Ausführung und das Undo von Commands koordiniert.
package command;

// Der CommandManager führt Commands aus und merkt sich die zuletzt ausgeführte Aktion für ein einmaliges Undo.
public class CommandManager {
    // Der allgemeine Typ Command erlaubt es, Subject- und Account-Aktionen gleich zu behandeln.
    private Command lastCommand;

    // Diese Methode führt ein beliebiges Command aus und speichert es anschließend als letzte Aktion.
    public void executeCommand(Command command) {
        // null wird ignoriert, weil ohne Command weder eine Aktion noch ein sinnvolles Undo möglich ist.
        if (command == null) {
            return;
        }

        // Zuerst wird die im konkreten Command gekapselte Aktion ausgeführt.
        command.execute();

        // Danach wird genau dieses Command gespeichert, damit seine undo-Methode erreichbar bleibt.
        lastCommand = command;
    }

    // Diese Methode macht die zuletzt über den Manager ausgeführte Aktion rückgängig.
    public void undoLastCommand() {
        // Ohne zuvor ausgeführtes Command gibt es keine Aktion, die rückgängig gemacht werden könnte.
        if (lastCommand == null) {
            return;
        }

        // Das gespeicherte Command kennt selbst die passende Gegenaktion und führt sie aus.
        lastCommand.undo();

        // Nach dem Undo wird die Referenz gelöscht, damit dieselbe Aktion nicht mehrfach rückgängig gemacht wird.
        lastCommand = null;
    }
}
