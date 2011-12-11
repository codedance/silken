package com.papercut.silken;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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
     * Convert a Java POJO to a Map.
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
     * Merge two SoyMapData resources.
     * @param s1 1st resource map.
     * @param s2 2nd resource map.
     * @return A new SoyMapData object containing data from both source.
     */
    public static SoyMapData mergeSoyMapData(SoyMapData s1, SoyMapData s2) {
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
    

}
