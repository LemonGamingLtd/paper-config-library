package io.papermc.paper.configuration;

public abstract class ConfigurationPart {

    public static abstract class Post extends ConfigurationPart {

        public abstract void postProcess();
    }

}
