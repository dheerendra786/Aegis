package aegis.com.aegis.Utils;

public class AegisConfig {
    public interface WebConstants {
        String NETWORK_MESSAGE = "Could not establish connection. Please check network settings or contact your mobile operator.";
        String INVALID_RESPONSE_MESSAGE = "Invalid resposne from server. Please try again later.";
        int CONNECTIONTIMEOUT = 60 * 1000;
        int READTIMEOUT = 60 * 1000;

        String HOST_API = "http://116.73.50.95:81/rec/"; // Live IP

        String HOST_API_IMAGE = "https://experchatqa.s3.amazonaws.com/";
        String BASE64URl = "data:image/jpeg;base64,";
        String PHONE_REGISTERED_NOT_VERIFIED = "1082";
        String country_code = ""; //+91

        //    Api request connstants...


    }

}
