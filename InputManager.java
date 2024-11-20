import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class InputManager {
    // 메인 날짜 유효성 통합 검증
    public boolean checkDateInput(LocalDate date, LocalDate dateNow){
        String formatDate = date.toString().replace("-","");
        if(isNumeric(formatDate) && checkDateVaildation(formatDate) && checkDateIsAfter(date,dateNow)){
            return true;
        }
        return false;
    }
    // 시간 유효성 검증
    public boolean checkTimeInput(LocalTime time,LocalTime timeNow,LocalDate today,
                                  LocalDate dateNow){
        String formatTime = time.toString().replace(":","");
        // 이전 설정 날짜가 없으면 true
        if(dateNow == null){
            if(isNumeric(formatTime) && checkTimeValidation(formatTime)){
                return true;
            }
        }
        if(isNumeric(formatTime) && checkTimeValidation(formatTime) && checkTimeIsAfter(time,timeNow,today,dateNow)){
            return true;
        }
        return false;
    }
    // 날짜의 유효성 검증 년,월,일의 범위 제한
    public boolean checkDateVaildation(String date){
        if(date.length() != 8){
            System.out.println("년,월,일을 정확하게 입력해주세요.");
            return false;
        }

        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        if(year <= 2014 || year >= 2034){
            System.out.println("년도의 범위가 잘못되었습니다. (2014 ~ 2034 사이)");
            return false;
        }else if(month <= 0 || month >= 13){
            System.out.println("월의 범위가 잘못되었습니다. (1~12 사이)");
            return false;
        }else if(day <= 0 || day >= 32){
            System.out.println("일의 범위가 잘못되었습니다. (1~31 사이)");
            return false;
        }

        return true;
    }
    // 초기에 입력한 날짜보다 이전인지 확인하는 메소드
    public boolean  checkDateIsAfter(LocalDate date,LocalDate dateNow){
        if(dateNow == null){
            return true;
        }
        if(date.isBefore(dateNow)){
            System.out.println(dateNow + " 보다 이전의 날짜는 허용되지않습니다.");
            return false;
        }
        return true;
    }
    // 시간의 유효성 검증 LocalTime의 범위 0~23시, 0~59분
    public boolean checkTimeValidation(String time){
        if(time.length() != 4){
            System.out.println("시간,분을 정확하게 입력해주세요.");
            return false;
        }

        int hour = Integer.parseInt(time.substring(0,2));
        int minute = Integer.parseInt(time.substring(2,4));
        if(hour < 0 || hour > 23){
            System.out.println("시간을 올바르게 입력해주세요(0~23).");
            return false;
        }
        if(minute < 0 || minute > 59){
            System.out.println("분을 올바르게 입력해주세요(0~59)");
            return false;
        }

        return true;
    }
    // 시간 이전인가 체크
    public boolean checkTimeIsAfter(LocalTime time,LocalTime timeNow,
                                    LocalDate today,
                                    LocalDate dateNow){
        // 입력으로 받은 날짜가 기준 날짜보다 이후이면,시간 상관 x
        if(today.isAfter(dateNow)){
            return true;
        }
        // 이전 날짜이면 Date 함수에서 걸러지고, 같으면 넘어올떄 비교
        if(timeNow == null){
            return true;
        }
        if(timeNow.isAfter(time)){
            System.out.println(timeNow + "보다 이전의 시간은 허용되지않습니다.");
            return false;
        }
        return true;
    }
    // 제목의 길이가 1~20 사이인지 확인하는 메소드
    // fix 1. 공백 입력 불가하도록 수정
    public boolean checkTitleLengthAndFirstChar(String title){
        if(title.trim().length() < 1 || title.length() > 20){
            System.out.println("제목의 이름은 1~20자 사이입니다.");
            return false;
        }
        if(title.trim().charAt(0) == '!'){
            System.out.println("! 는 제목 가장앞에 사용할 수 없습니다.");
            return false;
        }
        return true;
    }
    // 사용자가 입력한 값이 정수인지 확인하는 메소드
    public boolean isNumeric(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력해주세요.(0~9 사이)");
            return false;
        }
    }
    // 날짜 계산을 위해 YYYY-MM-DD 형식을 LocalDate로 변경해주는 함수
    public LocalDate stringToLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateStr, formatter);
    }
    // 시간 계산을 위해 HHMM 형식을 LocalTime으로 변경해주는 함수
    public LocalTime stringToLocalTime(String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(time,formatter);
    }
    // HHMM의 시간 형식을 HH:MM으로 변경해주는 함수
    public String formatTime(String time){
        String hour = time.substring(0,2);
        String minute = time.substring(2,4);
        return hour + ":" + minute;
    }
    // YYYYMMDD의 날짜 형식을 YYYY-MM-DD로 변경해주는 함수
    public String formatDate(String date) {
        // 년, 월, 일을 추출하여 형식 변경
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);

        return year + "-" + month + "-" + day;
    }
}
