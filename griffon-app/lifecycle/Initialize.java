import griffon.core.GriffonApplication;
import javafx.scene.Scene;
import org.codehaus.griffon.runtime.core.AbstractLifecycleHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collections;

public class Initialize extends AbstractLifecycleHandler {
    @Inject
    public Initialize(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Override
    public void execute() {
    }
}