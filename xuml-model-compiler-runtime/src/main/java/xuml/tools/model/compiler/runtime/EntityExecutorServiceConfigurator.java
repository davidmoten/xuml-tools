package xuml.tools.model.compiler.runtime;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.typesafe.config.Config;

import akka.dispatch.DispatcherPrerequisites;
import akka.dispatch.ExecutorServiceConfigurator;
import akka.dispatch.ExecutorServiceFactory;

public class EntityExecutorServiceConfigurator extends ExecutorServiceConfigurator {

    public EntityExecutorServiceConfigurator(Config config, DispatcherPrerequisites prerequisites) {
        super(config, prerequisites);
    }

    @Override
    public ExecutorServiceFactory createExecutorServiceFactory(String id, ThreadFactory factory) {
        return new ExecutorServiceFactory() {

            @Override
            public ExecutorService createExecutorService() {
                return Executors.newFixedThreadPool(5);
            }
        };
    }

}
