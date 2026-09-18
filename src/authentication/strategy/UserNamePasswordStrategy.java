// Die Klasse liegt im strategy-Paket, weil sie eine konkrete austauschbare Authentifizierungsart darstellt.
package authentication.strategy;

// Credential enthält den Typ und den Wert, die von dieser Strategie geprüft werden.
import authentication.Credential;
// CredentialType wird benötigt, damit diese Strategie ausschließlich Passwort-Credentials akzeptiert.
import authentication.CredentialType;
// Subject enthält die bereits gespeicherten Credentials, mit denen die Eingabe verglichen wird.
import authentication.Subject;

// Diese konkrete Strategie führt die Authentifizierung mit einem Passwort durch.
// "implements" verpflichtet die Klasse, die Methode aus AuthenticationStrategy bereitzustellen.
public class UserNamePasswordStrategy implements AuthenticationStrategy {

    // Override zeigt, dass diese Methode den gemeinsamen Vertrag des Strategy-Interfaces erfüllt.
    @Override
    public boolean authenticate(Subject subject, Credential credential) {
        // Ohne Subject oder Credential kann keine sinnvolle Authentifizierung durchgeführt werden.
        if (subject == null || credential == null) {
            return false;
        }

        // Diese Strategie ist nur für Passwörter zuständig und lehnt andere Credential-Arten direkt ab.
        if (credential.getCredentialType() != CredentialType.PASSWORD) {
            return false;
        }

        // Das Subject prüft, ob ein gespeichertes Passwort mit dem eingegebenen Credential übereinstimmt.
        return subject.hasMatchingCredential(credential);
    }
}
