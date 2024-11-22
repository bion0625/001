package com.uj.stxtory.util;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class FormatUtil {

    public static Date stringToDate(String str) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            date = str.equals("") ? null : sdf.parse(str);
        } catch (Exception e) {
            log.info(String.format("stringToDate : str >>>> %s", str));
        }
        return date;
    }

    public static long stringToLong(String str) {
        long result = 0L;
        str = str.equals("") ? "0" : str;
        try {
            str = str.replaceAll(",", "");
            result = Long.parseLong(str);
        } catch (Exception e) {
            log.info(String.format("stringToLong : str >>>> %s", str));
        }
        return result;
    }

    public static double StringToDouble(String str) {
        double result = 0;
        if ("null".equals(str) || str == null) return result;
        str = str.equals("") ? "0" : str;
        try {
            str = str.replaceAll(",", "");
            result = Double.parseDouble(str);
        } catch (Exception e) {
            log.info(String.format("StringToDouble : str >>>> %s", str));
        }
        return result;
    }
}
