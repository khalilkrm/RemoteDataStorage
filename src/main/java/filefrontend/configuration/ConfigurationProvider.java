package filefrontend.configuration;

public interface ConfigurationProvider {

    Configuration getReadInstance();
    Configuration getWriteInstance();

}
