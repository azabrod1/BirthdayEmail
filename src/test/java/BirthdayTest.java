import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class BirthdayTest {
    private final Path contactsPath = Paths.get("contacts.txt");
    private final Path optOutPath   = Paths.get("optOut.txt");

    public static List<Map<String, List<String>>> testCases() {
        var tests = new ArrayList<Map<String, List<String>>>();

        //Test basic functionality - only bobby in range
        tests.add( Map.of(
                "Contacts", List.of(
                        "Brown,Bobby,10-10-1950,bobby.brown@ilovellamaland.com",
                        "O'Rourke,Betsy,28-02-1900,betsy@heyitsme.com",
                        "Von Tappo,Alfredo,01-01-1920,alfie@vontappo.llama.land"),

                "OptOut", List.of("betsy@heyitsme.com"),
                "Expected", List.of("bobby.brown@ilovellamaland.com")
                )
        );

        //Same as above, but wont pass as Bobby is on opt out list
        tests.add( Map.of(
                "Contacts", List.of(
                        "Brown,Bobby,10-10-1950,bobby.brown@ilovellamaland.com",
                        "O'Rourke,Betsy,28-02-1900,betsy@heyitsme.com",
                        "Von Tappo,Alfredo,01-01-1920,alfie@vontappo.llama.land"),

                "OptOut", List.of("bobby.brown@ilovellamaland.com"),
                "Expected", Collections.EMPTY_LIST
                )
        );

        //There is a duplicate email so we should have blank result
        tests.add( Map.of(
                "Contacts", List.of(
                        "Brown,Bobby,10-10-1950,bobby.brown@ilovellamaland.com",
                        "Brown,Bobby,10-10-1950,bobby.brown@ilovellamaland.com",
                        "O'Rourke,Betsy,28-02-1900,betsy@heyitsme.com",
                        "Von Tappo,Alfredo,01-01-1920,alfie@vontappo.llama.land"),

                "OptOut", List.of("betsy@heyitsme.com"),
                "Expected", Collections.EMPTY_LIST
                )
        );

        //Bobby is more than 5 business days away - should not display
        tests.add( Map.of(
                "Contacts", List.of(
                        "Brown,Bobby,13-10-1950,bobby.brown@ilovellamaland.com",
                        "O'Rourke,Betsy,28-02-1900,betsy@heyitsme.com",
                        "Von Tappo,Alfredo,01-01-1920,alfie@vontappo.llama.land"),

                "OptOut", List.of("betsy@heyitsme.com"),
                "Expected", Collections.EMPTY_LIST
                )
        );

        //Bobby and Anya is less than 5 business days away - since we dont count weekends
        tests.add( Map.of(
                "Contacts", List.of(
                        "Glory,Nala,10-10-1950,purrs@ilovecatland.com",
                        "Gold,Anya,10-10-1950,cats@ilovecatland.com",
                        "Brown,Bobby,11-10-1950,bobby.brown@ilovellamaland.com",
                        "O'Rourke,Betsy,28-02-1900,betsy@heyitsme.com",
                        "Von Tappo,Alfredo,01-01-1920,alfie@vontappo.llama.land"),

                "OptOut", List.of("purrs@ilovecatland.com"),
                "Expected", List.of("bobby.brown@ilovellamaland.com", "cats@ilovecatland.com")
                )
        );

        //In this test case, the date is between 5 and 10 business days away - does not display as its less than 20 ppl
        List<String> contacts = new ArrayList<>();
        for(int p = 0; p < 10; ++p){
               String fname = randomString(5);
               String lname = randomString(8);
               String email = String.format("%s%d@purrs.cat", lname, p);
               contacts.add(String.format("%s,%s,14-10-1950,%s", lname, fname, email));
        }

        tests.add( Map.of(
                "Contacts", contacts,
                "OptOut", Collections.EMPTY_LIST,
                "Expected", Collections.EMPTY_LIST
                )
        );

        //same as above but with 20 people so it should show them
        contacts = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        for(int p = 0; p < 20; ++p){
            String fname = randomString(5);
            String lname = randomString(8);
            String email = String.format("%s%d@purrs.cat", lname, p);
            contacts.add(String.format("%s,%s,14-10-1950,%s", lname, fname, email));
        }

        tests.add( Map.of(
                "Contacts", contacts,
                "OptOut", Collections.EMPTY_LIST,
                "Expected", emails
                )
        );

        return tests;
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(contactsPath);
        Files.deleteIfExists(optOutPath);
    }

    void writeFiles(List<String> contactsFile, List<String> optFile) throws IOException {
        Files.write(contactsPath, contactsFile, StandardCharsets.UTF_8);
        Files.write(optOutPath, optFile, StandardCharsets.UTF_8);
    }

    @ParameterizedTest
    @MethodSource(value = "testCases")
    void testReport(Map<String, List<String>> input) throws IOException {
        writeFiles(input.get("Contacts"), input.get("OptOut"));
        ReportGenerator gen = new ReportGenerator(LocalDate.of(2021,10, 5));
        var expectedEmails = new HashSet<>(input.get("Expected"));

        var report = gen.calculateDailyReport(contactsPath.toString(), optOutPath.toString()).getReport();
        var emailsOnReport = report
                .values()
                .stream()
                .flatMap(List::stream)
                .map(contact -> contact.email)
                .collect(Collectors.toSet());

        assertEquals(expectedEmails, emailsOnReport);
    }

    public static String randomString(int size) {
        int leftLimit = 97; //a
        int rightLimit = 122; //z

        String generatedString = ThreadLocalRandom.current().ints(leftLimit, rightLimit + 1)
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

}
