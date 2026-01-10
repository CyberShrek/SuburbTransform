package org.vniizht.suburbtransform;

import org.vniizht.suburbtransform.database.SimpleJdbc;
import org.vniizht.suburbtransform.model.TransformationOptions;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        Set<String> argsSet = new HashSet<>(Arrays.asList(args));
        Date requestDate = new Date();

        new TransformationOptions(requestDate, argsSet.contains("prig"), argsSet.contains("pass"));

//        System.out.println(new HandbookDao());

//
//        // Поиск даты в формате DDMMYYYYY (8 цифр) и присвоение в requestDate
//        String ddMMyyyy = argsSet.stream().filter(arg -> arg.matches("\\d{8}")).findFirst().orElse(null);
//        if(ddMMyyyy != null) {
//            requestDate.setDate(Integer.parseInt(ddMMyyyy.substring(0, 2)));
//            requestDate.setMonth(Integer.parseInt(ddMMyyyy.substring(2, 4)) - 1);
//            requestDate.setYear(Integer.parseInt(ddMMyyyy.substring(4, 8)) - 1900);
//        } else {
//            requestDate.setDate(requestDate.getDate() - 1);
//        }
//
//        // Запуск функции бина
//        Transformation transformation = context.getBean(Transformation.class);
//        transformation.transform(new TransformationOptions(requestDate, argsSet.contains("prig"), argsSet.contains("pass")));
    }
}
