package com.bupt.easydriver.util;

import android.text.TextUtils;

import com.bupt.easydriver.database.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shinelon on 2017/7/17.
 */

public class Utility {
    public static boolean handleStudentResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allStudent = new JSONArray(response);
                int len = allStudent.length();
                for(int i = 0; i < len; i++){
                    JSONObject studentObject = allStudent.getJSONObject(i);
                    Student student = new Student();
                    student.setId(studentObject.getString("sid"));
                    student.setName(studentObject.getString("name"));
                    student.setSex(studentObject.getString("sex"));
                    student.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
