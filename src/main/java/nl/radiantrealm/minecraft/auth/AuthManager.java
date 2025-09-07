package nl.radiantrealm.minecraft.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AuthManager {
    private static final Map<Integer, Long> OTPExpiryMap = new HashMap<>();
    private static final Map<Integer, UUID> OTPTokenMap = new HashMap<>();

    private static final Map<UUID, Long> SessionExpiryMap = new HashMap<>();
    private static final Map<UUID, UUID> SessionTokenMap = new HashMap<>();

    private AuthManager() {}

    public static Integer generateOTPToken(UUID playerUUID) {
        if (playerUUID == null) {
            return null;
        }

        int random = 100000 + new Random().nextInt(900000);

        OTPExpiryMap.put(random, System.currentTimeMillis() + 300000);
        OTPTokenMap.put(random, playerUUID);
        return random;
    }

    public static UUID verifyOTPToken(Integer token) {
        if (token == null) {
            return null;
        }

        try {
            UUID playerUUID = OTPTokenMap.get(token);

            if (playerUUID == null) {
                return null;
            }

            Long expiry = OTPExpiryMap.getOrDefault(token, 0L);

            if (expiry < System.currentTimeMillis()) {
                return null;
            }

            UUID random = UUID.randomUUID();

            SessionExpiryMap.put(random, System.currentTimeMillis() + 1200000);
            SessionTokenMap.put(random, playerUUID);
            return random;
        } catch (Exception e) {
            return null;
        }
    }

    public static UUID verifySessionToken(UUID token) {
        if (token == null) {
            return null;
        }

        try {
            UUID playerUUID = SessionTokenMap.get(token);

            if (playerUUID == null) {
                return null;
            }

            Long expiry = SessionExpiryMap.getOrDefault(token, 0L);

            if (expiry < System.currentTimeMillis()) {
                return null;
            }

            return playerUUID;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean destroySessionToken(UUID token) {
        if (token == null) {
            return false;
        }

        try {
            SessionExpiryMap.remove(token);
            SessionTokenMap.remove(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
