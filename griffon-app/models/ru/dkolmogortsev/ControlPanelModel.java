package ru.dkolmogortsev;

import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import java.time.Duration;
import java.util.List;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;
import ru.dkolmogortsev.utils.ElapsedTimeFormatter;

@ArtifactProviderFor(GriffonModel.class)
public class ControlPanelModel extends AbstractGriffonModel
{
    private SimpleIntegerProperty elapsedProperty = new SimpleIntegerProperty(0);
    private SimpleStringProperty currentTimeEntryId = new SimpleStringProperty();
    private SimpleBooleanProperty taskStarted = new SimpleBooleanProperty(false);
    private SimpleStringProperty timerTextProp = new SimpleStringProperty(
            ElapsedTimeFormatter.formatElapsed(elapsedProperty.get()));
    private SimpleStringProperty taskNameProperty = new SimpleStringProperty("Name");
    private SimpleStringProperty taskDescriptionProperty = new SimpleStringProperty("Description");
    private ListProperty<String> tasks = new SimpleListProperty<>();
    private Timer timer;
    public ControlPanelModel()
    {
        elapsedProperty.addListener((observable, oldValue, newValue) ->
        {
            System.out.println("changed");
            timerTextProp.set(ElapsedTimeFormatter.formatElapsed(newValue.intValue()));
        });
    }

    public SimpleStringProperty currentTimeEntryIdProperty()
    {
        return currentTimeEntryId;
    }

    public int getElapsedProperty()
    {
        return elapsedProperty.get();
    }

    public SimpleBooleanProperty taskStartedProperty()
    {
        return taskStarted;
    }

    public SimpleStringProperty taskNameProperty()
    {
        return taskNameProperty;
    }

    public SimpleStringProperty taskDescriptionProperty()
    {
        return taskDescriptionProperty;
    }

    public boolean isTaskStarted()
    {
        return taskStarted.get();
    }

    public SimpleStringProperty getTimerTextProp()
    {
        return timerTextProp;
    }

    public ListProperty<String> tasksProperty()
    {
        return tasks;
    }

    public void setList(List<String> tasks)
    {
        this.tasks.setValue(FXCollections.observableArrayList(tasks));
    }

    public void startTimer()
    {
        timer = FxTimer.runPeriodically(Duration.ofSeconds(1L), () -> elapsedProperty.set(elapsedProperty.get() + 1));
        taskStarted.set(true);
    }

    public void stopTimer()
    {
        elapsedProperty.set(0);
        timer.stop();
        taskStarted.set(false);
    }

}