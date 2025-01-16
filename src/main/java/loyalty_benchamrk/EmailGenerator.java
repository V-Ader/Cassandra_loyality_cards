package loyalty_benchamrk;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class EmailGenerator {

    public static List<String> getEmails(String role, int number){
        List<String> result = new LinkedList<>();
        for (int i = 0; i < number; i++){
            result.add(role + '-' + i + '-' +getTime());
        }
        return result;
    }

    public static String getEmail(String role, int id){
        return role + '-' + id + getTime();
    }

    private static String getTime() {
        return LocalDate.now().toString();
    }
}
