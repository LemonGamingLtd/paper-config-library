# paper-config-library
A temporary library for using Paper's internal config API, until they release it officially. The API will be unstable, use at your own risk!

## Usage

### Configuration creation
#### Configuration class
```java
public class MainConfig extends ConfigurationPart {

  @Comment("This is the kick message")
  public String kickMessage = "Default kick message";
  
  @Constraints.Min(10)
  public int maxPlayers;
  
  public MongoDatabase mongoDatabase;

  public class MongoDatabase extends ConfigurationPart {
    @Required
    public String uri;
  }

}
```

#### Loading the configuration
```java
PaperConfigLoader configLoader = new PaperConfigLoader();
MainConfig config = configLoader.load(new File("config.yml"), MainConfig.class);
```

### Adding extra serializers
#### Serializer class
```java
public class ComponentSerializer extends ScalarSerializer<Component> {
  public ComponentSerializer() {
    super(Component.class);
  }

  public Component deserialize(Type type, Object obj) throws SerializationException {
    return MiniMessage.miniMessage().deserialize(obj.toString());
  }

  protected Object serialize(Component component, Predicate<Class<?>> typeSupported) {
    return MiniMessage.miniMessage().serialize(component);
  }
}
```

#### Registering the serializer
```java
PaperConfigLoader configLoader = new PaperConfigLoader();
configLoader.options(options -> options.serializers(builder -> builder.register(new ComponentSerializer())));
MainConfig config = configLoader.load(new File("config.yml"), MainConfig.class);
```
