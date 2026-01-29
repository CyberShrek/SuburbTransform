package org.vniizht.suburbtransform.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

    // Измеряет времени выполнения задачи в секундах
    public static float measureTime(Runnable task) {
        Date date = new Date();
        task.run();
        return (new Date().getTime() - date.getTime())/1000f;
    }

    // Форматирует дату в заданный формат
    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    // Объединяет два массива в один
    public static <T> T[] concatArrays(T[] array1, T[] array2) {
        T[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    // Доводит строку до нужной длины лидирующими нулями
    public static String addLeadingZeros(String str, int length) {
        if(str == null) str = "";
        StringBuilder zeros = new StringBuilder();
        for (int i = 0; i < length - str.length(); i++) {
            zeros.append('0');
        }
        return zeros.append(str).toString();
    }

    // Доводит строку до нужной длины нулями с конца
    public static String addTrailingZeros(String str, int length) {
        if(str == null) str = "";
        StringBuilder stringBuilder = new StringBuilder(str);
        while (stringBuilder.length() < length) {
            stringBuilder.append('0');
        }
        return stringBuilder.toString();
    }

    public static List<List<Long>> splitList(List<Long> prigIdnumsByRequestDate, int portionSize) {
        int portionCount = (int) Math.ceil(prigIdnumsByRequestDate.size() / (double) portionSize);
        List<List<Long>> portions = new ArrayList<>(portionCount);
        for (int i = 0; i < portionCount; i++) {
            int start = i * portionSize;
            int end = Math.min((i + 1) * portionSize, prigIdnumsByRequestDate.size());
            portions.add(prigIdnumsByRequestDate.subList(start, end));
        }
        return portions;
    }

    public static Map<String, Object> objectToMap(Object object) {
        Map<String, Object> map = new HashMap<>();
        for(Field field : object.getClass().getFields()) {
            try {
                map.put(field.getName(), field.get(object));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

}
