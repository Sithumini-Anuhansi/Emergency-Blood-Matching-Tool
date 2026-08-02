package com.bloodnetwork.app.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Standard ABO/Rh donor-compatibility rules -- who is allowed to donate to whom.
// e.g. O- is a universal donor; AB+ is a universal recipient.
public final class BloodCompatibility {

    private static final Map<String, Set<String>> CAN_DONATE_TO = new HashMap<>();

    static {
        CAN_DONATE_TO.put("O-",  setOf("O-","O+","A-","A+","B-","B+","AB-","AB+"));
        CAN_DONATE_TO.put("O+",  setOf("O+","A+","B+","AB+"));
        CAN_DONATE_TO.put("A-",  setOf("A-","A+","AB-","AB+"));
        CAN_DONATE_TO.put("A+",  setOf("A+","AB+"));
        CAN_DONATE_TO.put("B-",  setOf("B-","B+","AB-","AB+"));
        CAN_DONATE_TO.put("B+",  setOf("B+","AB+"));
        CAN_DONATE_TO.put("AB-", setOf("AB-","AB+"));
        CAN_DONATE_TO.put("AB+", setOf("AB+"));
    }

    private BloodCompatibility() {}

    private static Set<String> setOf(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    // Can a donor with donorGroup safely give blood to a recipient needing recipientGroup?
    public static boolean canDonate(String donorGroup, String recipientGroup) {
        Set<String> recipients = CAN_DONATE_TO.get(normalize(donorGroup));
        return recipients != null && recipients.contains(normalize(recipientGroup));
    }

    private static String normalize(String group) {
        return group == null ? "" : group.trim().toUpperCase();
    }
}
