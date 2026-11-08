package readingroom;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryDemoTest {

    @Test
    void demoReportsEveryScenarioOutcome() {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();

        new LibraryDemo(new PrintStream(captured, true, StandardCharsets.UTF_8)).run();
        String output = captured.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Member is not registered: Kwame Asante"));
        assertTrue(output.contains("Item does not belong to this library: The Pragmatic Programmer"));
        assertTrue(output.contains("Lent \"Things Fall Apart\" to Kofi Mensah for 14 days"));
        assertTrue(output.contains("Lent \"French Audio Course\" to Ama Owusu for 14 days"));
        assertTrue(output.contains("Returned \"French Audio Course\""));
        assertTrue(output.contains("Item is already on loan: Things Fall Apart"));
        assertTrue(output.contains("=== The Reading Room - end of day ==="));
    }
}
