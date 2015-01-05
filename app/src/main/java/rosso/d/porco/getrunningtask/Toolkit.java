package rosso.d.porco.getrunningtask;

import android.content.pm.ApplicationInfo;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by daniel_hsu on 2014/10/17.
 */
public class Toolkit {

    private static ArrayList<String> ADD_GAME_WHITE_LIST = new ArrayList<String>(Arrays.asList(
            "com.facebook.katana"));

    public static boolean isSystemApp(ApplicationInfo info) {

        for(int i=0;i<ADD_GAME_WHITE_LIST.size();i++){
            if( info.packageName.equals(ADD_GAME_WHITE_LIST.get(i)) )
                return false;
        }
        if ((info.flags & ApplicationInfo.FLAG_SYSTEM ) > 0 || info.packageName.equals("system")) {
            return true;
        } else {
            return false;
        }
    }
}
