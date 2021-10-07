import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class ReportGenerator {
    final LocalDate today;

    public ReportGenerator(LocalDate today) {
        this.today = today;
    }

    public ReportGenerator() {
        this(LocalDate.now());
    }

    public LocalDate getNextBirthday(LocalDate dob) {

        var nextBday = dob.withYear(today.getYear());
        if (nextBday.isBefore(today))
            nextBday = nextBday.plusYears(1);

        return nextBday;
    }

    public BirthdayReport calculateDailyReport(String contactsFile, String optOutFile) {
        List<Contact> contacts = loadContacts(contactsFile);

        Set<String> emailsToSkip = getDuplicateEmails(contacts);
        emailsToSkip.addAll(loadEmailsToSkip(optOutFile));

        var contactsGrouped = contactsGroupedByBirthday(contacts, emailsToSkip);
        filterByNoticeDay(contactsGrouped);

        return new BirthdayReport(contactsGrouped, today);
    }

    public Map<LocalDate, List<Contact>> contactsGroupedByBirthday(List<Contact> contacts, Set<String> emailsToSkip) {
        LocalDate dateLimit = Utils.addWeekdays(today, 11); //filter out any birthday too far in future to be considered for speed
        var contactsByBirthday = contacts.stream()
                .filter(contact -> contact.getNextBirthday(today).isBefore(dateLimit))
                .filter(contact -> !emailsToSkip.contains(contact.email))
                .collect(groupingBy(contact -> contact.getNextBirthday(today)));

        return contactsByBirthday;
    }

    //Assume we want to also print people's name with <5 days notice
    public void filterByNoticeDay(Map<LocalDate, List<Contact>> birthdays) {

        int weekdays = 0;
        LocalDate current = today;
        StringBuilder sb = new StringBuilder();

        while (weekdays <= 10) {

            var bdayPpl = birthdays.getOrDefault(current, null);
            if (bdayPpl != null && !(weekdays <= 5 || bdayPpl.size() > 20))
                birthdays.remove(current);

            current = current.plusDays(1);
            if (current.getDayOfWeek() != DayOfWeek.SATURDAY && current.getDayOfWeek() != DayOfWeek.SUNDAY)
                weekdays++;
        }
    }

    public static Set<String> getDuplicateEmails(Collection<Contact> contacts) {
        Set<String> duplicateEmails = new HashSet<>();
        Set<String> emailsSeen = new HashSet<>();

        //skip dup emails
        for (Contact contact : contacts) {
            if (!emailsSeen.add(contact.email)) //skip dup emails
                duplicateEmails.add(contact.email);
        }

        return duplicateEmails;
    }

    public static List<String> loadEmailsToSkip(String optOutTxt) {

        return new FileManager(optOutTxt).getData();
    }

    public static List<Contact> loadContacts(String contactsTxt) {

        List<Contact> contacts = new FileManager(contactsTxt)
                .getData()
                .stream()
                .map(contactStr -> {
                    var contactSplit = contactStr.split(",");
                    Contact contact = new Contact(new PersonName(contactSplit[1], contactSplit[0]),
                            LocalDate.parse(contactSplit[2], Constants.DATE_FORMAT),
                            contactSplit[3]);

                    return contact;
                })
                .collect(Collectors.toList());

        return contacts;
    }
}
