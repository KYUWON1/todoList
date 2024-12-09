import java.time.LocalDate;

public class DateResult {
    private String rawDate;
    private LocalDate formatDate;

    public DateResult(String rawDate, LocalDate formatDate) {
        this.rawDate = rawDate;
        this.formatDate = formatDate;
    }

    public String getRawDate() {
        return rawDate;
    }

    public void setRawDate(String rawDate) {
        this.rawDate = rawDate;
    }

    public LocalDate getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(LocalDate formatDate) {
        this.formatDate = formatDate;
    }
}
