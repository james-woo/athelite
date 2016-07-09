package com.athelite.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StringDifference {
    public static String difference(String str1, String str2) {
        if(str1.length() < str2.length()){
            String temp = str1;
            str1 = str2;
            str2 = temp;
        }

        char[] c1 = str1.toCharArray();
        char[] c2 = str2.toCharArray();

        List<Character> list1 = new ArrayList<Character>();
        for (char c : c1) {
            list1.add(c);
        }

        List<Character> list2 = new ArrayList<Character>();
        for (char c : c2) {
            list2.add(c);
        }

        Collections.sort(list1);
        Collections.sort(list2);
        for(int i = 0; i < list1.size() - 1; i++) {
            if(list1.get(i) != list2.get(i)) {
                return String.valueOf(list1.get(i));
            }
        }

        return String.valueOf(list1.get(list1.size() - 1));
    }
}
