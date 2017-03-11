package ru.dkolmogortsev;

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import java.util.List;
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
import ru.dkolmogortsev.controls.TimeEntryButton;
import ru.dkolmogortsev.customui.DailyGridContainer;
import ru.dkolmogortsev.customui.DayGridPane;
import ru.dkolmogortsev.messages.StartTask;
import ru.dkolmogortsev.messages.TimeEntryDeleted;
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

    private DailyGridContainer entriesPane;

    @Override
    public void initUI()
    {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.fitToWidthProperty();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.prefHeight(400);
        scrollPane.prefWidthProperty().bind(parentView.getAnchorPane().widthProperty());
        entriesPane = new DailyGridContainer();
        entriesPane.prefWidthProperty().bind(scrollPane.widthProperty());
        scrollPane.setContent(entriesPane);
        parentView.getAnchorPane().addRow(1, scrollPane);

        EventStreams.changesOf(model.getMap()).subscribe(change ->
        {
            String date = change.getKey();
            List<TimeEntry> list = change.getMap().get(date);
            int gridIndex = entriesPane.getGridIndex(date);
            if (gridIndex != -1)
            {
                entriesPane.getChildren().set(gridIndex, buildDayGrid(date, list));
            }
            else
            {
                entriesPane.getChildren().add(0, buildDayGrid(date, list));
            }

        });
    }

    private void buildTimeEntryLine(TimeEntry timeEntry, FlowPane holderPane)
    {
        GridPane header = (GridPane)parentView.getPane().getChildren().get(0);//Always header
        GridPane entry = new GridPane();
        entry.getColumnConstraints().addAll(TimeEntryUiHelper.getConstraints());
        Task t = storage.getTask(timeEntry.getTaskId());

        entry.prefWidthProperty().bind(holderPane.widthProperty());
        TimeEntryButton timeEntryButton = new TimeEntryButton(t);
        bindHoverIcons(timeEntryButton, "time.entry.start.hover", "time.entry.start.normal");
        timeEntryButton.prefHeightProperty().bind(header.heightProperty().multiply(0.75));

        TimeEntryButton deleteButton = new TimeEntryButton(t);
        bindHoverIcons(deleteButton, "time.entry.delete.hover", "time.entry.delete.normal");
        deleteButton.prefHeightProperty().bind(header.heightProperty().multiply(0.75));

        EventStreams.eventsOf(timeEntryButton, MouseEvent.MOUSE_CLICKED)
                .subscribe(mouseEvent -> getApplication().getEventRouter().publishEvent(new StartTask(t.getUUID())));

        deleteButton.setOnAction(
                event -> getApplication().getEventRouter().publishEvent(new TimeEntryDeleted(timeEntry.getId())));
        entry.addRow(0, new Label(t.getDescription()), new Label(t.getTaskName()), new Label(
                        ElapsedTimeFormatter.formatElapsed(new Duration(timeEntry.getDuration()).getStandardSeconds())),
                new Label(TimeEntryUiHelper.formatDate(timeEntry.getStart())),
                new Label(TimeEntryUiHelper.formatDate(timeEntry.getEnd())), timeEntryButton, deleteButton);
        holderPane.getChildren().add(0, entry);
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

    private DayGridPane buildDayGrid(String date, List<TimeEntry> entries)
    {
        DayGridPane pane = new DayGridPane(date);
        pane.prefWidthProperty().bind(entriesPane.widthProperty());
        GridPane dayHeader = new GridPane();
        dayHeader.prefWidthProperty().bind(pane.widthProperty());

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);

        dayHeader.getColumnConstraints().addAll(c1, c2);

        dayHeader.addRow(0, new Label(date), new Label(ElapsedTimeFormatter.formatElapsed(
                new Duration(entries.stream().mapToLong(TimeEntry::getDuration).sum()).getStandardSeconds())));
        pane.addRow(0, dayHeader);
        FlowPane e = new FlowPane();
        e.prefWidthProperty().bind(entriesPane.widthProperty());
        entries.forEach(timeEntry -> buildTimeEntryLine(timeEntry, e));
        pane.addRow(1, e);
        return pane;
    }

}
