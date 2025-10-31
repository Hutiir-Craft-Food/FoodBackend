package com.khutircraftubackend.validated;

public final class ThreatLevelResolver {

    private ThreatLevelResolver() {}

    public static String resolve(String mimeType) {
        if (mimeType == null) return "НЕВІДОМИЙ";

        if (isExecutable(mimeType)) return "ВИСОКИЙ";
        if (mimeType.startsWith("application/")) return "ПОМІРНИЙ";
        return "НИЗЬКИЙ";
    }

    private static boolean isExecutable(String mimeType) {
        return mimeType.startsWith("application/x-")
                || mimeType.contains("executable")
                || mimeType.contains("binary")
                || mimeType.equals("application/x-msdownload")
                || mimeType.equals("application/x-dosexec");
    }
}