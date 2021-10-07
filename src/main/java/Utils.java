import java.time.DayOfWeek;
import java.time.LocalDate;

public class Utils {

    public static LocalDate getNextBirthday(LocalDate today, LocalDate dob) {

        var nextBday = dob.withYear(today.getYear());
        if(nextBday.isBefore(today))
            nextBday = nextBday.plusYears(1);

        return nextBday;
    }

    public static LocalDate addWeekdays(LocalDate date, int days){
        LocalDate result = date;
        int addedDays = 0;
        while (addedDays < days) {
            result = result.plusDays(1);
            if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                ++addedDays;
            }
        }
        return result;
    }

}
