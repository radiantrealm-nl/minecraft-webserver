package nl.radiantrealm.minecraft.auth;

import com.sun.net.httpserver.HttpExchange;
import nl.radiantrealm.library.utils.FormatUtils;

import java.net.HttpCookie;
import java.util.List;
import java.util.UUID;

public class CookieUtils {
    private static final String DOMAIN = "radiantrealm.nl";

    private CookieUtils() {}

    public static void setCookie(HttpExchange exchange, String key, String value, int age) {
        String cookie = String.format(
                "%s=%s; Path=/; Domain=%s; Max-Age=%d; HttpOnly; Secure; SameSite=None",
                key,
                value,
                DOMAIN,
                age
        );

        exchange.getResponseHeaders().add("Set-Cookie", cookie);
    }

    public static String getCookie(HttpExchange exchange, String name) {
        if (exchange == null || name == null) {
            return null;
        }

        List<String> cookies = exchange.getRequestHeaders().get("Cookie");

        if (cookies == null) {
            return null;
        }

        for (String cookieHeader : cookies) {
            List<HttpCookie> parsed = HttpCookie.parse(cookieHeader);

            for (HttpCookie cookie : parsed) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public static UUID getSessionTOken(HttpExchange exchange) {
        String cookie = CookieUtils.getCookie(exchange, "csrf");

        if (cookie == null) {
            return null;
        }

        try {
            UUID token = FormatUtils.formatUUID(cookie);
            return AuthManager.verifySessionToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}
