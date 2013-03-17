package com.papercut.silken;

import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class TemplateParseTest extends TestCase {
    
    private URL exampleTemplateURL = getClass().getResource("exampleTemplate.soy");;

    @Test
    public void testCallNamespaceReference() {
        // arrange
        // act
        List<String> refs = TemplateParser.referencedNamespaces(exampleTemplateURL);
        // assert
        assertTrue(refs.size() > 0);
        assertTrue(refs.contains("my.namespace"));
    }
    
    @Test
    public void testAliasNamespaceReference() {
        // arrange
        // act
        List<String> refs = TemplateParser.referencedNamespaces(exampleTemplateURL);
        // assert
        assertTrue(refs.size() > 0);
        assertTrue(refs.contains("my.very.long.namespace"));
    }

}
