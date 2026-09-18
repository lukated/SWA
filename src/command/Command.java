// Das command-Paket enthält Aktionen, die als eigene Objekte ausgeführt und rückgängig gemacht werden können.
package command;

// Das Interface legt den gemeinsamen Vertrag für alle Commands im Projekt fest.
public interface Command {
    // execute führt die eigentliche Aktion aus, zum Beispiel ein Subject zum Store hinzuzufügen.
    void execute();

    // undo führt die passende Gegenaktion aus und macht damit das zuletzt ausgeführte Command rückgängig.
    void undo();
}
