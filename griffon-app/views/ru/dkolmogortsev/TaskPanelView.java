package ru.dkolmogortsev;

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javax.inject.Inject;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;
import org.reactfx.EventStreams;
import ru.dkolmogortsev.controls.TimeEntryStartButton;
import ru.dkolmogortsev.messages.StartTask;
import ru.dkolmogortsev.task.Task;
import ru.dkolmogortsev.task.TimeEntry;
import ru.dkolmogortsev.task.storage.TaskStorage;

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonView.class)
public class TaskPanelView extends AbstractGriffonView
{

    @MVCMember
    private ControlAndTaskView parentView;

    @MVCMember
    private TaskPanelModel model;

    @Inject
    private TaskStorage storage;

    private GridPane pane;

    private FlowPane entriesPane;

    @Override
    public void initUI()
    {
        ScrollPane scrollPane = new ScrollPane();
        //scrollPane.fitToHeightProperty();
        scrollPane.fitToWidthProperty();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.prefHeight(400);
        pane = new GridPane();
        pane.setGridLinesVisible(true);
        pane.prefWidthProperty().bind(scrollPane.widthProperty());
        //pane.prefHeightProperty().bind(scrollPane.heightProperty());
        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(100.0);
        pane.getColumnConstraints().addAll(c);
        pane.addRow(0, new Label("Today"));

        entriesPane = new FlowPane();
        entriesPane.setPrefWidth(pane.getWidth());
        pane.addRow(1, entriesPane);
        scrollPane.setContent(pane);
        parentView.getAnchorPane().addRow(1, scrollPane);

        EventStreams.changesOf(model.newTimeEntryProperty()).subscribe(timeEntryChange ->
        {
            TimeEntry newValue = timeEntryChange.getNewValue();
            GridPane entry = new GridPane();

            Task t = storage.getTask(newValue.getTaskId());

            entry.prefWidthProperty().bind(pane.widthProperty());
            TimeEntryStartButton timeEntryStartButton = new TimeEntryStartButton(t);
            EventStreams.eventsOf(timeEntryStartButton, MouseEvent.MOUSE_CLICKED).subscribe(
                    mouseEvent -> getApplication().getEventRouter().publishEvent(new StartTask(t.getUUID())));
            entry.addRow(0, new Label(newValue.getId()), new Label(String.valueOf(newValue.getStart())),
                    timeEntryStartButton);
            entriesPane.getChildren().add(0, entry);
        });

    }
}
