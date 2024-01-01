package filefrontend.configuration;

import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BlockingConfigurationProvider implements ConfigurationProvider {

    private final ReadWriteLock locksmith = new ReentrantReadWriteLock();

    private final Lock writeLocker = locksmith.writeLock();
    private final Lock readLocker = locksmith.readLock();
    private final Path path;

    private final Configuration configuration;

    private BlockingConfigurationProvider(final Configuration configuration, final Path path) {
        super();
        this.configuration = configuration;
        this.path = path;
    }

    public static BlockingConfigurationProvider from(final Configuration configuration, final Path path) {
        return new BlockingConfigurationProvider(configuration, path);
    }

    @Override
    public Configuration getWriteInstance() {
        try {
            writeLocker.lock();
            return configuration;
        } finally {
            writeLocker.unlock();
        }
    }

    @Override
    public Configuration getReadInstance() {
        try {
            readLocker.lock();
            return configuration;
        } finally {
            readLocker.unlock();
        }
    }
}
