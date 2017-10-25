package com.athaydes.osgi.api.messages;

/**
 *
 */
public @interface TestResult {

    TestInfo testInfo();

    boolean success();

}
