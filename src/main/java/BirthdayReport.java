import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

public class BirthdayReport {
    private final Map<LocalDate, List<Contact>> report;
    private final LocalDate today;

    public BirthdayReport(Map<LocalDate, List<Contact>> report, LocalDate today) {
        this.report = report;
        this.today = today;
    }

    public void printReport(){
        report.entrySet().stream().sorted().forEach( entry -> {
            var date = entry.getKey();
            System.out.printf("Birthdays on %s (%d days notice):\n", date, DAYS.between(today, date));
            entry.getValue().stream()
                    .map(contact -> String.format("\tName: %s, Email: %s\n", contact.name, contact.email))
                    .forEach(System.out::printf);

            System.out.println();
        });
    }

    public Map<LocalDate, List<Contact>> getReport(){
        return report;
    }

}
