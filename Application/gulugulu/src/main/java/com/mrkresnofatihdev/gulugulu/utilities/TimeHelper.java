package com.mrkresnofatihdev.gulugulu.utilities;

import java.time.Instant;

public class TimeHelper {
    public static String GetUtcMilliTimestamp() {
        return String.format("%015d", Instant.now().toEpochMilli());
    }

    public static String GetDescUtcMilliTimestamp() {
        var futureEpoch = Instant.parse("2050-01-01T00:00:00Z").toEpochMilli();
        var currentEpoch = Instant.now().toEpochMilli();
        return String.format("%015d", futureEpoch - currentEpoch);
    }
}
