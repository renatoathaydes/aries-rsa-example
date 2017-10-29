package com.athaydes.osgi.client;

import com.athaydes.osgi.api.MessageService;
import com.athaydes.osgi.api.Messages.TestInfo;
import com.athaydes.osgi.api.Messages.TestResult;
import com.athaydes.osgiaas.cli.CommandHelper;
import org.apache.felix.shell.Command;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.PrintStream;
import java.util.List;

/**
 *
 */
@Component(immediate = true)
public class MessageSender implements Command {

    @Reference
    volatile MessageService messageService;

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
    public void execute(String line, PrintStream out, PrintStream err) {
        List<String> commands = CommandHelper.breakupArguments(line, 2);
        if (commands.size() != 2) {
            CommandHelper.printError(err, getUsage(), "Too few arguments");
        } else {
            String message = commands.get(1);
            out.println("Sending TestInfo message: " + message);

            TestInfo info = TestInfo.newBuilder()
                    .setTestClass(String.class.toString())
                    .setTestMethod(message)
                    .build();

            try {
                TestResult result = messageService.send(info);
                out.println("Got response back: " + result);
            } catch (Exception e) {
                e.printStackTrace(err);
            }
        }
    }

}
