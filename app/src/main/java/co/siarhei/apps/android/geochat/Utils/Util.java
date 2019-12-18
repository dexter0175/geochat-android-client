
package co.siarhei.apps.android.geochat.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {

    public static boolean isOnline(Date updated_at){
        Date now = Calendar.getInstance().getTime();
        long diffInMillies = Math.abs(now.getTime() - updated_at.getTime());
        long diff = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diff<=120;
    }

    static  public String getChatName(String user1,String user2) {
        String chatName = "chat_";

        if (user1.compareTo(user2) > 0) {
            return chatName + user1 + "_" + user2;
        } else if (user1.compareTo(user2) < 0) {
            return chatName + user2 + "_" + user1;
        } else {
            return chatName + "self_" + user1;
        }
    }

    static  public Location getCurrentLocation(SharedPreferences prefs) {
        Location _loc = new Location("");

        _loc.setLatitude(Double.longBitsToDouble(prefs.getLong("locationLat", 0)));
        _loc.setLongitude(Double.longBitsToDouble(prefs.getLong("locationLong", 0)));
        return _loc;
    }
    public static String constructFeedbackContent(Intent data) {
        String answers_json = data.getExtras().getString("answers");

        return "Thank you so much for providing feedback for our Application.\n\n" +
                "This will allow us to improve the app for yourself and for new users in the future.\n\n" +
                "Please press \"Send Message\"...\n\n\n\n\n\n" +
                answers_json + "\n\n" +
                System.getProperty("os.version") + "\n" +
                ((Build.VERSION.SDK_INT > 0) ? Build.VERSION.SDK_INT : "") + "\n" +
                ((Build.DEVICE != null) ? Build.DEVICE : "") + "\n" +
                ((Build.MODEL != null) ? Build.MODEL : "") + "\n" +
                ((Build.PRODUCT != null) ? Build.PRODUCT : "") + "\n\n" +
                "==== message end ====";
    }

    public static String constructSurveyContent() {
        return
        "{\n" +
                "  \"survey_properties\": {\n" +
                "    \"intro_message\": \"<strong>Your feedback helps us to build a better GeoChat platform.</strong><br><br><br>It will take less than 2 minutes to answer the feedback quiz.\",\n" +
                "    \"end_message\": \"Thank you for giving the time to take our survey.\",\n" +
                "    \"skip_intro\": false\n" +
                "  },\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"question_type\": \"Radioboxes\",\n" +
                "      \"question_title\": \"Will you continue to use the GeoChat service?\",\n" +
                "      \"description\": \"\",\n" +
                "      \"required\": true,\n" +
                "      \"random_choices\": false,\n" +
                "      \"choices\": [\n" +
                "        \"Yes\",\n" +
                "        \"No\",\n" +
                "        \"Maybe\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"question_type\": \"StringMultiline\",\n" +
                "      \"question_title\": \"Has the GeoChat crashed or acted bizarrely while you have been using it?\",\n" +
                "      \"description\": \"\",\n" +
                "      \"required\": false,\n" +
                "      \"number_of_lines\": 3\n" +
                "    },\n" +
                "    {\n" +
                "      \"question_type\": \"StringMultiline\",\n" +
                "      \"question_title\": \"Did you have any technical problems using the app?\",\n" +
                "      \"description\": \"\",\n" +
                "      \"required\": false,\n" +
                "      \"number_of_lines\": 3\n" +
                "    },\n" +
                "    {\n" +
                "      \"question_type\": \"StringMultiline\",\n" +
                "      \"question_title\": \"How would you improve the GeoChat app?\",\n" +
                "      \"description\": \"\",\n" +
                "      \"required\": false,\n" +
                "      \"number_of_lines\": 3\n" +
                "    },\n" +
                "    {\n" +
                "      \"question_type\": \"StringMultiline\",\n" +
                "      \"question_title\": \"How might this GeoChat become useful to your day-to-day life?\",\n" +
                "      \"description\": \"\",\n" +
                "      \"required\": false,\n" +
                "      \"number_of_lines\": 3\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
