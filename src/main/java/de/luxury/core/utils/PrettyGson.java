package de.luxury.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PrettyGson {

    public final static Gson prettyGson;
    public final static Gson normalGson;

    static {
        prettyGson = new GsonBuilder().setPrettyPrinting().create();
        normalGson = new Gson();
    }

}
