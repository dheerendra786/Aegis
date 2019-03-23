package aegis.com.aegis.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public final static String DATE_FORMAT_1 = "MMM dd, yyyy";
    public final static String DATE_FORMAT_2 = "MM/dd";
    public final static String DATE_FORMAT_3 = "MMM dd, yyyy hh:mm aa";
    public final static String DATE_FORMAT_4 = "MMMM dd, yyyy hh:mm aa";
    public final static String DATE_FORMAT_5 = "yyyy-MM-dd hh:mm:ss";
    public final static String DATE_FORMAT_6 = "yyyy-M-d";
    public final static String DATE_FORMAT_7 = "dd-MM-yyyy HH:mm:ss";
    public final static String DATE_FORMAT_10 = "dd-MM-yyyy HH:mm";
    public final static String DATE_FORMAT_8 = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_FORMAT_9 = "yyyy-MM-dd HH:mm";
    public final static String PATTERN_FORMAT_WEEK = "EE";
    public final static String PATTERN_FORMAT_MONTH = "MMM";
    public final static String FORMAT1 = "hh:mm aa";
    public final static String FORMAT2 = "EEE MMM dd, yyyy hh:mm aa";
    public final static String FORMAT3 = "HH:mm:ss";

    public final static long MILLISECONDS_ONE_HOUR = 1000 * 60 * 60;
    public final static long MILLISECONDS_ONE_DAY = 1000 * 60 * 60 * 24;

    public static String getDateTime(long milliseconds, String outPutFormat) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);

        SimpleDateFormat sdf = new SimpleDateFormat(outPutFormat, Locale.getDefault());
        String outPutDate = sdf.format(cal.getTime());

        return outPutDate;
    }

    public static String getDateTime(Calendar cal, String outPutFormat) {

        SimpleDateFormat sdf = new SimpleDateFormat(outPutFormat, Locale.getDefault());
        String outPutDate = sdf.format(cal.getTime());

        return outPutDate;
    }


    public static long getTimeInMillis(String dateTime, String inputFormat) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(inputFormat, Locale.getDefault());
            Date date = sdf.parse(dateTime);
            return date.getTime();
        } catch (ParseException e) {
            LogUtils.error(e.getMessage());
            return 0;
        }
    }


    public static String getBestDay(long inputTimeStamp, String outPutFormat) {

        Calendar calInput = Calendar.getInstance();
        calInput.setTime(new Date(inputTimeStamp));

        Calendar calDevice = Calendar.getInstance();
        calDevice.setTimeInMillis(System.currentTimeMillis());

        int monthInput = calInput.get(Calendar.MONTH);
        int monthDevice = calDevice.get(Calendar.MONTH);

        int dayInput = calInput.get(Calendar.DAY_OF_MONTH);
        int dayDevice = calDevice.get(Calendar.DAY_OF_MONTH);

        String outPutDay = "";

        if (monthInput == monthDevice && dayInput == dayDevice) {
            outPutDay = "Today";
            return outPutDay;
        } else if (monthInput == monthDevice && dayInput + 1 == dayDevice) {
            outPutDay = "Yesterday";
            return outPutDay;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(outPutFormat, Locale.getDefault());
            outPutDay = sdf.format(calInput.getTime());
            return outPutDay;
        }
    }

    public static int getDaysDifference(long startTimeStamp, long endTimeStamp) {

        Date startDate = new Date(startTimeStamp);
        Date endDate = new Date(endTimeStamp);

        LogUtils.debug("startDate = ", startDate);
        LogUtils.debug("endDate = ", endDate);

        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTimeInMillis(startTimeStamp);
        endCal.setTimeInMillis(endTimeStamp);

        int diff = endCal.get(Calendar.DAY_OF_YEAR) - startCal.get(Calendar.DAY_OF_YEAR);
        diff = diff + (startCal.getMaximum(Calendar.DAY_OF_YEAR) * (endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)));

        return diff;
    }

    public static String getTimeinAMPM(String time) {
        try {
            String str = "";
            String timeArr[] = time.split(":");
            if (Integer.parseInt(timeArr[0]) > 11) {
                if (Integer.parseInt(timeArr[0]) > 12) {
                    int h = Integer.parseInt(timeArr[0]) - 12;
                    if (timeArr[1].contains("PM")) {
                        timeArr[1] = timeArr[1].replace("PM", "");
                    } else {
                        timeArr[1] = timeArr[1].replace("AM", "");
                    }
                    str = StringUtils.pad(h) + ":" + StringUtils.pad(Integer.parseInt(timeArr[1])) + " PM";
                }

                if (Integer.parseInt(timeArr[1]) < 10) {
                    int h = Integer.parseInt(timeArr[0]);
                    if (timeArr[1].contains("PM")) {
                        timeArr[1] = timeArr[1].replace("PM", "");
                    } else {
                        timeArr[1] = timeArr[1].replace("AM", "");
                    }
                    str = StringUtils.pad(h) + ":" + StringUtils.pad(Integer.parseInt(timeArr[1])) + " PM";
                } else
                    str = timeArr[0] + ":" + timeArr[1] + " PM";
            } else {
                if (timeArr[1].contains("PM")) {
                    timeArr[1] = timeArr[1].replace("PM", "");
                } else {
                    timeArr[1] = timeArr[1].replace("AM", "");
                }
                str = StringUtils.pad(Integer.parseInt(timeArr[0])) + ":" + StringUtils.pad(Integer.parseInt(timeArr[1])) + " AM";

            }

            return str;
        } catch (Exception e) {
            return time;
        }


    }

    public static String getFormattedDate(String date) {
        String dmyarr[] = date.split("-");
        // Assuming that you already have this.
        int year = Integer.parseInt(dmyarr[0]);
        int month = Integer.parseInt(dmyarr[1]);
        int day = Integer.parseInt(dmyarr[2]);
        try {
            // First convert to Date. This is one of the many ways.
            String dateString = String.format("%d-%d-%d", year, month, day);
            Date parsedDate = new SimpleDateFormat(DATE_FORMAT_6).parse(dateString);

            // Then get the day of week from the Date based on specific locale.
            String dayOfWeek = new SimpleDateFormat(PATTERN_FORMAT_WEEK, Locale.ENGLISH).format(parsedDate);
            // Then get the month of year from the Date based on specific locale.
            String monthOfYear = new SimpleDateFormat(PATTERN_FORMAT_MONTH, Locale.ENGLISH).format(parsedDate);
            // return date format like : Thu Dec 04, 2014
            return dayOfWeek + " " + monthOfYear + " " + StringUtils.pad(day) + ", " + year;
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }

    }

    public static String getESTTime() {
        Calendar currentdate = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT_7);
        TimeZone obj = TimeZone.getTimeZone("EST");
        formatter.setTimeZone(obj);
        //11-12-2014 01:46:40
        return "" + formatter.format(currentdate.getTime()).split(" ")[1];
    }

    public static String getUTCDateTime() {
        Calendar currentdate = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT_8);
        TimeZone obj = TimeZone.getTimeZone("UTC");
        formatter.setTimeZone(obj);
        //2014-12-05 01:46:40
        return "" + formatter.format(currentdate.getTime());
    }

    public static String getUTCDateTime1(String time) {
        String formattedDate = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm aa");
//			df.setTimeZone(TimeZone.getDefault());
            Date date = df.parse(time);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            formattedDate = df.format(date);
            System.out.println("time: " + time);
            System.out.println("formattedDate: " + formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;

    }

    public static String getUTCDateTime2(String time) {
        String formattedDate = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy hh:mm aa");
//			df.setTimeZone(TimeZone.getDefault());
            Date date = df.parse(time);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            formattedDate = df.format(date);
            System.out.println("time: " + time);
            System.out.println("formattedDate: " + formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;

    }

    public static String getFormatedDate(String givenDate) {
        // 2015-01-05 08:16:53
        try {
            String formatedDate = "";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            SimpleDateFormat newDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date1 = dateFormat.parse(givenDate);
            Date date2 = new Date();

            Calendar cal1 = Calendar.getInstance(Locale.getDefault());
            Calendar cal2 = Calendar.getInstance(Locale.getDefault());
            Calendar cal3 = Calendar.getInstance(Locale.getDefault());

            cal1.setTime(date1);
            cal2.setTime(date2);
            cal3.setTime(date2);

            cal3.set(Calendar.DAY_OF_MONTH, cal3.get(Calendar.DAY_OF_MONTH) - 1);

            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                formatedDate = "Today " + timeFormat.format(date1);
            } else if (cal1.get(Calendar.YEAR) == cal3.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal3.get(Calendar.DAY_OF_YEAR)) {
                formatedDate = "Yesterday " + timeFormat.format(date1);
            } else {
                formatedDate = newDateFormat.format(date1);
            }
            return formatedDate;
        } catch (Exception e) {
            e.printStackTrace();
            return givenDate;
        }
    }

    public static String getUTCDateTime(int mins) {
        Calendar currentdate = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT_8);
        TimeZone obj = TimeZone.getTimeZone("UTC");
        formatter.setTimeZone(obj);

        int mHour = currentdate.get(Calendar.HOUR_OF_DAY);
        int mMins = currentdate.get(Calendar.MINUTE);
        int mSecs = currentdate.get(Calendar.SECOND);
        mMins += mins;
        if (mMins > 59) {
            mHour++;
            if (mHour > 23)
                mHour = 0;
            mMins = mMins - 60;
        }
        currentdate.set(currentdate.get(Calendar.YEAR), currentdate.get(Calendar.MONTH), currentdate.get(Calendar.DAY_OF_MONTH), mHour, mMins + 15, mSecs);
        return "" + formatter.format(currentdate.getTime());
//		return StringUtils.pad(mHour)+":"+StringUtils.pad(mMins)+":"+StringUtils.pad(mSecs);

    }
    //"2015-05-27 13:24:43"

    public static String getUTCDateTime(String time) {
        String formattedDate = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
//			df.setTimeZone(TimeZone.getDefault());
            Date date = df.parse(time);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            formattedDate = df.format(date);
            System.out.println("time: " + time);
            System.out.println("formattedDate: " + formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;

    }

    //GET CURRENT DATE TIME
    public static String getDateWithMMMMFormat() {
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        return format.format(curDate);
    }

    //GET CURRENT TIME ONLY
    public static String getTimeOnly() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(cal.getTime());
    }

    public static String getChatDateOrTime(String dateTime) {
        String[] dateAndTime = dateTime.split("/");


        if (compareMMMMByCompareTo(dateAndTime[0], getDateWithMMMMFormat()) == 0) {
            return dateAndTime[1];
        } else {
            return dateAndTime[0];
        }
    }

    public static int compareMMMMByCompareTo(String fromDate, String toDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");

            Date from = format.parse(fromDate);
            Date to = format.parse(toDate);
            if (from.compareTo(to) > 0) {
                //System.out.println("Date1 is after Date2");
                return 1;
            } else if (from.compareTo(to) < 0) {
                //System.out.println("Date1 is before Date2");
                return -1;
            } else if (from.compareTo(to) == 0) {
                //System.out.println("Date1 is equal to Date2");
                return 0;
            } else {
                //System.out.println("How to get here?");
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return 2;
    }

//	 public static String getNewDateFormat(String dateStr){
//		 String finaldateFormat = "";
//		 String[] dateArr = dateStr.split(" ");
//		 String date = dateArr[0];
//		 String time = dateArr[1];
//		 String[] newDateArr = date.split("-");
//		 String year = newDateArr[0];
//		 String month = newDateArr[1];
//		 String dd = newDateArr[2];
//		 finaldateFormat = month + dd + year + " " + time;
//		return finaldateFormat;
//		 
//	 }

    public static String changeDataFormat(String dateStr) {
        try {
            DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // parse the date string into Date object
            Date date = srcDf.parse(dateStr);
            DateFormat destDf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            // format the date into another format
            dateStr = destDf.format(date);
            System.out.println("Converted date is : " + dateStr);
            return dateStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // April 24,2015
    public static String changeDataFormat1(String dateStr) {
        try {
            DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // parse the date string into Date object
            Date date = srcDf.parse(dateStr);
            DateFormat destDf = new SimpleDateFormat("MMM dd, yyyy");
            // format the date into another format
            dateStr = destDf.format(date);
            System.out.println("Converted date is : " + dateStr);
            return dateStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String changeDataFormat6(String dateStr) {
        try {
            DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSZ");
            // parse the date string into Date object
            Date date = srcDf.parse(dateStr);
            DateFormat destDf = new SimpleDateFormat(DATE_FORMAT_1);      //MMM dd, yyyy, hh:mm:ss a
            // format the date into another format
            dateStr = destDf.format(date);
            System.out.println("Converted date is : " + dateStr);
            return dateStr;
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getLocalCurrentTime() {
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        return format.format(curDate);
    }

    // April 24,2015 10:33:20 PM
    public static String changeDataFormat2(String dateStr) {
        try {
            DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // parse the date string into Date object
            Date date = srcDf.parse(dateStr);
            DateFormat destDf = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a");      //MMM dd, yyyy, hh:mm:ss a
            // format the date into another format
            dateStr = destDf.format(date);
            System.out.println("Converted date is : " + dateStr);
            return dateStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String changeDataFormat3(String dateStr) {
        try {
            DateFormat srcDf = new SimpleDateFormat(FORMAT2);
            // parse the date string into Date object
            Date date = srcDf.parse(dateStr);
            DateFormat destDf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            // format the date into another format
            dateStr = destDf.format(date);
            System.out.println("Converted date is : " + dateStr);
            return dateStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String changeDataFormat4(String dateStr) {
        try {
            DateFormat srcDf = new SimpleDateFormat("MM/dd/yy hh:mm aa");
            // parse the date string into Date object
            Date date = srcDf.parse(dateStr);
            DateFormat destDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // format the date into another format
            dateStr = destDf.format(date);
            System.out.println("Converted date is : " + dateStr);
            return dateStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String changeDataFormat5(String dateStr) {
        try {
            DateFormat srcDf = new SimpleDateFormat(DATE_FORMAT_10);
            // parse the date string into Date object
            Date date = srcDf.parse(dateStr);
            DateFormat destDf = new SimpleDateFormat(DATE_FORMAT_3);      //MMM dd, yyyy, hh:mm:ss a
            // format the date into another format
            dateStr = destDf.format(date);
            System.out.println("Converted date is : " + dateStr);
            return dateStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getLocalTime(String created_at) {
        String formattedDate = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(created_at);
            df.setTimeZone(TimeZone.getDefault());
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(date);
//				cal.add(Calendar.MINUTE,-2);
            formattedDate = df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;

    }


    public static boolean isValidEventDate(String expDate) {
        boolean isValid = false;
        long selectedTime = DateUtils.getTimeInMillis(expDate, FORMAT2);
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT2);
        String currentDateandTime = sdf.format(new Date());
        long currentTime = DateUtils.getTimeInMillis(currentDateandTime, FORMAT2);

        if (selectedTime == currentTime) {
            isValid = true;
        } else if (selectedTime > currentTime) {
            isValid = true;
        }
        return isValid;

    }

    public static boolean isEndDateGreater(String startDate, String endDate) {
        boolean isValid = false;
        long startTime = DateUtils.getTimeInMillis(startDate, FORMAT2);
        long endTime = DateUtils.getTimeInMillis(endDate, FORMAT2);
        if (endTime > startTime) {
            isValid = true;
        }
        return isValid;
    }
}
