package com.bloodnetwork.app;

// Tiny, dependency-free assertion helper for exercising the pure-Java business logic
// (model/graph/service/controller) outside of Android/Gradle. Run with plain javac+java.
public final class Assert {
    private static int passed = 0;
    private static int failed = 0;

    private Assert() {}

    public static void assertTrue(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + description);
        } else {
            failed++;
            System.out.println("  [FAIL] " + description);
        }
    }

    public static void assertEquals(String description, Object expected, Object actual) {
        boolean ok = (expected == null) ? (actual == null) : expected.equals(actual);
        assertTrue(description + "  [expected: " + expected + ", actual: " + actual + "]", ok);
    }

    public static void printSummary() {
        System.out.println("\n===== TEST SUMMARY =====");
        System.out.println("Passed: " + passed + "   Failed: " + failed);
        System.out.println(failed > 0 ? "SOME TESTS FAILED" : "ALL TESTS PASSED");
        if (failed > 0) System.exit(1);
    }
}
