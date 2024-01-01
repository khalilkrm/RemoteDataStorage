package storbackend.configuration;


public interface ConfigurationProvider {

    Configuration getReadInstance();
    Configuration getWriteInstance();

}
