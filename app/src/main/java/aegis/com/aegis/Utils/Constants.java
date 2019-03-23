package aegis.com.aegis.Utils;

public class Constants {
    public static final String PHONE_NUMBER = "phone_number";
    public static final String ADVERTISING_ID = "advertising_id";

    public interface ACTION {
        public static String MAIN_ACTION = "com.foregroundservice.action.main";
        public static String PREV_ACTION = "com.foregroundservice.action.prev";
        public static String PLAY_ACTION = "com.foregroundservice.action.play";
        public static String NEXT_ACTION = "com.foregroundservice.action.next";
        public static String STARTFOREGROUND_ACTION = "com.foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.foregroundservice.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
