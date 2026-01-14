package org.vniizht.suburbtransform;

import org.vniizht.suburbtransform.model.TransformationOptions;
import org.vniizht.suburbtransform.service.Transformation;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        Set<String> argsSet = new HashSet<>(Arrays.asList(args));
        Date requestDate = new Date();

        // Поиск даты в формате DDMMYYYYY
        String ddMMyyyy = argsSet.stream().filter(arg -> arg.matches("\\d{8}")).findFirst().orElse(null);
        if(ddMMyyyy != null) {
            requestDate.setDate(Integer.parseInt(ddMMyyyy.substring(0, 2)));
            requestDate.setMonth(Integer.parseInt(ddMMyyyy.substring(2, 4)) - 1);
            requestDate.setYear(Integer.parseInt(ddMMyyyy.substring(4, 8)) - 1900);
        } else {
            requestDate = null;
        }

        // Запуск
        Transformation.run(new TransformationOptions(requestDate,
                argsSet.contains("prig"),
                argsSet.contains("pass")));
    }
}
