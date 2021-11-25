package com.juliovazquez.hsclinic.Tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tool_String {

    public static boolean ps_pattern (String ps) {
        Pattern pat = Pattern.compile("[0-9]+/{1}[0-9]+");
        Matcher mat = pat.matcher(ps);
        if (mat.matches()) {
            return true;
        } else {
            return false;
        }
    }

}
