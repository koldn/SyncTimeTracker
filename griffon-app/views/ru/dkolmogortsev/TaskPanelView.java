package ru.dkolmogortsev;

import static ru.dkolmogortsev.utils.TimeEntryUiHelper.getConstraints;
import static ru.dkolmogortsev.utils.TimeEntryUiHelper.getTimeFromMills;

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javax.inject.Inject;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
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

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonView.class)
public class TaskPanelView extends AbstractGriffonView
{
    private static final String pattern = "dd/MM/yyyy"; //TODO make it configurable

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
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.prefHeight(400);
        scrollPane.prefWidthProperty().bind(parentView.getContainerPane().widthProperty());
        entriesPane = new DailyGridContainer();
        entriesPane.prefWidthProperty().bind(scrollPane.widthProperty());
        scrollPane.setContent(entriesPane);
        parentView.getContainerPane().addRow(1, scrollPane);

        EventStreams.changesOf(model.getMap()).subscribe(change ->
        {
            long date = change.getKey();
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

    private GridPane buildTimeEntryLine(Task t, TimeEntry timeEntry)
    {
        GridPane header = (GridPane)parentView.getPane().getChildren().get(0);//Always header
        GridPane entry = new GridPane();

        entry.focusTraversableProperty().set(true);
        entry.getColumnConstraints().addAll(getConstraints());

        entry.prefWidthProperty().bind(parentView.getPane().widthProperty());
        TimeEntryButton timeEntryButton = new TimeEntryButton();
        toStartTaskView(timeEntryButton);
        timeEntryButton.prefHeightProperty().bind(header.heightProperty().multiply(0.75));

        TimeEntryButton deleteButton = new TimeEntryButton();
        toDeleteEntryButton(deleteButton);
        deleteButton.prefHeightProperty().bind(header.heightProperty().multiply(0.75));

        EventStreams.eventsOf(timeEntryButton, MouseEvent.MOUSE_CLICKED)
                .subscribe(mouseEvent -> publishTaskStarted(t.getId()));

        deleteButton.setOnAction(event -> publishEntryDeleted(timeEntry));

        String startStopString = new StringBuilder().append(getTimeFromMills(timeEntry.getStart())).append(" -> ").append(getTimeFromMills(timeEntry.getEnd())).toString();
        Label startStopLabel = new Label(startStopString);
        startStopLabel.setTextAlignment(TextAlignment.CENTER);
        entry.addRow(0, new Label(t.getDescription()), new Label(t.getTaskName()), new Label(ElapsedTimeFormatter.formatElapsed(new Duration(timeEntry.getDuration()).getStandardSeconds())),
                startStopLabel, timeEntryButton, deleteButton);

        initHover(entry, timeEntryButton, deleteButton);
        return entry;
    }

    private void toDeleteEntryButton(TimeEntryButton deleteButton)
    {
        bindHoverIcons(deleteButton, "time.entry.delete.hover", "time.entry.delete.normal");
    }

    private void initHover(GridPane entry, TimeEntryButton timeEntryButton, TimeEntryButton deleteButton)
    {
        entry.backgroundProperty().bind(Bindings.when(entry.hoverProperty())
                .then(new Background(new BackgroundFill(Color.web("#fcf0b3"), null, null)))
                .otherwise(new Background(new BackgroundFill(Color.TRANSPARENT, null, null))));

        deleteButton.visibleProperty().bind(Bindings.when(entry.hoverProperty()).then(true).otherwise(false));
        timeEntryButton.visibleProperty().bind(Bindings.when(entry.hoverProperty()).then(true).otherwise(false));
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

    private DayGridPane buildDayGrid(long date, List<TimeEntry> entries)
    {
        DayGridPane pane = new DayGridPane(date);
        pane.prefWidthProperty().bind(((GridPane)parentView.getPane().getChildren().get(0)).widthProperty());
        GridPane dayHeader = new GridPane();
        ReadOnlyDoubleProperty headerHeight = ((GridPane)parentView.getPane().getChildren().get(0)).heightProperty();

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);

        dayHeader.getColumnConstraints().addAll(c1, c2);
        dayHeader.prefWidthProperty().bind(((GridPane)parentView.getPane().getChildren().get(0)).widthProperty());
        LocalDate localDate = new LocalDate(date);
        boolean isToday = localDate.equals(LocalDate.now());
        Label currentDayLabel = new Label(isToday ? "Today" : localDate.toString(pattern));
        currentDayLabel.setAlignment(Pos.CENTER_LEFT);
        currentDayLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        currentDayLabel.getStyleClass().addAll("lbl-info", "daygrid-lbl");
        currentDayLabel.prefHeightProperty().bind(headerHeight.multiply(0.60));
        Label dayDuration = new Label(ElapsedTimeFormatter.formatElapsed(
                new Duration(entries.stream().mapToLong(TimeEntry::getDuration).sum()).getStandardSeconds()));
        dayDuration.setAlignment(Pos.CENTER_RIGHT);
        dayDuration.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        dayDuration.getStyleClass().addAll("lbl-info", "daygrid-lbl");
        dayHeader.addRow(0, currentDayLabel, dayDuration);
        pane.addRow(0, dayHeader);
        FlowPane e = new FlowPane();

        Map<Long, List<TimeEntry>> dups = entries.stream()
                .collect(Collectors.groupingBy(o -> o.getTaskId(), Collectors.toList()));
        dups.forEach((aLong, entries1) -> buildEntriesNew(e, aLong, entries1));
        pane.addRow(1, e);
        return pane;
    }

    private void buildEntriesNew(FlowPane parentPane, long taskId, List<TimeEntry> entries)
    {
        Task task = storage.getTask(taskId);
        List<GridPane> uiLines = entries.stream().map(timeEntry -> buildTimeEntryLine(task, timeEntry))
                .collect(Collectors.toList());
        if (uiLines.size() == 1)
        {
            parentPane.getChildren().add(uiLines.get(0));
            return;
        }

        long overAllDuration = entries.stream().mapToLong(TimeEntry::getDuration).sum();

        FlowPane ents = new FlowPane();
        ents.prefWidthProperty().bind(parentPane.widthProperty());
        ToggleButton tg = new ToggleButton();
        tg.setText(String.valueOf(entries.size()));
        tg.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tg.getStyleClass().clear();

        EventStreams.changesOf(tg.selectedProperty()).subscribe(booleanChange ->
        {
            boolean newVal = booleanChange.getNewValue();
            if (newVal)
            {
                ents.getChildren().addAll(1, uiLines);
            }
            else
            {
                ents.getChildren().removeAll(uiLines);
            }
        });

        GridPane controlPane = (GridPane)parentView.getPane().getChildren().get(0);//Always header

        TimeEntryButton timeEntryButton = new TimeEntryButton();
        toStartTaskView(timeEntryButton);
        timeEntryButton.prefHeightProperty().bind(controlPane.heightProperty().multiply(0.75));

        TimeEntryButton deleteButton = new TimeEntryButton();
        toDeleteEntryButton(deleteButton);
        deleteButton.prefHeightProperty().bind(controlPane.heightProperty().multiply(0.75));

        EventStreams.eventsOf(deleteButton, MouseEvent.MOUSE_CLICKED)
                .subscribe(mouseEvent -> entries.forEach(timeEntry ->
                {
                    publishEntryDeleted(timeEntry);
                }));

        EventStreams.eventsOf(timeEntryButton, MouseEvent.MOUSE_CLICKED)
                .subscribe(mouseEvent -> publishTaskStarted(taskId));

        ReadOnlyDoubleProperty headerHeight = ((GridPane)parentView.getPane().getChildren().get(0)).heightProperty();
        GridPane groupedEntry = new GridPane();
        groupedEntry.getColumnConstraints().addAll(getConstraints());
        groupedEntry.prefWidthProperty().bind(((GridPane)parentView.getPane().getChildren().get(0)).widthProperty());
        groupedEntry.prefHeightProperty().bind(headerHeight.multiply(0.60));
        groupedEntry.addRow(0, new Label(task.getDescription()), new Label(task.getTaskName()),
                new Label(ElapsedTimeFormatter.formatElapsed(new Duration(overAllDuration).getStandardSeconds())), tg,
                timeEntryButton, deleteButton);
        ents.getChildren().add(0, groupedEntry);

        initHover(groupedEntry, timeEntryButton, deleteButton);

        parentPane.getChildren().add(ents);
    }

    private void toStartTaskView(TimeEntryButton timeEntryButton)
    {
        bindHoverIcons(timeEntryButton, "time.entry.start.hover", "time.entry.start.normal");
    }

    private void publishTaskStarted(long taskId)
    {
        getApplication().getEventRouter().publishEvent(new StartTask(taskId));
    }

    private void publishEntryDeleted(TimeEntry timeEntry)
    {
        getApplication().getEventRouter().publishEvent(new TimeEntryDeleted(timeEntry.getId()));
    }
}
