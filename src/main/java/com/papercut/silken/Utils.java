package com.papercut.silken;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.restricted.SoyMsg;
import com.google.template.soy.msgs.restricted.SoyMsgBundleImpl;

/**
 * Various utility methods.
 * 
 * @author chris
 */
public class Utils {

    private Utils() {}
   
    
    /**
     * Convert all data stored in a POJO or Map<String, Object> into a format compatible with Soy's DataMap.
     * This method will convert nested POJOs to a corresponding nested Maps.
     * 
     * @param obj The Map or POJO who's data should be converted.
     * @return A Map of data compatible with Soy.
     */
    @SuppressWarnings("unchecked")
    public static  Map<String, ?> toSoyCompatibleMap(Object obj) {
        Object ret = toSoyCompatibleObjects(obj);
        if (!(ret instanceof Map)) {
            throw new IllegalArgumentException("Input should be a Map or POJO.");
        }
        
        return (Map<String, ?>) ret;
    }
    
    /**
     * Convert an object (or graph of objects) to types compatible with Soy (data able to be stored in SoyDataMap).
     * This will convert:
     *    - POJOs to Maps
     *    - Iterables to Lists
     *    - all strings and primitives remain as is.
     *    
     * @param obj The object to convert.
     * @return The object converted (in applicable).
     */
    public static Object toSoyCompatibleObjects(Object obj) {
        
        if (obj == null) {
            return obj;
        }

        if (Primitives.isWrapperType(obj.getClass()) 
                || obj.getClass().isPrimitive()
                || obj instanceof String) {
            return obj;
        }

        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            Map<String, Object> newMap = new HashMap<String, Object>(map.size());
            for (String key : map.keySet()) {
                newMap.put(key, toSoyCompatibleObjects(map.get(key)));
            }
            return newMap;
        }

        if (obj instanceof Iterable<?>) {
            List<Object> list = Lists.newArrayList();
            for (Object subValue : ((Iterable<?>) obj)) {
                list.add(toSoyCompatibleObjects(subValue));
            }            
            return list;
        }
        
        if (obj.getClass().isArray()) {
            return obj;
        }

        // At this point we must assume it's a POJO so map-it.
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> pojoMap = (Map<String, Object>) pojoToMap(obj);
            Map<String, Object> newMap = new HashMap<String, Object>(pojoMap.size());
            for (String key : pojoMap.keySet()) {
                newMap.put(key, toSoyCompatibleObjects(pojoMap.get(key)));
            }
            return newMap;
        }
    }
    
    
    /**
     * Convert a Java POJO to a Map<String, Object>.
     * @param pojo The Java pojo object with standard getters and setters.
     * @return Pojo data as a Map.
     */
    public static Map<String, ?> pojoToMap(Object pojo) {

        Map<String, Object> map = new HashMap<String, Object>();

        for (Method method : pojo.getClass().getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0
                    && method.getReturnType() != void.class && method.getName().matches("^(get|is).+")) {
                
                String name = method.getName().replaceAll("^(get|is)", "");
                name = Character.toLowerCase(name.charAt(0)) + (name.length() > 1 ? name.substring(1) : "");
                
                Object value;
                try {
                    value = method.invoke(pojo);
                    map.put(name, value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return map;
    }
    
    /**
     * Merge two SoyMapData resources.  If the two maps to merge contain duplicate key names, source
     * s2 will overwrite s1.  
     * @param s1 1st resource map.
     * @param s2 2nd resource map.
     * @return A new SoyMapData object containing data from both source.
     */
    public static SoyMapData mergeSoyMapData(SoyMapData s1, SoyMapData s2) {
        Preconditions.checkNotNull(s1);
        Preconditions.checkNotNull(s2);
        SoyMapData merged = new SoyMapData();
        for (String key: s1.getKeys()) {
            merged.putSingle(key, s1.getSingle(key));
        }
        for (String key: s2.getKeys()) {
            merged.putSingle(key, s2.getSingle(key));
        }
        return merged;
    }
    
    /**
     * Take a list of SoyMsgBundles and merge. This is a little hacky but to make life easier we'll use an
     * internal/restricted class: SoyMsgBundleImpl.
     * 
     * @param soyMsgBundles
     *            A list of SoyMsgBundles to merge.
     * @return a merged set of messages.
     */
    public static SoyMsgBundle mergeMsgBundles(List<SoyMsgBundle> soyMsgBundles) {

        Preconditions.checkArgument(soyMsgBundles != null && soyMsgBundles.size() > 0, "No MsgBundels defined");

        String localString = "";
        List<SoyMsg> msgs = Lists.newArrayList();

        for (SoyMsgBundle smb : soyMsgBundles) {
            localString = smb.getLocaleString();
            Iterator<SoyMsg> iter = smb.iterator();
            while (iter.hasNext()) {
                msgs.add(iter.next());
            }
        }

        return new SoyMsgBundleImpl(localString, msgs);
    }
    

    /**
     * Referenced from: http://www.java2s.com/Code/Java/Data-Type/ConvertsaStringtoaLocale.htm
     * 
     * <p>Converts a String to a Locale.</p>
     *
     * <p>This method takes the string format of a locale and creates the
     * locale object from it.</p>
     *
     * <pre>
     *   LocaleUtils.toLocale("en")         = new Locale("en", "")
     *   LocaleUtils.toLocale("en_GB")      = new Locale("en", "GB")
     *   LocaleUtils.toLocale("en_GB_xxx")  = new Locale("en", "GB", "xxx")   (#)
     * </pre>
     *
     * <p>(#) The behaviour of the JDK variant constructor changed between JDK1.3 and JDK1.4.
     * In JDK1.3, the constructor upper cases the variant, in JDK1.4, it doesn't.
     * Thus, the result from getVariant() may vary depending on your JDK.</p>
     *
     * <p>This method validates the input strictly.
     * The language code must be lowercase.
     * The country code must be uppercase.
     * The separator must be an underscore.
     * The length must be correct.
     * </p>
     *
     * @param str  the locale String to convert, null returns null
     * @return a Locale, null if null input
     * @throws IllegalArgumentException if the string is an invalid format
     */
    public static Locale stringToLocale(String str) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len != 2 && len != 5 && len < 7) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        char ch0 = str.charAt(0);
        char ch1 = str.charAt(1);
        if (ch0 < 'a' || ch0 > 'z' || ch1 < 'a' || ch1 > 'z') {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        if (len == 2) {
            return new Locale(str, "");
        } else {
            if (str.charAt(2) != '_') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            char ch3 = str.charAt(3);
            if (ch3 == '_') {
                return new Locale(str.substring(0, 2), "", str.substring(4));
            }
            char ch4 = str.charAt(4);
            if (ch3 < 'A' || ch3 > 'Z' || ch4 < 'A' || ch4 > 'Z') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if (len == 5) {
                return new Locale(str.substring(0, 2), str.substring(3, 5));
            } else {
                if (str.charAt(5) != '_') {
                    throw new IllegalArgumentException("Invalid locale format: " + str);
                }
                return new Locale(str.substring(0, 2), str.substring(3, 5), str.substring(6));
            }
        }
    }

}
