package com.athaydes.osgi.api;

import com.athaydes.osgi.api.Messages.TestInfo;
import com.athaydes.osgi.api.Messages.TestResult;

/**
 *
 */
public interface MessageService {

    TestResult send(TestInfo info);

}
