package ru.dkolmogortsev;

import griffon.core.event.EventHandler;
import griffon.core.injection.Module;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.infinispan.Cache;
import org.kordamp.jipsy.ServiceProviderFor;
import ru.dkolmogortsev.task.storage.InfinispanCacheManager;

import javax.inject.Inject;
import javax.inject.Named;

@ServiceProviderFor(Module.class)
public class ApplicationModule extends AbstractModule {


    @Override
    protected void doConfigure() {
        bind(EventHandler.class)
            .to(ApplicationEventHandler.class)
            .asSingleton();
    }
}