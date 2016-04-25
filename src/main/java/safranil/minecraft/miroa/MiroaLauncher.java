package safranil.minecraft.miroa;

import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.AuthProfile;
import fr.theshark34.openauth.model.response.AuthResponse;

public class MiroaLauncher {
    static MiroaLauncher self;
    private static final String CLIENT_TOKEN = "MiroaLauncher";

    private String accessToken;

    /**
     * Make class as Singleton
     */
    private MiroaLauncher() {}

    /**
     * Get or create the unique Miroa launcher instance
     * @return singleton instance
     */
    public static MiroaLauncher getInstance() {
        if (self == null) {
            self = new MiroaLauncher();
        }
        return self;
    }

    public boolean auth(String username, String password) throws AuthenticationException {
        Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);

        AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, CLIENT_TOKEN);

        accessToken = response.getAccessToken();

        return true;
    }
}
