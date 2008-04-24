package org.buildng.flexmetrics.commons.lang;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


/**
 * Utility Methoden für Strings / String-Sets / String-Lists usw.
 * 
 * @author TIH
 */
public class StringUtil {
    
    /**
     * private ctor: no instances.
     */
    private StringUtil() {
        
    }

    // --------------------------------------------------------------------------
    // String-List/String-Set -> (Sorted) Array
    // --------------------------------------------------------------------------

    public static String[] convertToArray(List<String> pList) {
        return convertToArray(pList, false);
    }

    public static String[] convertToArray(List<String> pList, boolean sort) {
        String[] strings = new String[pList.size()];
        pList.toArray(strings);

        if (sort) {
            Arrays.sort(strings);
        }

        return strings;
    }

    public static String[] convertToArray(Set<String> pSet) {
        return convertToArray(pSet, false);
    }

    public static String[] convertToArray(Set<String> pSet, boolean sort) {
        String[] strings = new String[pSet.size()];
        pSet.toArray(strings);

        if (sort) {
            Arrays.sort(strings);
        }

        return strings;
    }
    
    
    // --------------------------------------------------------------------------
    // String-List/String-Set -> CSV-String
    // --------------------------------------------------------------------------

    public static String getCSVString(List<String> pList) {
        return getCSVString(pList, false);
    }

    public static String getCSVString(List<String> pList, boolean pSort) {
        return getCSVString(convertToArray(pList, pSort));
    }

    public static String getCSVString(List<String> pList, boolean pSort, String pSeparator) {
        return getCSVString(convertToArray(pList, pSort), pSeparator);
    }

    public static String getCSVString(Set<String> pSet) {
        return getCSVString(pSet, false);
    }

    public static String getCSVString(Set<String> pSet, boolean sort) {
        return getCSVString(convertToArray(pSet, sort));
    }

    public static String getCSVString(Set<String> pSet, boolean sort, String pSeparator) {
        return getCSVString(convertToArray(pSet, sort), pSeparator);
    }

    public static String getCSVString(String[] pStringArray) {
        return getCSVString(pStringArray, ",");
    }
    
    public static String getCSVString(String[] pStringArray, String pSeparator) {
        if (pStringArray.length > 0) {
            StringBuilder sb = new StringBuilder();

            sb.append(pStringArray[0]);

            for (int i = 1; i < pStringArray.length; i++) {
                sb.append(pSeparator);
                sb.append(pStringArray[i]);
            }

            return sb.toString();
        } else {
            return "";
        }
    }
}
