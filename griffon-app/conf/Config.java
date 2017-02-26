import griffon.util.AbstractMapResourceBundle;

import javax.annotation.Nonnull;
import java.util.Map;

import static java.util.Arrays.asList;
import static griffon.util.CollectionUtils.map;

public class Config extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        map(entries)
            .e("application", map()
                .e("title", "time-tracker")
                .e("startupGroups", asList("mainView"))
                .e("autoShutdown", true)
            )
            .e("mvcGroups", map()
                .e("controlPanel", map()
                    .e("model", "ru.dkolmogortsev.ControlPanelModel")
                    .e("view", "ru.dkolmogortsev.ControlPanelView")
                    .e("controller", "ru.dkolmogortsev.ControlPanelController")
                )
                    .e("taskPanel", map()
                            .e("model", "ru.dkolmogortsev.TaskPanelModel")
                            .e("view", "ru.dkolmogortsev.TaskPanelView")
                            .e("controller", "ru.dkolmogortsev.TaskPanelController")
                    )
                    .e("mainView",map().e("view","ru.dkolmogortsev.ControlAndTaskView"))
            );
    }
}