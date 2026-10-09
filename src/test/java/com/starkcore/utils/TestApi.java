package com.starkcore.utils;

import org.junit.Assert;
import org.junit.Test;


public class TestApi {

    private static String plural(String name) {
        return Api.getLastNamePlural(new Resource.ClassData(Object.class, name));
    }

    @Test
    public void testPluralOfNameEndingInCh() {
        Assert.assertEquals("speeches", plural("AiSpeech"));
    }

    @Test
    public void testPluralOfNameEndingInSh() {
        Assert.assertEquals("dashes", plural("Dash"));
    }

    @Test
    public void testPluralOfNameEndingInX() {
        Assert.assertEquals("boxes", plural("Box"));
    }

    @Test
    public void testPluralOfRegularName() {
        Assert.assertEquals("logs", plural("Log"));
    }

    @Test
    public void testPluralOfNameEndingInY() {
        Assert.assertEquals("entities", plural("Entity"));
    }

    @Test
    public void testPluralOfNameEndingInEy() {
        Assert.assertEquals("keys", plural("Key"));
    }

    @Test
    public void testPluralOfNameEndingInS() {
        Assert.assertEquals("address", plural("Address"));
    }
}
