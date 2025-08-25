package com.vesco.financetracker.util;

public final class CookieUtil {
    private CookieUtil() {
    }

    public static String authSetCookie(String token, long maxAgeSeconds, boolean secure, String sameSite) {
        StringBuilder sb = new StringBuilder();
        sb.append("AUTH_TOKEN=").append(token).append("; HttpOnly; Path=/; Max-Age=").append(maxAgeSeconds);
        if (secure)
            sb.append("; Secure");
        if (sameSite != null)
            sb.append("; SameSite=").append(sameSite);
        return sb.toString();
    }

    public static String clearAuthCookie() {
        return "AUTH_TOKEN=; HttpOnly; Path=/; Max-Age=0";
    }
}
