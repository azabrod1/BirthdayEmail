import java.time.LocalDate;

public class Birthday {

    public static void main(String[] args){
        var report = new ReportGenerator().calculateDailyReport(args[0], args[1]);
        report.printReport();
    }



}
