package aegis.com.aegis.Utils;

import android.text.TextUtils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
   
	public static boolean isEmpty(String data){
		
		LogUtils.info(data);
		if(TextUtils.isEmpty(data)){
			return true;
		}
		else{
			String inputData = data.trim();
			if(inputData.equalsIgnoreCase("null")){
				return true;
			}		
			else if(inputData.equalsIgnoreCase("\"\"")){
				return true;
			}
		}
		return false;
	}
	
	public static String getValidString(String str)
	{
		if(isEmpty(str))
			return " ";
		else
			return str;
	}
	public static String getValidlatlong(String str)
	{
		if(isEmpty(str))
			return "0.0";
		else
			return str;
	}
	
	/**
	 * This method will Check Email Validation.</br>
	 * 
	 * @param email
	 *            - This is input email into <b>String</b> format.
	 * @return <b>true</b> if Email is valid.
	 */
//	public static boolean isEmailValid(String email) {
////		Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
//		Pattern emailPattern = Pattern.compile("[a-z0-9._-]+@[a-z]+\\.+[a-z]+");
//		
//		Boolean validation = emailPattern.matcher(email).matches();
//		if (email.contains(".@")) {
//			validation=false;
//		}
//		return validation;
//	}
	
	public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    } 
	public static String FirstLetterInUpperCase(String word){
		
		String result = "";
		if(word.length()>0){			
		  char c = word.charAt(0);
		  String splitedString = word.substring(1, word.length());
		  result = Character.toUpperCase(c)+splitedString;
		}
		return result;
	}
	
	
	public static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
	
	public static int getIntValue(String value){
		
		if(value == null){
			return 0;
		}
		
		try {
			int iValue = Integer.parseInt(value);
			return iValue;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static Double getDoubleValue(String value){
		
		if(value == null){
			return 0.0;
		}
		
		try {
			Double iValue = Double.valueOf(value);
			return iValue;
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}
	
	public static String getIntValueFromFloat(String value){
		
		try {
			int iValue = Float.valueOf(value).intValue();
			return String.valueOf(iValue);
		} catch (NumberFormatException e) {
			return "0";
		}
	}
	
	public static String getRandomImageName() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(UUID.randomUUID().toString());
		sb.append("_a_");
		sb.append(System.currentTimeMillis());
		sb.append(".png");
		return sb.toString();
	}
	
	public static String getStringValue(boolean value){
		String sValue = (value) ? "1" : "0";
		return sValue;
	}
	
	public static boolean isValidString(String str) {
		boolean isValid = false;
		if (str != null && str.length() > 0) {
			isValid = true;
		} else {
			isValid = false;
		}
		return isValid;

	}
	
	public static String changeDateFormat(String updateDate) {
		String[] split = updateDate.split(" ");
		String date = split [0];
		String formateDate = date.replace("-", "/");
		return formateDate;		
	}

}
