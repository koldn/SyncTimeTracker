package ru.dkolmogortsev;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;

import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;
import org.joda.time.Duration;
import org.reactfx.EventStreams;

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import ru.dkolmogortsev.controls.TimeEntryButton;
import ru.dkolmogortsev.messages.StartTask;
import ru.dkolmogortsev.task.Task;
import ru.dkolmogortsev.task.TimeEntry;
import ru.dkolmogortsev.task.storage.TaskStorage;
import ru.dkolmogortsev.task.storage.TimeEntriesStorage;
import ru.dkolmogortsev.utils.ElapsedTimeFormatter;
import ru.dkolmogortsev.utils.TimeEntryUiHelper;

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

    @Inject
    private TimeEntriesStorage entriesStorage;

    private GridPane pane;

    private FlowPane entriesPane;

    @Override
    public void initUI()
    {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.fitToWidthProperty();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
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
        entriesPane.prefWidthProperty().bind(pane.widthProperty());
        pane.addRow(1, entriesPane);
        scrollPane.setContent(pane);
        parentView.getAnchorPane().addRow(1, scrollPane);

        EventStreams.changesOf(model.groupedTimeEntriesProperty()).subscribe(mapChange -> {
            System.out.println(mapChange);
        });

        EventStreams.changesOf(model.newTimeEntryProperty()).subscribe(timeEntryChange -> {
            TimeEntry newValue = timeEntryChange.getNewValue();
            buildTimeEntryLine(newValue);
            entriesStorage.getEntriesGroupedByDay();
        });

    }

    private void buildTimeEntryLine(TimeEntry newValue)
    {
        GridPane header = (GridPane)parentView.getPane().getChildren().get(0);//Always header
        GridPane entry = new GridPane();
        entry.getColumnConstraints().addAll(TimeEntryUiHelper.getConstraints());
        entry.prefHeight(100);
        Task t = storage.getTask(newValue.getTaskId());

        entry.prefWidthProperty().bind(pane.widthProperty());
        TimeEntryButton timeEntryButton = new TimeEntryButton(t);
        bindHoverIcons(timeEntryButton, "time.entry.start.hover", "time.entry.start.normal");
        timeEntryButton.prefHeightProperty().bind(header.heightProperty().multiply(0.75));

        TimeEntryButton deleteButton = new TimeEntryButton(t);
        bindHoverIcons(deleteButton, "time.entry.delete.hover", "time.entry.delete.normal");
        deleteButton.prefHeightProperty().bind(header.heightProperty().multiply(0.75));

        EventStreams.eventsOf(timeEntryButton, MouseEvent.MOUSE_CLICKED)
                .subscribe(mouseEvent -> getApplication().getEventRouter().publishEvent(new StartTask(t.getUUID())));
        entry.addRow(0, new Label(t.getDescription()), new Label(t.getTaskName()),
                new Label(
                        ElapsedTimeFormatter.formatElapsed(new Duration(newValue.getDuration()).getStandardSeconds())),
                new Label(TimeEntryUiHelper.formatDate(newValue.getStart())),
                new Label(TimeEntryUiHelper.formatDate(newValue.getEnd())), timeEntryButton, deleteButton);
        entriesPane.getChildren().add(0, entry);
    }

    private void bindHoverIcons(TimeEntryButton timeEntryButton, String hoverIconPath, String normalIconPath)
    {
        ImageView hoverIcon = new ImageView(
                (String)getApplication().getResourceResolver().resolveResource(hoverIconPath));
        ImageView noHover = new ImageView(
                (String)getApplication().getResourceResolver().resolveResource(normalIconPath));
        timeEntryButton.graphicProperty()
                .bind(Bindings.when(timeEntryButton.hoverProperty()).then(hoverIcon).otherwise(noHover));
    }
}
