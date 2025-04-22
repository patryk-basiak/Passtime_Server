/**
 *
 *  @author Basiak Patryk S30757
 *
 */

package zad1;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {

    static String[] dniTygodnia = {"poniedziałek", "wtorek", "środa", "czwartek", "piątek", "sobota", "niedziela"};

    public static String passed(String s, String s1) {
        DateTimeFormatter f;
        boolean isTimeFormat = s.contains("T") && s1.contains("T");

        if (isTimeFormat) {
            f = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm").withResolverStyle(ResolverStyle.STRICT);
        } else {
            f = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM uuuu").withLocale(new Locale("pl")).withResolverStyle(ResolverStyle.STRICT);
        LocalDate ld;
        LocalDate ld1;

        try {
            ld = LocalDate.parse(s, f);
            ld1 = LocalDate.parse(s1, f);
        } catch (Exception ex) {
            return "*** " + ex;
        }

        long days = ChronoUnit.DAYS.between(ld, ld1);
        LocalDateTime localDateTime = null;
        LocalDateTime localDateTime1 = null;
        long hours = 0;
        long minutes = 0;

        if (isTimeFormat) {
            localDateTime = LocalDateTime.parse(s, f);
            localDateTime1 = LocalDateTime.parse(s1, f);
            ZonedDateTime zdt = localDateTime.atZone(ZoneId.of("Europe/Warsaw"));
            ZonedDateTime zdt1 = localDateTime1.atZone(ZoneId.of("Europe/Warsaw"));
            hours = ChronoUnit.HOURS.between(zdt, zdt1);
            minutes = ChronoUnit.MINUTES.between(zdt, zdt1);
        }

        double weeks = (double) days / 7;
        Period difference = Period.between(ld, ld1);

        StringBuilder result = new StringBuilder();

        if (isTimeFormat) {
            result.append("Od ")
                    .append(dateTimeFormatter.format(localDateTime.toLocalDate()))
                    .append(" (")
                    .append(dniTygodnia[localDateTime.getDayOfWeek().getValue() - 1])
                    .append(") godz. ")
                    .append(localDateTime.toLocalTime())
                    .append(" do ")
                    .append(dateTimeFormatter.format(localDateTime1.toLocalDate()))
                    .append(" (")
                    .append(dniTygodnia[localDateTime1.getDayOfWeek().getValue() - 1])
                    .append(") godz. ")
                    .append(localDateTime1.toLocalTime())
                    .append("\n");
        } else {
            result.append("Od ")
                    .append(dateTimeFormatter.format(ld))
                    .append(" (")
                    .append(dniTygodnia[ld.getDayOfWeek().getValue() - 1])
                    .append(") do ")
                    .append(dateTimeFormatter.format(ld1))
                    .append(" (")
                    .append(dniTygodnia[ld1.getDayOfWeek().getValue() - 1])
                    .append(")\n")
                    .append("- mija: ")
                    .append(days)
                    .append(" dni, tygodni ")
                    .append(String.format("%.2f", weeks))
                    .append("\n");
        }

        if (hours > 0 || minutes > 0) {
            result.append("- godzin: ")
                    .append(hours)
                    .append(", minut: ")
                    .append(minutes)
                    .append("\n");
        }

        StringBuilder wynik = new StringBuilder();
        if (difference.getYears() > 0 || difference.getMonths() > 0 || difference.getDays() > 0) {
            wynik.append("- kalendarzowo:");
        }
        if (difference.getYears() > 0) {
            int lata = difference.getYears();
            wynik.append(" ")
                    .append(lata)
                    .append(" ")
                    .append(lata == 1 ? "rok" : lata <= 4 ? "lata" : "lat");
        }

        if (difference.getMonths() > 0) {
            int miesiace = difference.getMonths();
            wynik.append(wynik.length() > 1 ? ", " : " ")
                    .append(miesiace)
                    .append(" ")
                    .append(miesiace == 1 ? "miesiąc" : miesiace <= 4 ? "miesiące" : "miesięcy");
        }

        if (difference.getDays() > 0) {
            int dni = difference.getDays();
            wynik.append(wynik.length() > 15 ? ", " : " ")
                    .append(dni)
                    .append(" ")
                    .append(dni == 1 ? "dzień" : "dni");
        }

        result.append(wynik);

        return result.toString();
    }
}
