package org.processmining.filterbook.cells;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deckfour.uitopia.api.model.ViewType;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.contexts.uitopia.hub.ProMResourceManager;
import org.processmining.contexts.uitopia.hub.ProMViewManager;
import org.processmining.filterbook.filters.Filter;
import org.processmining.filterbook.filters.FilterTemplate;
import org.processmining.filterbook.filters.NewFilter;
import org.processmining.filterbook.notebook.Notebook;
import org.processmining.filterbook.parameters.Parameters;
import org.processmining.filterbook.types.LogType;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.impl.PluginManagerImpl;
import org.processmining.framework.util.Pair;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.framework.util.ui.widgets.ProMTextField;

import com.fluxicon.slickerbox.components.RoundedPanel;
import com.fluxicon.slickerbox.components.SlickerButton;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class ComputationCell extends Cell implements ListSelectionListener, ActionListener {

	private final ProMCanceller canceller;

	/*
	 * The list of filtered logs for this computation cell.
	 */
	private List<XLog> filteredLogs;
	/*
	 * The list of possible inputs logs for this computation cell.
	 */
	private List<LogType> inputLogs;
	/*
	 * The input log currently selected.
	 */
	private LogType inputLog;
	/*
	 * The output log of this computation cell.
	 */
	private LogType outputLog;
	/*
	 * The list of filters for this computation cell.
	 */
	private List<Filter> filters;
	/*
	 * The list of possible views for this computation cell.
	 */
	private List<ViewType> views;

	/*
	 * The widget showing the list of filters.
	 */
	private ProMList<Filter> filterList;
	/*
	 * The filter currently selected by the user.
	 */
	private Filter selectedFilter;
	/*
	 * The widget showing the list of possible views.
	 */
	private ProMList<ViewType> viewList;
	/*
	 * The view currently selected by the user.
	 */
	private ViewType selectedView;
	/*
	 * The widget showing the list of possible input logs.
	 */
	private ProMList<LogType> logList;
	/*
	 * The input log currently selected by the user.
	 */
	private LogType selectedLog;

	/*
	 * The widget for the computation cell.
	 */
	private JComponent widget;
	/*
	 * A widget for the name of the computation cell.
	 */
	private JComponent nameWidget;
	/*
	 * The main widget for the computation cell.
	 */
	private JComponent mainWidget;
	/*
	 * Whether to hide the list of filters.
	 */
	private boolean filtersHidden;
	/*
	 * Whether to hide the list of views.
	 */
	private boolean viewsHidden;
	/*
	 * A button to hide the list of filters.
	 */
	private SlickerButton hideFiltersButton;
	/*
	 * A button to show the list of filters.
	 */
	private SlickerButton showFiltersButton;
	/*
	 * A button to hide the list of views.
	 */
	private SlickerButton hideViewsButton;
	/*
	 * A button to show the list of views.
	 */
	private SlickerButton showViewsButton;
	/*
	 * A button to move the selected filter up in the list of filters.
	 */
	private SlickerButton upButton;
	/*
	 * A button to move the selected filter down in the list of filters.
	 */
	private SlickerButton downButton;
	/*
	 * A button to remove the selected filter from the list of filters.
	 */
	private SlickerButton xButton;
	/*
	 * A button to compute the cell.
	 */
	private SlickerButton doButton;
	/*
	 * A button to export the output log, if available.
	 */
	private SlickerButton exportButton;
	/*
	 * A button displaying the name of the computation cell.
	 */
	private JButton labelButton;
	/*
	 * A text field that allows the user to edit the name of the computation cell.
	 */
	private ProMTextField labelField;

	/*
	 * The last view selected by the user.
	 */
	private ViewType lastView;

	/*
	 * Coordinates for different widgets in the main table layout.
	 */
	private final int FILTER_TOP_NO_LOGS = 1;
	private final int FILTER_TOP_LOGS = 3;
	private final int FILTER_BOT = 7;
	private final int FILTER_LEFT = 0;
	private final int FILTER_RIGHT = 1;

	private final int VIEW_TOP = 1;
	private final int VIEW_BOT = 7;
	private final int VIEW_LEFT = 5;
	private final int VIEW_RIGHT = 6;

	private final int MAIN_TOP_NO_NAME = 1;
	private final int MAIN_TOP_NAME = 2;
	private final int MAIN_BOT = 7;
	private final int MAIN_LEFT_NO_FILTER = 1;
	private final int MAIN_LEFT_FILTER = 3;
	private final int MAIN_RIGHT_NO_VIEW = 5;
	private final int MAIN_RIGHT_VIEW = 3;

	private final int NAME_LEFT_NO_FILTER = 1;
	private final int NAME_LEFT_FILTER = 3;
	private final int NAME_RIGHT_NO_VIEW = 5;
	private final int NAME_RIGHT_VIEW = 3;
	private final int NAME_Y = 1;

	private final int LOG_TOP = 1;
	private final int LOG_BOT = 2;
	private final int LOG_LEFT = 0;
	private final int LOG_RIGHT = 1;

	private final int SHOW_FILTER_X = 0;
	private final int SHOW_FILTER_Y = 4;
	private final int HIDE_FILTER_X = 2;
	private final int HIDE_FILTER_Y = 4;

	private final int SHOW_VIEW_X = 6;
	private final int SHOW_VIEW_Y = 4;
	private final int HIDE_VIEW_X = 4;
	private final int HIDE_VIEW_Y = 4;

	private final int DO_LEFT = 0;
	private final int DO_RIGHT = 1;
	private final int DO_Y = 0;

	private final int EXPORT_LEFT = 5;
	private final int EXPORT_RIGHT = 6;
	private final int EXPORT_Y = 0;

	private final int LABEL_X = 3;
	private final int LABEL_Y = 0;

	/*
	 * Color showing that the input of the computation cell is available but the
	 * computation cell is not up-to-date.
	 */
	private final Color doColor = new Color(255, 165, 0);
	/*
	 * Color showing that the input of the computation cell is available and the
	 * computation cell is up-to-date.
	 */
	private final Color exportColor = new Color(240, 240, 240);
	/*
	 * Color showing that the input of the computation cell is not available.
	 */
	private final Color waitColor = new Color(30, 144, 255);

	/**
	 * Construct a new computation cell with given context, canceller, notebook,
	 * name, notebook input log, and output logs of computation cells preceding this
	 * cell.
	 * 
	 * @param context The given context
	 * @param canceller The given canceller
	 * @param notebook The given notebook
	 * @param name The give name
	 * @param notebookInputLog The input log of the notebook
	 * @param cellOutputLogs The list of output logs of preceding computation cells.
	 */
	public ComputationCell(UIPluginContext context, ProMCanceller canceller, Notebook notebook, String name,
			LogType notebookInputLog, List<LogType> cellOutputLogs) {
		super(context, notebook, name);
		this.canceller = canceller;
		// By default, the input log will be the input log of the notebook.
		inputLog = notebookInputLog;
		// Collect the possible input logs.
		inputLogs = new ArrayList<LogType>();
		inputLogs.add(notebookInputLog);
		inputLogs.addAll(cellOutputLogs);
		// Create the output log. As there are no filters yet, the output log is the input log.
		outputLog = new LogType(notebookInputLog.getLog(), "Output log of " + name);
		// Prepare the filtered logs.
		filteredLogs = new ArrayList<XLog>();
		filteredLogs.add(notebookInputLog.getLog());
		// Prepare the filters.
		filters = new ArrayList<Filter>();
		filters.add(new NewFilter(notebookInputLog.getLog(), new Parameters(), this));
		// Prepare the views.
		views = getViews();
		// No last view yet.
		lastView = null;
		// No widget yet, etc.
		widget = null;
		nameWidget = null;
		mainWidget = null;
		filtersHidden = false;
		viewsHidden = false;
		// Create the necessary buttons.
		hideFiltersButton = new SlickerButton("<");
		hideFiltersButton.addActionListener(this);
		showFiltersButton = new SlickerButton(">");
		showFiltersButton.addActionListener(this);
		hideViewsButton = new SlickerButton(">");
		hideViewsButton.addActionListener(this);
		showViewsButton = new SlickerButton("<");
		showViewsButton.addActionListener(this);
		upButton = new SlickerButton("\u0245");
		upButton.addActionListener(this);
		downButton = new SlickerButton("V");
		downButton.addActionListener(this);
		xButton = new SlickerButton("X");
		xButton.addActionListener(this);
		doButton = new SlickerButton("Recompute cell"); //"\u25b7");
		doButton.addActionListener(this);
		exportButton = new SlickerButton("Export filtered log");
		exportButton.addActionListener(this);
		// Reset all selections.
		selectedFilter = null;
		selectedView = null;
		selectedLog = null;
	}

	/**
	 * Gets the list of filters.
	 * @return The list of filters.
	 */
	public List<Filter> getFilters() {
		return filters;
	}

	/**
	 * Updates the list of input logs.
	 * @param cellOutputLogs The new list of preceding output logs
	 */
	public void setInputLogs(List<LogType> cellOutputLogs) {
		// Create the new list of input logs.
		LogType notebookInputLog = inputLogs.get(0);
		inputLogs.clear();
		inputLogs.add(notebookInputLog);
		inputLogs.addAll(cellOutputLogs);
		if (logList != null) {
			widget.remove(logList);
		}
		DefaultListModel<LogType> logListModel = new DefaultListModel<LogType>();
		for (LogType log : inputLogs) {
			logListModel.addElement(log);
		}
		logList = new ProMList<LogType>("Select a log", logListModel);
		logList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		logList.addListSelectionListener(this);
		if (this.inputLogs.contains(inputLog) || inputLog == notebookInputLog) {
			// The currently selected input log is sill available. Nothing to do.
		} else {
			// The currently selected input log is not available anymore. Select input log of notebook instead.
			logList.setSelection(notebookInputLog);
			setSelectedLog(notebookInputLog);
			// Reset output log, as input log has changed.
			outputLog.setLog(null);
			labelButton.setBackground(waitColor);
			if (outputLog.getLog() == null && inputLog.getLog() != null) {
				// We have an input log, but no output log: cell is not up-to-date.
				widget.add(doButton, DO_LEFT + ", " + DO_Y + ", " + DO_RIGHT + ", " + DO_Y);
				labelButton.setBackground(doColor);
			} else {
				// Cell is up-to-date: no need for the compute button.
				widget.remove(doButton);
			}
			if (outputLog.getLog() != null) {
				// We have an output log: cell is up-to-date.
				widget.add(exportButton, EXPORT_LEFT + ", " + EXPORT_Y + ", " + EXPORT_RIGHT + ", " + EXPORT_Y);
				labelButton.setBackground(exportColor);
			} else {
				// No output log: no need for the export output log button.
				widget.remove(exportButton);
			}
		}
		logList.setPreferredSize(new Dimension(100, 100));
		if (!filtersHidden) {
			// Update the list of filters.
			if (inputLogs.size() > 1) {
				// Only possible input log is input log of notebook. Remove the list to select an input log.
				widget.add(logList, LOG_LEFT + ", " + LOG_TOP + ", " + LOG_RIGHT + ", " + LOG_BOT);
				// Update the list of filters.
				widget.remove(filterList);
				widget.add(filterList, FILTER_LEFT + ", " + FILTER_TOP_LOGS + ", " + FILTER_RIGHT + ", " + FILTER_BOT);
			} else {
				// Update the list of input logs to choose from.
				if (logList != null) {
					widget.remove(logList);
					logList = null;
				}
				// Update the list of filters.
				widget.remove(filterList);
				widget.add(filterList,
						FILTER_LEFT + ", " + FILTER_TOP_NO_LOGS + ", " + FILTER_RIGHT + ", " + FILTER_BOT);
			}
		}
	}

	/**
	 * Update the output log in the background.
	 */
	public void updateOutputLog() {
		updateOutputLog(true);
	}

	/**
	 * Sets the input log. Needed when creating from template.
	 * @param inputLog The input log
	 */
	public void setInputLog(LogType inputLog) {
		this.inputLog = inputLog;
		setSelectedLog(this.inputLog);
		logList.setSelection(inputLog);
	}

	/**
	 * Sets the view. Needed when creating from template.
	 * @param view The view
	 */
	public void setLastView(ViewType view) {
		this.lastView = view;
		setSelectedView(this.lastView);
		viewList.setSelection(view);
	}

	/**
	 * Update the output log.
	 * @param doInBackground Whether to do the update in the background.
	 */
	public void updateOutputLog(boolean doInBackground) {
		if (inputLog.getLog() == null) {
			// No input log: Cannot update.
			outputLog.setLog(null);
			// Inform the notebook that this cell has been updated.
			getNotebook().updated(this);
			return;
		}
		// Prepare the list of filtered logs. There should be sufficiently many.
		while (filteredLogs.size() <= filters.size()) {
			filteredLogs.add(null);
		}
		// Remove the main widget.
		widget.remove(mainWidget);
		// Remove the name widget, if any.
		if (nameWidget != null) {
			widget.remove(nameWidget);
		}
		nameWidget = null;
		if (doInBackground) {
			// Update the output log in the background.
			JLabel label = new JLabel("<html><h3>Filtering log, please be patient...</h3></html>");
			label.setHorizontalAlignment(JLabel.CENTER);
			mainWidget = label;
			widget.add(mainWidget, (filtersHidden ? MAIN_LEFT_NO_FILTER : MAIN_LEFT_FILTER) + ", " + MAIN_TOP_NO_NAME
					+ ", " + (viewsHidden ? MAIN_RIGHT_NO_VIEW : MAIN_RIGHT_VIEW) + ", " + MAIN_BOT);
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				public Void doInBackground() {
					updateOutputLogDoInBackground();
					return null;
				}

				public void done() {
					// Create a new widget.
					updateOutputLogDone();
				}
			};
			worker.execute();
		} else {
			// Update the output log in the foreground.
			updateOutputLogDoInBackground();
			// Create a new widget.
			updateOutputLogDone();

		}
		widget.revalidate();
		widget.repaint();
	}

	/**
	 * Applies all filters in sequence.
	 */
	private void updateOutputLogDoInBackground() {
		int i;
		filteredLogs.set(0, inputLog.getLog());
		for (i = 0; i < filters.size(); i++) {
			filters.get(i).setLog(filteredLogs.get(i));
			filteredLogs.set(i + 1, filters.get(i).filter());
		}
		outputLog.setLog(filteredLogs.get(i));
	}

	/**
	 * Creates a new widget for the cell.
	 */
	private void updateOutputLogDone() {
		widget.remove(mainWidget);
		// Get a visualization from the last view.
		mainWidget = getVisualization(lastView);
		widget.add(mainWidget, (filtersHidden ? MAIN_LEFT_NO_FILTER : MAIN_LEFT_FILTER) + ", " + MAIN_TOP_NO_NAME + ", "
				+ (viewsHidden ? MAIN_RIGHT_NO_VIEW : MAIN_RIGHT_VIEW) + ", " + MAIN_BOT);
		labelButton.setBackground(waitColor);
		if (outputLog.getLog() == null && inputLog.getLog() != null) {
			widget.add(doButton, DO_LEFT + ", " + DO_Y + ", " + DO_RIGHT + ", " + DO_Y);
			labelButton.setBackground(doColor);
		} else {
			widget.remove(doButton);
		}
		if (outputLog.getLog() != null) {
			widget.add(exportButton, EXPORT_LEFT + ", " + EXPORT_Y + ", " + EXPORT_RIGHT + ", " + EXPORT_Y);
			labelButton.setBackground(exportColor);
		} else {
			widget.remove(exportButton);
		}
		// Notify the notebook that this cell has been updated.
		getNotebook().updated(this);
		// Reset the selected fitler.
		filterList.setSelection(new ArrayList<Filter>());
		setSelectedFilter(null);
		// Select the last selected view.
		viewList.setSelection(lastView);
		setSelectedView(lastView);
		
		widget.revalidate();
		widget.repaint();
	}

	/**
	 * Gets a widget for this cell. 
	 */
	public JComponent getWidget(boolean doReset) {
		if (doReset) {
			// Reset widget.
			widget = null;
		}
		if (widget != null) {
			// Reuse existing widget.
			return widget;
		}
		// Create new widget.
		if (inputLog.getLog() == null) {
			outputLog.setLog(null);
		}
		widget = new RoundedPanel();
		double size[][] = { { 30, 170, 30, TableLayoutConstants.FILL, 30, 170, 30 },
				{ 30, 30, TableLayoutConstants.FILL, TableLayoutConstants.FILL, 30, TableLayoutConstants.FILL,
						TableLayoutConstants.FILL, 30 } };
		widget.setLayout(new TableLayout(size));

		// Add the list of filters.
		DefaultListModel<Filter> filterListModel = new DefaultListModel<Filter>();
		for (Filter filter : filters) {
			filterListModel.addElement(filter);
		}
		filterList = new ProMList<Filter>("Select a filter", filterListModel);
		filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		filterList.addListSelectionListener(this);
		filterList.setPreferredSize(new Dimension(100, 100));
		if (filtersHidden) {
			widget.remove(hideFiltersButton);
			widget.add(showFiltersButton, SHOW_FILTER_X + ", " + SHOW_FILTER_Y);
		} else {
			widget.remove(showFiltersButton);
			widget.add(filterList, FILTER_LEFT + ", " + (inputLogs.size() > 1 ? FILTER_TOP_LOGS : FILTER_TOP_NO_LOGS)
					+ ", " + FILTER_RIGHT + ", " + FILTER_BOT);
			widget.add(hideFiltersButton, HIDE_FILTER_X + ", " + HIDE_FILTER_Y);
		}

		// Add the list of views.
		DefaultListModel<ViewType> viewListModel = new DefaultListModel<ViewType>();
		for (ViewType view : views) {
			viewListModel.addElement(view);
			if (lastView == null && view.getTypeName().startsWith("Explore Event Log")) {
				lastView = view;
			}
		}
		viewList = new ProMList<ViewType>("Select a view", viewListModel);
		viewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		viewList.addListSelectionListener(this);
		viewList.setSelection(lastView);
		// Show the last selected view.
		setSelectedView(lastView);
		viewList.setPreferredSize(new Dimension(100, 100));
		if (viewsHidden) {
			widget.remove(hideViewsButton);
			widget.add(showViewsButton, SHOW_VIEW_X + ", " + SHOW_VIEW_Y);
		} else {
			widget.remove(showViewsButton);
			widget.add(hideViewsButton, HIDE_VIEW_X + ", " + HIDE_VIEW_Y);
			widget.add(viewList, VIEW_LEFT + ", " + VIEW_TOP + ", " + VIEW_RIGHT + ", " + VIEW_BOT);
		}
		
		// Add the list of input logs.
		DefaultListModel<LogType> logListModel = new DefaultListModel<LogType>();
		for (LogType log : inputLogs) {
			logListModel.addElement(log);
		}
		logList = new ProMList<LogType>("Select a log", logListModel);
		logList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		logList.addListSelectionListener(this);
		logList.setSelection(inputLog);
		setSelectedLog(inputLog);
		logList.setPreferredSize(new Dimension(100, 100));
		if (!filtersHidden && inputLogs.size() > 1) {
			widget.add(logList, LOG_LEFT + ", " + LOG_TOP + ", " + LOG_RIGHT + ", " + LOG_BOT);
		}

		// Add the name of the cell.
		labelButton = new JButton("<html><h1>"
				+ getName().replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</h1></html>");
		labelButton.setBorderPainted(false);
		labelButton.setBackground(exportColor);
		labelButton.setHorizontalAlignment(JLabel.CENTER);
		labelButton.addActionListener(this);
		labelField = new ProMTextField(getName());
		labelField.getTextField().addActionListener(this);
		labelField.getTextField().setHorizontalAlignment(JLabel.CENTER);
		widget.add(labelButton, LABEL_X + ", " + LABEL_Y);
		labelButton.setBackground(waitColor);

		// Update cell status.
		if (inputLog.getLog() != null) {
			if (outputLog.getLog() == null) {
				widget.add(doButton, DO_LEFT + ", " + DO_Y + ", " + DO_RIGHT + ", " + DO_Y);
				labelButton.setBackground(doColor);
			} else {
				widget.add(exportButton, EXPORT_LEFT + ", " + EXPORT_Y + ", " + EXPORT_RIGHT + ", " + EXPORT_Y);
				labelButton.setBackground(exportColor);
			}
		} else {
			outputLog.setLog(null);
		}

		widget.setPreferredSize(new Dimension(600, 600));
		return widget;
	}

	private void listDoInBackground() {
	}

	/**
	 * Shows the configuration of the given filter in the main widget.
	 * @param filter The given filter
	 */
	private void listDone(Filter filter) {
		if (mainWidget != null) {
			widget.remove(mainWidget);
		}
		mainWidget = filter.getWidget();
		widget.add(mainWidget,
				(filtersHidden ? MAIN_LEFT_NO_FILTER : MAIN_LEFT_FILTER) + ", "
						+ (nameWidget != null ? MAIN_TOP_NAME : MAIN_TOP_NO_NAME) + ", "
						+ (viewsHidden ? MAIN_RIGHT_NO_VIEW : MAIN_RIGHT_VIEW) + ", " + MAIN_BOT);
		widget.revalidate();
		widget.repaint();
	}

	/**
	 * Shows the given component in the main widget.
	 * @param component
	 */
	public void setMainWidget(JComponent component) {
		if (mainWidget != null) {
			widget.remove(mainWidget);
		}
		mainWidget = component;
		widget.add(mainWidget,
				(filtersHidden ? MAIN_LEFT_NO_FILTER : MAIN_LEFT_FILTER) + ", "
						+ (nameWidget != null ? MAIN_TOP_NAME : MAIN_TOP_NO_NAME) + ", "
						+ (viewsHidden ? MAIN_RIGHT_NO_VIEW : MAIN_RIGHT_VIEW) + ", " + MAIN_BOT);
	}

	/**
	 * Handles changes.
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
		if (e.getSource() == filterList.getList()) {
			// Use has selected a filter.
			List<Filter> selected = filterList.getSelectedValuesList();
			if (selected.size() == 1) {
				// Reset view list selection.
				viewList.setSelection(new ArrayList<ViewType>());
				setSelectedView(null);
				// Remove the name widget.
				if (nameWidget != null) {
					widget.remove(nameWidget);
				}
				if (!(selected.get(0) instanceof NewFilter)) {
					// Show the name of the selected filter in the name widget.
					selected.get(0).update();
					nameWidget = new JPanel();
					double size[][] = { { 30, 30, 30, TableLayoutConstants.FILL, 90 }, { 30 } };
					nameWidget.setLayout(new TableLayout(size));
					nameWidget.add(upButton, "0, 0");
					nameWidget.add(downButton, "1, 0");
					nameWidget.add(xButton, "2, 0");
					JLabel label = new JLabel("<html><h2>" + selected.get(0).getName() + "</h2></html>");
					label.setHorizontalAlignment(JLabel.CENTER);
					nameWidget.add(label, "3, 0");
					widget.add(nameWidget, (filtersHidden ? NAME_LEFT_NO_FILTER : NAME_LEFT_FILTER) + ", " + NAME_Y
							+ ", " + (viewsHidden ? NAME_RIGHT_NO_VIEW : NAME_RIGHT_VIEW) + ", " + NAME_Y);
				}
				// Reset main widget.
				if (mainWidget != null) {
					widget.remove(mainWidget);
				}
				// Set temporary text in main widget.
				JLabel label = new JLabel("<html><h3>Scanning log, please be patient...</h3></html>");
				label.setHorizontalAlignment(JLabel.CENTER);
				mainWidget = label;
				widget.add(mainWidget,
						(filtersHidden ? MAIN_LEFT_NO_FILTER : MAIN_LEFT_FILTER) + ", "
								+ (nameWidget != null ? MAIN_TOP_NAME : MAIN_TOP_NO_NAME) + ", "
								+ (viewsHidden ? MAIN_RIGHT_NO_VIEW : MAIN_RIGHT_VIEW) + ", " + MAIN_BOT);
				// Set (in the background) the configuration of the selected filter in the main widget.
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					public Void doInBackground() {
						listDoInBackground();
						return null;
					}

					public void done() {
						listDone(selected.get(0));
					}
				};
				worker.execute();
				widget.revalidate();
				widget.repaint();
			}
		} else if (e.getSource() == viewList.getList()) {
			// User has selected a view
			List<ViewType> selected = viewList.getSelectedValuesList();
			if (selected.size() == 1) {
				// Reset selection in filter list.
				filterList.setSelection(new ArrayList<Filter>());
				setSelectedFilter(null);
				// Remove name widget.
				if (nameWidget != null) {
					widget.remove(nameWidget);
				}
				nameWidget = null;
				// Remove main widget.
				if (mainWidget != null) {
					widget.remove(mainWidget);
				}
				// Get widget for selected view, and set is as main widget.
				lastView = selected.get(0);
				mainWidget = getVisualization(lastView);
				widget.add(mainWidget,
						(filtersHidden ? MAIN_LEFT_NO_FILTER : MAIN_LEFT_FILTER) + ", " + MAIN_TOP_NO_NAME + ", "
								+ (viewsHidden ? MAIN_RIGHT_NO_VIEW : MAIN_RIGHT_VIEW) + ", " + MAIN_BOT);
				widget.revalidate();
				widget.repaint();
			}
		} else if (e.getSource() == logList.getList()) {
			// User has selected an input log.
			List<LogType> selected = logList.getSelectedValuesList();
			if (selected.size() == 1 && inputLog != selected.get(0)) {
				// Reset selection in filter list.
				filterList.setSelection(new ArrayList<Filter>());
				setSelectedFilter(null);
				// Regiser the selected input log.
				inputLog = selected.get(0);
				// For the time being, use this input log as input log for every filter.
				for (Filter filter : filters) {
					filter.setLog(inputLog.getLog());
				}
				// Reset the output log. If there are no filter, then the output log is the input log.
				outputLog.setLog(filters.size() == 1 ? inputLog.getLog() : null);
				// Remove the main widget.
				if (mainWidget != null) {
					widget.remove(mainWidget);
				}
				// Set the last view as the new main widget.
				mainWidget = getVisualization(lastView);
				widget.add(mainWidget,
						(filtersHidden ? MAIN_LEFT_NO_FILTER : MAIN_LEFT_FILTER) + ", " + MAIN_TOP_NO_NAME + ", "
								+ (viewsHidden ? MAIN_RIGHT_NO_VIEW : MAIN_RIGHT_VIEW) + ", " + MAIN_BOT);
				
				// Update cell status.
				labelButton.setBackground(waitColor);
				if (outputLog.getLog() == null && inputLog.getLog() != null) {
					widget.add(doButton, DO_LEFT + ", " + DO_Y + ", " + DO_RIGHT + ", " + DO_Y);
					labelButton.setBackground(doColor);
				} else {
					widget.remove(doButton);
				}
				if (outputLog.getLog() != null) {
					widget.add(exportButton, EXPORT_LEFT + ", " + EXPORT_Y + ", " + EXPORT_RIGHT + ", " + EXPORT_Y);
					labelButton.setBackground(exportColor);
				} else {
					widget.remove(exportButton);
				}
				
				// Notify the notebook that this cell has changed.
				
				getNotebook().updated(this);
				widget.revalidate();
				widget.repaint();
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == hideFiltersButton) {
			// Hide filter list (and log list).
			filtersHidden = true;
			widget.remove(hideFiltersButton);
			widget.remove(filterList);
			if (logList != null) {
				widget.remove(logList);
			}
			widget.add(showFiltersButton, SHOW_FILTER_X + ", " + SHOW_FILTER_Y);
			if (nameWidget != null) {
				widget.remove(nameWidget);
				widget.add(nameWidget, NAME_LEFT_NO_FILTER + ", " + NAME_Y + ", "
						+ (viewsHidden ? NAME_RIGHT_NO_VIEW : NAME_RIGHT_VIEW) + ", " + NAME_Y);
			}
			widget.remove(mainWidget);
			widget.add(mainWidget, MAIN_LEFT_NO_FILTER + ", " + (nameWidget != null ? MAIN_TOP_NAME : MAIN_TOP_NO_NAME)
					+ ", " + (viewsHidden ? MAIN_RIGHT_NO_VIEW : MAIN_RIGHT_VIEW) + ", " + MAIN_BOT);
			widget.revalidate();
			widget.repaint();
		} else if (e.getSource() == showFiltersButton) {
			// Show filter list (and log list).
			filtersHidden = false;
			widget.remove(showFiltersButton);
			widget.add(hideFiltersButton, HIDE_FILTER_X + ", " + HIDE_FILTER_Y);
			if (inputLogs.size() > 1) {
				widget.add(filterList, FILTER_LEFT + ", " + FILTER_TOP_LOGS + ", " + FILTER_RIGHT + ", " + FILTER_BOT);
				widget.add(logList, LOG_LEFT + ", " + LOG_TOP + ", " + LOG_RIGHT + ", " + LOG_BOT);
			} else {
				widget.add(filterList,
						FILTER_LEFT + ", " + FILTER_TOP_NO_LOGS + ", " + FILTER_RIGHT + ", " + FILTER_BOT);
			}
			if (nameWidget != null) {
				widget.remove(nameWidget);
				widget.add(nameWidget, NAME_LEFT_FILTER + ", " + NAME_Y + ", "
						+ (viewsHidden ? NAME_RIGHT_NO_VIEW : NAME_RIGHT_VIEW) + ", " + NAME_Y);
			}
			widget.remove(mainWidget);
			widget.add(mainWidget, MAIN_LEFT_FILTER + ", " + (nameWidget != null ? MAIN_TOP_NAME : MAIN_TOP_NO_NAME)
					+ ", " + (viewsHidden ? MAIN_RIGHT_NO_VIEW : MAIN_RIGHT_VIEW) + ", " + MAIN_BOT);
			widget.revalidate();
			widget.repaint();
		} else if (e.getSource() == hideViewsButton) {
			// Hide view list.
			viewsHidden = true;
			widget.remove(hideViewsButton);
			widget.remove(viewList);
			widget.add(showViewsButton, SHOW_VIEW_X + ", " + SHOW_VIEW_Y);
			if (nameWidget != null) {
				widget.remove(nameWidget);
				widget.add(nameWidget, (filtersHidden ? NAME_LEFT_NO_FILTER : NAME_LEFT_FILTER) + ", " + NAME_Y + ", "
						+ NAME_RIGHT_NO_VIEW + ", " + NAME_Y);
			}
			widget.remove(mainWidget);
			widget.add(mainWidget,
					(filtersHidden ? MAIN_LEFT_NO_FILTER : MAIN_LEFT_FILTER) + ", "
							+ (nameWidget != null ? MAIN_TOP_NAME : MAIN_TOP_NO_NAME) + ", " + MAIN_RIGHT_NO_VIEW + ", "
							+ MAIN_BOT);
			widget.revalidate();
			widget.repaint();
		} else if (e.getSource() == showViewsButton) {
			// Shows view list.
			viewsHidden = false;
			widget.remove(showViewsButton);
			widget.add(hideViewsButton, HIDE_VIEW_X + ", " + HIDE_VIEW_Y);
			widget.add(viewList, VIEW_LEFT + ", " + VIEW_TOP + ", " + VIEW_RIGHT + ", " + VIEW_BOT);
			if (nameWidget != null) {
				widget.remove(nameWidget);
				widget.add(nameWidget, (filtersHidden ? NAME_LEFT_NO_FILTER : NAME_LEFT_FILTER) + ", " + NAME_Y + ", "
						+ NAME_RIGHT_VIEW + ", " + NAME_Y);
			}
			widget.remove(mainWidget);
			widget.add(mainWidget,
					(filtersHidden ? MAIN_LEFT_NO_FILTER : MAIN_LEFT_FILTER) + ", "
							+ (nameWidget != null ? MAIN_TOP_NAME : MAIN_TOP_NO_NAME) + ", " + MAIN_RIGHT_VIEW + ", "
							+ MAIN_BOT);
			widget.revalidate();
			widget.repaint();
		} else if (e.getSource() == xButton || e.getSource() == upButton || e.getSource() == downButton) {
			// Remove a filter, move it up, or move it down.
			List<Filter> selected = filterList.getSelectedValuesList();
			int index = filters.indexOf(selected.get(0));
			if (e.getSource() == xButton || (e.getSource() == upButton && index > 0)
					|| (e.getSource() == downButton && index < filters.size() - 2)) {
				// Remove the filter.
				filters.remove(selected.get(0));
				if (e.getSource() == upButton) {
					// If up button, restore it at correct position.
					filters.add(index - 1, selected.get(0));
				} else if (e.getSource() == downButton) {
					// If down button, restore it at coreect position.
					filters.add(index + 1, selected.get(0));
				}
			}
			if (e.getSource() == xButton) {
				// Restore last view if filter is removed.
				viewList.setSelection(lastView);
				setSelectedView(lastView);
			}
			// Recreate list of filters.
			DefaultListModel<Filter> filterListModel = new DefaultListModel<Filter>();
			for (Filter filter : filters) {
				filterListModel.addElement(filter);
			}
			if (!filtersHidden) {
				widget.remove(filterList);
			}
			filterList = new ProMList<Filter>("Select a filter", filterListModel);
			filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			filterList.addListSelectionListener(this);
			filterList.setPreferredSize(new Dimension(100, 100));
			if (e.getSource() == xButton) {
				// Selected filter is removed, remove selection.
				filterList.setSelection(new ArrayList<Filter>());
				setSelectedFilter(null);
			} else {
				// Selected filter is moved up or down, reselect it.
				filterList.setSelection(selected.get(0));
			}
			if (!filtersHidden) {
				widget.add(filterList,
						FILTER_LEFT + ", " + (inputLogs.size() > 1 ? FILTER_TOP_LOGS : FILTER_TOP_NO_LOGS) + ", "
								+ FILTER_RIGHT + ", " + FILTER_BOT);
			}
			// Reset output log.
			outputLog.setLog(filters.size() == 1 ? inputLog.getLog() : null);
			// Remove main widget.
			widget.remove(mainWidget);
			if (e.getSource() == xButton) {
				// Filter is removed. Show last view.
				if (nameWidget != null) {
					widget.remove(nameWidget);
				}
				nameWidget = null;
				mainWidget = getVisualization(lastView);
			}
			widget.add(mainWidget,
					(filtersHidden ? MAIN_LEFT_NO_FILTER : MAIN_LEFT_FILTER) + ", "
							+ (nameWidget != null ? MAIN_TOP_NAME : MAIN_TOP_NO_NAME) + ", "
							+ (viewsHidden ? MAIN_RIGHT_NO_VIEW : MAIN_RIGHT_VIEW) + ", " + MAIN_BOT);
			
			// Update cell status.
			labelButton.setBackground(waitColor);
			if (outputLog.getLog() == null && inputLog.getLog() != null) {
				widget.add(doButton, DO_LEFT + ", " + DO_Y + ", " + DO_RIGHT + ", " + DO_Y);
				labelButton.setBackground(doColor);
			} else {
				widget.remove(doButton);
			}
			if (outputLog.getLog() != null) {
				widget.add(exportButton, EXPORT_LEFT + ", " + EXPORT_Y + ", " + EXPORT_RIGHT + ", " + EXPORT_Y);
				labelButton.setBackground(exportColor);
			} else {
				widget.remove(exportButton);
			}
			
			// Notify notebook that this cell has changed.
			getNotebook().updated(this);
			
			widget.revalidate();
			widget.repaint();
		} else if (e.getSource() == doButton) {
			// Recompute the output log.
			updateOutputLog();
		} else if (e.getSource() == exportButton) {
			// Export the output log into the workspace.
			exportOutputLog();
		} else if (e.getSource() == labelButton) {
			// Allow the user to edit the cell name.
			widget.remove(labelButton);
			widget.add(labelField, LABEL_X + ", " + LABEL_Y);
			widget.revalidate();
			widget.repaint();
		} else if (e.getSource() == labelField.getTextField()) {
			// Update the cell name.
			labelButton.setText("<html><h1>"
					+ labelField.getText().replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;")
					+ "</h1></html>");
			outputLog.setName("Output log of " + labelField.getText());
			widget.remove(labelField);
			widget.add(labelButton, LABEL_X + ", " + LABEL_Y);
			getNotebook().updated(this);
			widget.revalidate();
			widget.repaint();
		}
	}

	/**
	 * To be called when something in this cell has changed.
	 */
	public void updated() {
		// Reset the output log.
		outputLog.setLog(null);
		// Update cell status.
		if (outputLog.getLog() == null && inputLog.getLog() != null) {
			widget.add(doButton, DO_LEFT + ", " + DO_Y + ", " + DO_RIGHT + ", " + DO_Y);
			labelButton.setBackground(doColor);
		} else {
			widget.remove(doButton);
			labelButton.setBackground(waitColor);
		}
		// Remove export button (as output log has been reset).
		widget.remove(exportButton);
		// Notify notebook that this cell has changed.
		getNotebook().updated(this);
		
		widget.revalidate();
		widget.repaint();
	}

	/**
	 * Adds the selected filter to the list of filters.
	 * @param selectedFilter The selected filter
	 */
	public void add(Filter selectedFilter) {
		// If we have an output log, it will be the input log for the new filter, 
		// as the new filter will be added at the end.
		if (outputLog.getLog() != null) {
			selectedFilter.setLog(outputLog.getLog());
		}
		// Update filter parameters.
		selectedFilter.updateParameters();
		// Add the filter just before the last filter (which is always the "New filter" filter).
		filters.add(filters.size() - 1, selectedFilter);
		// Recreate the list of filters.
		DefaultListModel<Filter> filterListModel = new DefaultListModel<Filter>();
		for (Filter filter : filters) {
			filterListModel.addElement(filter);
		}
		if (!filtersHidden) {
			widget.remove(filterList);
		}
		filterList = new ProMList<Filter>("Select a filter", filterListModel);
		filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Select the selected filter.
		filterList.setSelection(selectedFilter);
		setSelectedFilter(selectedFilter);
		filterList.addListSelectionListener(this);
		filterList.setPreferredSize(new Dimension(100, 100));
		if (!filtersHidden) {
			widget.add(filterList, FILTER_LEFT + ", " + (inputLogs.size() > 1 ? FILTER_TOP_LOGS : FILTER_TOP_NO_LOGS)
					+ ", " + FILTER_RIGHT + ", " + FILTER_BOT);
		}
		
		// Put the configuration widget for the filter as the main widget.
		if (nameWidget != null) {
			widget.remove(nameWidget);
			nameWidget = null;
		}
		if (mainWidget != null) {
			widget.remove(mainWidget);
			mainWidget = null;
		}
		nameWidget = new JPanel();
		double size[][] = { { 30, 30, 30, TableLayoutConstants.FILL, 90 }, { 30 } };
		nameWidget.setLayout(new TableLayout(size));
		// Add buttons to remove or move filter.
		nameWidget.add(upButton, "0, 0");
		nameWidget.add(downButton, "1, 0");
		nameWidget.add(xButton, "2, 0");
		// Add name widget for this filter.
		JLabel label = new JLabel("<html><h2>" + selectedFilter.getName() + "</h2></html>");
		label.setHorizontalAlignment(JLabel.CENTER);
		nameWidget.add(label, "3, 0");
		widget.add(nameWidget, (filtersHidden ? NAME_LEFT_NO_FILTER : NAME_LEFT_FILTER) + ", " + NAME_Y + ", "
				+ (viewsHidden ? NAME_RIGHT_NO_VIEW : NAME_RIGHT_VIEW) + ", " + NAME_Y);
		// Get filter widget, and set it as main widget.
		mainWidget = selectedFilter.getWidget();
		widget.add(mainWidget,
				(filtersHidden ? MAIN_LEFT_NO_FILTER : MAIN_LEFT_FILTER) + ", "
						+ (nameWidget != null ? MAIN_TOP_NAME : MAIN_TOP_NO_NAME) + ", "
						+ (viewsHidden ? MAIN_RIGHT_NO_VIEW : MAIN_RIGHT_VIEW) + ", " + MAIN_BOT);
		// reset the output log.
		outputLog.setLog(null);
		// Update cell status.
		if (outputLog.getLog() == null && inputLog.getLog() != null) {
			widget.add(doButton, DO_LEFT + ", " + DO_Y + ", " + DO_RIGHT + ", " + DO_Y);
			labelButton.setBackground(doColor);
		} else {
			widget.remove(doButton);
			labelButton.setBackground(waitColor);
		}
		// Remove export button (as output log has been reset).
		widget.remove(exportButton);
		
		// Notify notebook that this cell ahs changed.
		getNotebook().updated(this);
		
		widget.revalidate();
		widget.repaint();
	}

	/**
	 * Gets the output log.
	 * @return The output log.
	 */
	public LogType getOutputLog() {
		return outputLog;
	}

	/**
	 * Gets the input log.
	 * @return The input log.
	 */
	public LogType getInputLog() {
		return inputLog;
	}

	/**
	 * Gets a list of possible views.
	 * @return A list of possible views.
	 */
	public List<ViewType> getViews() {
		List<ViewType> views = new ArrayList<ViewType>();
		UIPluginContext context = getContext();
		// Get the necessary managers
		ProMViewManager vm = ProMViewManager.initialize(context.getGlobalContext()); // Get current view manager
		ProMResourceManager rm = ProMResourceManager.initialize(context.getGlobalContext()); // Get current resource manager
		// Get the possible visualizers for the input event log.
		List<ViewType> logViewTypes = vm.getViewTypes(rm.getResourceForInstance(this.inputLogs.get(0).getLog()));
		// Add all visualizer (except this one).
		for (ViewType type : logViewTypes) {
			// Exclude a view with "Notebook" in its name to avoid stacking noteboiok views.
			if (!type.getTypeName().contains("Notebook")) {
				views.add(type);
			}
		}
		return views;
	}

	/**
	 * Gets a widget for the given view.
	 * @param type The given view
	 * @return TA widget for the given view.
	 */
	public JComponent getVisualization(ViewType type) {
		if (inputLog.getLog() == null) {
			// No input log. Cannot view a non-existing log.
			JLabel label = new JLabel("<html><h3>The selected input log is not yet available...</h3></html>");
			label.setHorizontalAlignment(JLabel.CENTER);
			return label;
		}
		UIPluginContext context = getContext();
		// Get all log visualizers.
		Set<Pair<Integer, PluginParameterBinding>> logVisualizers = PluginManagerImpl.getInstance().find(
				Visualizer.class, JComponent.class, context.getPluginContextType(), true, false, false,
				inputLog.getLog().getClass());
		logVisualizers.addAll(
				PluginManagerImpl.getInstance().find(Visualizer.class, JComponent.class, context.getPluginContextType(),
						true, false, false, inputLog.getLog().getClass(), canceller.getClass()));
		// Check all log visualizers for the visualizer name.
		for (Pair<Integer, PluginParameterBinding> logVisualizer : logVisualizers) {
			// Get the visualizer name.
			String visualizerName = logVisualizer.getSecond().getPlugin().getAnnotation(Visualizer.class).name();
			if (visualizerName.equals(UITopiaVariant.USEPLUGIN)) {
				// Visualizer name is the plug-in name.
				visualizerName = logVisualizer.getSecond().getPlugin().getAnnotation(Plugin.class).name();
			}
			// Remove @XYZ<Space> from the beginning.
			if (visualizerName.startsWith("@") && visualizerName.contains(" ")) {
				visualizerName = visualizerName.substring(visualizerName.indexOf(" ") + 1);
			}
			// Check whether name matches.
			if (visualizerName.equals(type.getTypeName())) {
				// Name matches, invoke this log visualizer and return the correct result.
				try {
					// Get plug-in name.
					String pluginName = logVisualizer.getSecond().getPlugin().getAnnotation(Plugin.class).name();
					// Invoke the plug-in, depending on whether or not it takes a canceller.
					List<Class<?>> parameterTypes = logVisualizer.getSecond().getPlugin()
							.getParameterTypes(logVisualizer.getSecond().getMethodIndex());
					// Set the log to visualize. By default, visualize the output log.
					XLog log = outputLog.getLog();
					if (log == null) {
						// No output log. Visualize input log instead.
						log = inputLog.getLog();
					}
					if (parameterTypes.size() == 2 && parameterTypes.get(1) == ProMCanceller.class) {
						// Let the user know which plug-in we're invoking. This name should match the name of the plug-in.
						System.out.println(
								"[Visualizerd] Calling plug-in " + pluginName + " w/ canceller to visualize log");
						return context.tryToFindOrConstructFirstNamedObject(JComponent.class, pluginName, null, null,
								log, new ProMCanceller() {
									public boolean isCancelled() {
										// TODO Auto-generated method stub
										return false;
									}
								});
					} else {
						// Let the user know which plug-in we're invoking. This name should match the name of the plug-in.
						System.out.println(
								"[Visualizerd] Calling plug-in " + pluginName + " w/o canceller to visualize log");
						return context.tryToFindOrConstructFirstNamedObject(JComponent.class, pluginName, null, null,
								log);
					}
				} catch (Exception ex) {
					// Message if visualizer fails for whatever reason.
					return new JLabel("Visualizer " + type.getTypeName() + " failed: " + ex.getMessage());
				}
			}
		}
		// If the visualizer could not be found, show some text.
		JLabel label = new JLabel("<html></h3>Visualizer " + type.getTypeName() + " could not be found...</h3></html>");
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}

	/**
	 * Exports output log into the workspace.
	 */
	public void exportOutputLog() {
		// Clone the output log to have a fresh log to export into the workspace.
		XLog output = (XLog) outputLog.getLog().clone();
		String name = outputLog.getName();

		// Export the cloned log into the workspace, and make it favorite.
		getContext().getProvidedObjectManager().createProvidedObject(name, output, XLog.class, getContext());
		getContext().getGlobalContext().getResourceManager().getResourceForInstance(output).setFavorite(true);
	}

	/**
	 * Updates the cell.
	 */
	public void update() {
		// Update all filters.
		for (Filter filter : filters) {
			filter.setLog(inputLog.getLog());
			filter.update();
		}
	}

	/**
	 * Gets the selected filter.
	 * @return The selected filter.
	 */
	public Filter getSelectedFilter() {
		return selectedFilter;
	}

	/**
	 * Sets the selected filter to the given filter.
	 * @param selectedFilter The given filter
	 */
	public void setSelectedFilter(Filter selectedFilter) {
		this.selectedFilter = selectedFilter;
	}

	/**
	 * Gets the selected view.
	 * @return The selected view
	 */
	public ViewType getSelectedView() {
		return selectedView;
	}

	/**
	 * Sets the selected view to the given view.
	 * @param selectedView the given view
	 */
	public void setSelectedView(ViewType selectedView) {
		this.selectedView = selectedView;
	}

	/**
	 * Gets the selected log.
	 * @return The selected log
	 */
	public LogType getSelectedLog() {
		return selectedLog;
	}

	/**
	 * Sets the selected log to the given log.
	 * @param selectedLog The given log.
	 */
	public void setSelectedLog(LogType selectedLog) {
		this.selectedLog = selectedLog;
	}

	/**
	 * Gets a template for the cell.
	 */
	public CellTemplate getTemplate() {
		// Create new template.
		ComputationCellTemplate computationCellTemplate = new ComputationCellTemplate();
		// Copy name.
		computationCellTemplate.setName(getName());
		// Copy last view.
		computationCellTemplate.setView(lastView.getTypeName());
		// Copy input log.
		computationCellTemplate.setInput(inputLog.getName());
		// Add filter template for every filter (except New filter).
		computationCellTemplate.setFilterTemplates(new ArrayList<FilterTemplate>());
		for (Filter filter : filters) {
			if (filter instanceof NewFilter) {
				// ignore
			} else {
				computationCellTemplate.getFilterTemplates().add(filter.getTemplate());
			}
		}
		// Return template.
		return computationCellTemplate;
	}

}
