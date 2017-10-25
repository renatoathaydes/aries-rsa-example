package com.athaydes.osgi.client;

import com.athaydes.osgi.api.Listener;
import com.athaydes.osgi.api.MessageService;
import com.athaydes.osgi.api.Messages.TestInfo;
import com.athaydes.osgi.api.Messages.TestResult;
import com.athaydes.osgiaas.cli.CommandHelper;
import org.apache.felix.shell.Command;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
@Component( immediate = true )
public class MessageSender implements Command {

    @Reference
    volatile MessageService messageService;

    private final MessageReceiver receiver = new MessageReceiver();

    @Activate
    public void init() {
        messageService.addListener( receiver );
    }

    @Override
    public String getName() {
        return "send-msg";
    }

    @Override
    public String getUsage() {
        return "send-msg <message>";
    }

    @Override
    public String getShortDescription() {
        return "Command to test the MessageSender in the osgi-client bundle";
    }

    @Override
    public void execute( String line, PrintStream out, PrintStream err ) {
        List<String> commands = CommandHelper.breakupArguments( line, 2 );
        if ( commands.size() != 2 ) {
            CommandHelper.printError( err, getUsage(), "Too few arguments" );
        } else {
            String message = commands.get( 1 );
            out.println( "Sending TestInfo message: " + message );

            Future<TestResult> promise = receiver.getPromise();

            TestInfo info = TestInfo.newBuilder()
                    .setTestClass( String.class.toString() )
                    .setTestMethod( message )
                    .build();

            messageService.accept( info );

            out.println( "Waiting for response..." );

            try {
                TestResult response = promise.get( 5, TimeUnit.SECONDS );
                out.println( "Got response back: " + response );
            } catch ( Exception e ) {
                e.printStackTrace( err );
            }
        }
    }

    private static class MessageReceiver implements Listener<TestResult> {

        private final AtomicReference<Deferred> resultRef = new AtomicReference<>();

        @Override
        public Class<TestResult> messageType() {
            return TestResult.class;
        }

        Future<TestResult> getPromise() {
            Deferred futureTask = new Deferred();

            boolean wasNull = resultRef.compareAndSet( null, futureTask );

            if ( wasNull ) {
                return futureTask;
            } else {
                throw new RuntimeException( "Called getPromise() while waiting for future to complete" );
            }
        }

        @Override
        public void react( TestResult message ) {
            Deferred deferred = resultRef.getAndSet( null );
            if ( deferred != null ) {
                deferred.set( message );
            } else {
                throw new RuntimeException( "Received message but no promise was waiting for it" );
            }
        }
    }

    private static class Deferred extends FutureTask<TestResult> {

        Deferred() {
            super( () -> null );
        }

        @Override
        public void set( TestResult testResult ) {
            super.set( testResult );
        }
    }

}
