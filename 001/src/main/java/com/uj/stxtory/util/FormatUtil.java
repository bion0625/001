package com.uj.stxtory.util;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class FormatUtil {

    public static Date stringToDate(String str) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            date = str.isEmpty() ? null : sdf.parse(str);
        } catch (Exception e) {
            log.info(String.format("stringToDate : str >>>> %s", str));
        }
        return date;
    }

    public static long stringToLong(String str) {
        long result = 0L;
        str = str.isEmpty() ? "0" : str;
        try {
            str = str.replaceAll(",", "");
            result = Long.parseLong(str);
        } catch (Exception e) {
            log.info(String.format("stringToLong : str >>>> %s", str));
        }
        return result;
    }

    public static double stringToDouble(String str) {
        double result = 0;
        if ("null".equals(str) || str == null) return result;
        str = str.isEmpty() ? "0" : str;
        try {
            str = str.replaceAll(",", "");
            result = Double.parseDouble(str);
        } catch (Exception e) {
            log.info(String.format("StringToDouble : str >>>> %s", str));
        }
        return result;
    }

    public static String dateToString(LocalDateTime date) {
        String str = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            str = date == null ? null : date.format(formatter);
        } catch (Exception e) {
            log.info(String.format("dateToString : date >>>> %s", date));
        }
        return str;
    }

    public static String shortDateToString(LocalDateTime date) {
        String str = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
            str = date == null ? null : date.format(formatter);
        } catch (Exception e) {
            log.info(String.format("dateToString : date >>>> %s", date));
        }
        return str;
    }
}
