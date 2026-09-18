// Die Klasse gehört zum Authentication-Paket, weil Credentials nur für die Authentifizierung benötigt werden.
package authentication;

// Ein Credential fasst eine Authentifizierungsart und den dazugehörigen Wert zu einem Objekt zusammen.
public class Credential {
    // Der Typ legt fest, wie der Wert interpretiert wird, zum Beispiel als Passwort oder Fingerabdruck.
    // "private" schützt das Attribut vor direktem Zugriff von außen; "final" verhindert spätere Änderungen.
    private final CredentialType credentialType;

    // Object wird verwendet, weil unterschiedliche Credential-Arten unterschiedliche Werttypen haben können.
    // Auch dieser Wert bleibt nach dem Erstellen unverändert, damit ein Credential einen stabilen Zustand besitzt.
    private final Object credentialValue;

    // Der Konstruktor verlangt beide Bestandteile, damit kein unvollständiges Credential erzeugt werden kann.
    public Credential(CredentialType credentialType, Object credentialValue) {
        // "this.credentialType" bezeichnet das Attribut des neuen Objekts; rechts steht der übergebene Parameter.
        this.credentialType = credentialType;

        // Der übergebene Authentifizierungswert wird im neuen Credential gespeichert.
        this.credentialValue = credentialValue;
    }

    // Der Getter erlaubt lesenden Zugriff, ohne das private Attribut von außen veränderbar zu machen.
    public CredentialType getCredentialType() {
        // Zurückgegeben wird die Authentifizierungsart dieses konkreten Credentials.
        return credentialType;
    }

    // Auch der eigentliche Wert wird kontrolliert über einen Getter nach außen gegeben.
    public Object getCredentialValue() {
        // Der gespeicherte Wert wird beispielsweise von einer AuthenticationStrategy für den Vergleich benötigt.
        return credentialValue;
    }
}
