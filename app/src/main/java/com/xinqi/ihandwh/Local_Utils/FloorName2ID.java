package com.xinqi.ihandwh.Local_Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by presisco on 2015/11/17.
 */
public class FloorName2ID {
    private static String[] availableID;
    private static final String[] floors={"不限", "三楼", "四楼",
                                    "五楼", "六楼", "七楼",
                                    "八楼", "九楼", "十楼",
                                    "十一楼", "十二楼",
                                    "图东环楼三楼", "图东环楼四楼"};

    public static void setAvailableID(String[] data)
    {
    //  availableID=data;
        availableID=floors;
    }

    public static List<String> getAvailableFloors() {
        List<String> result = new ArrayList<String>();
        result.add(floors[0]);
        for (int i = 1; i < floors.length; ++i) {
            String id = getID(floors[i]);
            for (int j = 0; j < availableID.length; ++j) {
                if (availableID[j].equals(id)) {
                    result.add(floors[i]);
                    break;
                }
            }
        }
        return result;
    }

    public static List<String> getAllFloors(){
        ArrayList<String> full_list=new ArrayList<String>();
        for(String content:floors){
            full_list.add(content);
        }
        return full_list;
    }

    public static String getID(String floorName)
    {
        String id="000";
        switch(floorName)
        {
            case "三楼":id+="103";break;
            case "四楼":id+="104";break;
            case "五楼":id+="105";break;
            case "六楼":id+="106";break;
            case "七楼":id+="107";break;
            case "八楼":id+="108";break;
            case "九楼":id+="109";break;
            case "十楼":id+="110";break;
            case "十一楼":id+="111";break;
            case "十二楼":id+="112";break;
            case "图东环楼三楼":id+="203";break;
            case "图东环楼四楼":id+="204";break;
            default:id+="000";
        }
        return id;
    }
}
