package com.papercut.silken;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class TemplateParser {
    
    private TemplateParser() {}
    private static final Pattern aliasPattern = Pattern.compile("\\{alias\\s+([\\.\\w]+)");
    private static final Pattern callPattern = Pattern.compile("\\{call\\s+([\\.\\w]+?)\\.\\w+\\s");
    
    public static List<String> referencedNamespaces(URL template) {
        Preconditions.checkNotNull(template, "template can't be null");
        
        File f;
        try {
            f = new File(template.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid template reference", e);
        }
        
        try {
            return Files.readLines(f, Charsets.UTF_8, new LineProcessor<List<String>>() {

                private Set<String> refs = Sets.newHashSet();
                
                @Override
                public List<String> getResult() {
                    return Lists.newArrayList(refs);
                }

                @Override
                public boolean processLine(String line) throws IOException {
                    Matcher aliasMatcher = aliasPattern.matcher(line);
                    while (aliasMatcher.find()) {
                        refs.add(aliasMatcher.group(1));
                    }
                    Matcher callMatcher = callPattern.matcher(line);
                    while (callMatcher.find()) {
                        String ns = callMatcher.group(1);
                        if (!Strings.isNullOrEmpty(ns)) {
                            refs.add(ns);
                        }
                    }
                    return true;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse template.", e);
        }
    }

}
