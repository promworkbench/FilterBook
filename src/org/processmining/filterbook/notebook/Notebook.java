package org.processmining.filterbook.notebook;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.filterbook.cells.Cell;
import org.processmining.filterbook.cells.CellTemplate;
import org.processmining.filterbook.cells.ComputationCell;
import org.processmining.filterbook.cells.TextCell;
import org.processmining.filterbook.types.LogType;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.util.ui.widgets.ProMScrollPane;

import com.fluxicon.slickerbox.components.SlickerButton;

public class Notebook implements ActionListener {

	private UIPluginContext context;
	private ProMCanceller canceller;

	private LogType inputLog;
	private LogType outputLog;

	private JComponent widget;
	private JComponent scrollPane;
	private boolean addComputationCell = false;

	private List<Cell> cells;
	private List<SlickerButton> doButtons;
	private List<SlickerButton> exportButtons;
	private List<SlickerButton> addComputationCellButtons;
	private List<SlickerButton> addTextCellButtons;
	private List<SlickerButton> swapCellButtons;
	private List<SlickerButton> removeCellButtons;

	private final String DO_LABEL = "Recompute notebook";
	private final String EXPORT_LABEL = "Create notebook template";
	private final String ADD_COMP_LABEL = "Add computation cell here";
	private final String ADD_TEXT_LABEL = "Add text cell here";
	private final String SWAP_LABEL = "Swap cells";
	private final String REM_LABEL = "Remove cell below";

	public Notebook(UIPluginContext context, ProMCanceller canceller, XLog log) {
		this(context, canceller, log, false);
	}
	
	public Notebook(UIPluginContext context, ProMCanceller canceller, XLog log, boolean addComputationCell) {
		this.context = context;
		this.canceller = canceller;
		this.addComputationCell = addComputationCell;
		inputLog = new LogType(log, "Input log of notebook");
		outputLog = new LogType(log, "Output log of notebook");
		cells = new ArrayList<Cell>();
		doButtons = new ArrayList<SlickerButton>();
		exportButtons = new ArrayList<SlickerButton>();
		addComputationCellButtons = new ArrayList<SlickerButton>();
		addTextCellButtons = new ArrayList<SlickerButton>();
		swapCellButtons = new ArrayList<SlickerButton>();
		removeCellButtons = new ArrayList<SlickerButton>();
		widget = new JPanel();
		scrollPane = null;
	}

	public LogType getInputLog() {
		return inputLog;
	}

	public List<LogType> getOutputLogs() {
		List<LogType> outputLogs = new ArrayList<LogType>();
		for (Cell cell : cells) {
			if (cell instanceof ComputationCell) {
				outputLogs.add(((ComputationCell) cell).getOutputLog());
			}
		}
		return outputLogs;
	}

	public JComponent getWidget() {
		if (scrollPane != null) {
			return scrollPane;
		}
		widget = new JPanel();
		if (addComputationCell) {
			addComputationCell = false;
			/*
			 * Add an initial computation cell to prevent the user form looking at an almost
			 * blank screen.
			 */
			addCell(0, new ComputationCell(context, canceller, this, getLabel(), inputLog, new ArrayList<LogType>()));
		}
		widget.setLayout(new BoxLayout(widget, BoxLayout.PAGE_AXIS));
		addComputationCellButtons.clear();
		addTextCellButtons.clear();
		swapCellButtons.clear();
		removeCellButtons.clear();
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		SlickerButton button = new SlickerButton(DO_LABEL);
		button.addActionListener(this);
		doButtons.add(button);
		buttonPanel.add(button);
		button = new SlickerButton(EXPORT_LABEL);
		button.addActionListener(this);
		exportButtons.add(button);
		buttonPanel.add(button);
		button = new SlickerButton(ADD_COMP_LABEL);
		button.addActionListener(this);
		addComputationCellButtons.add(button);
		buttonPanel.add(button);
		button = new SlickerButton(ADD_TEXT_LABEL);
		button.addActionListener(this);
		addTextCellButtons.add(button);
		buttonPanel.add(button);
		button = new SlickerButton(SWAP_LABEL);
		button.addActionListener(this);
		swapCellButtons.add(button);
		button = new SlickerButton(REM_LABEL);
		button.addActionListener(this);
		removeCellButtons.add(button);
		if (cells.size() > 0) {
			buttonPanel.add(button);
		}
		widget.add(buttonPanel);
		int i = 0;
		for (Cell cell : cells) {
			widget.add(cell.getWidget(true));
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
			button = new SlickerButton(DO_LABEL);
			button.addActionListener(this);
			doButtons.add(button);
			buttonPanel.add(button);
			button = new SlickerButton(EXPORT_LABEL);
			button.addActionListener(this);
			exportButtons.add(button);
			buttonPanel.add(button);
			button = new SlickerButton(ADD_COMP_LABEL);
			button.addActionListener(this);
			addComputationCellButtons.add(button);
			buttonPanel.add(button);
			button = new SlickerButton(ADD_TEXT_LABEL);
			button.addActionListener(this);
			addTextCellButtons.add(button);
			buttonPanel.add(button);
			if (i < cells.size() - 1) {
				button = new SlickerButton(SWAP_LABEL);
				button.addActionListener(this);
				swapCellButtons.add(button);
				buttonPanel.add(button);
				button = new SlickerButton(REM_LABEL);
				button.addActionListener(this);
				removeCellButtons.add(button);
				buttonPanel.add(button);
			}
			widget.add(buttonPanel);
			if (cell instanceof ComputationCell) {
				outputLog = ((ComputationCell) cell).getOutputLog();
			}
			i++;
		}
		scrollPane = new ProMScrollPane(widget);
		return scrollPane;
	}

	public void updated(Cell cell) {
		boolean doUpdate = false;
		for (int i = 0; i < cells.size(); i++) {
			if (doUpdate) {
				if (2 * i + 1 < widget.getComponentCount()) {
					widget.remove(2 * i + 1);
					cells.get(i).update();
					widget.add(cells.get(i).getWidget(true), 2 * i + 1);
				}
			} else if (cells.get(i) == cell) {
				doUpdate = true;
			}
		}
	}

	private String getLabel() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmmssSSS");
		return "Cell " + dtf.format(LocalDateTime.now());
	}

	public void actionPerformed(ActionEvent e) {
		int i = addComputationCellButtons.indexOf(e.getSource());
		if (i > -1) {
			addCell(i, new ComputationCell(context, canceller, this, getLabel(), inputLog, new ArrayList<LogType>()));
			updateWidget();
			return;
		}
		i = addTextCellButtons.indexOf(e.getSource());
		if (i > -1) {
			addCell(i, new TextCell(context, this, getLabel()));
			updateWidget();
			return;
		}
		i = swapCellButtons.indexOf(e.getSource());
		if (i > -1) {
			cells.add(i, cells.remove(i - 1));
			updateWidget();
			return;
		}
		i = removeCellButtons.indexOf(e.getSource());
		if (i > -1) {
			removeCell(i);
			updateWidget();
			return;
		}
		i = exportButtons.indexOf(e.getSource());
		if (i > -1) {
			NotebookTemplate template = getTemplate();
			context.getProvidedObjectManager().createProvidedObject("Exported notebook", template,
					NotebookTemplate.class, context);
			context.getGlobalContext().getResourceManager().getResourceForInstance(template).setFavorite(true);
			return;
		}
		i = doButtons.indexOf(e.getSource());
		if (i > -1) {
			/*
			 * Create a private list with all computation cells. This avoids that, say,
			 * swapping cells, leads to issues with iterating over all cells.
			 */
			List<ComputationCell> computationCells = new ArrayList<ComputationCell>();
			for (Cell cell : cells) {
				if (cell instanceof ComputationCell) {
					computationCells.add((ComputationCell) cell);
				}
			}
			/*
			 * Update all computation cells in the specified order in the background.
			 */
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				public Void doInBackground() {
					computeAllDoInBackground(computationCells);
					return null;
				}

				public void done() {
					computeAllDone();
				}
			};
			worker.execute();
			widget.revalidate();
			widget.repaint();
			return;
		}
	}

	private void computeAllDoInBackground(List<ComputationCell> computationCells) {
		/*
		 * Update all computation cells in the specified order.
		 */
		for (ComputationCell computationCell : computationCells) {
			computationCell.updateOutputLog(false);
		}
	}

	private void computeAllDone() {
		widget.revalidate();
		widget.repaint();
	}

	private void addCell(int i, Cell cell) {
		SlickerButton button = new SlickerButton(DO_LABEL);
		button.addActionListener(this);
		doButtons.add(i, button);
		button = new SlickerButton(EXPORT_LABEL);
		button.addActionListener(this);
		exportButtons.add(i, button);
		button = new SlickerButton(ADD_COMP_LABEL);
		button.addActionListener(this);
		addComputationCellButtons.add(i, button);
		button = new SlickerButton(ADD_TEXT_LABEL);
		button.addActionListener(this);
		addTextCellButtons.add(i, button);
		button = new SlickerButton(SWAP_LABEL);
		button.addActionListener(this);
		swapCellButtons.add(i, button);
		button = new SlickerButton(REM_LABEL);
		button.addActionListener(this);
		removeCellButtons.add(i, button);
		cells.add(i, cell);
	}

	private void removeCell(int i) {
		cells.remove(i);
		doButtons.remove(i);
		exportButtons.remove(i);
		addComputationCellButtons.remove(i);
		addTextCellButtons.remove(i);
		swapCellButtons.remove(i);
		removeCellButtons.remove(i);
	}

	private void updateWidget() {
		widget.removeAll();
		List<LogType> outputLogs = new ArrayList<LogType>();
		int i = 0;
		for (Cell cell : cells) {
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
			buttonPanel.add(doButtons.get(i));
			buttonPanel.add(exportButtons.get(i));
			buttonPanel.add(addComputationCellButtons.get(i));
			buttonPanel.add(addTextCellButtons.get(i));
			if (i > 0) {
				buttonPanel.add(swapCellButtons.get(i));
			}
			buttonPanel.add(removeCellButtons.get(i));
			widget.add(buttonPanel);
			widget.add(cell.getWidget(false));
			if (cell instanceof ComputationCell) {
				ComputationCell computationCell = (ComputationCell) cell;
				computationCell.setInputLogs(outputLogs);
				outputLogs.add(((ComputationCell) cell).getOutputLog());
			}
			if (cell instanceof ComputationCell) {
				outputLog = ((ComputationCell) cell).getOutputLog();
			}
			i++;
		}
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(doButtons.get(i));
		buttonPanel.add(exportButtons.get(i));
		buttonPanel.add(addComputationCellButtons.get(i));
		buttonPanel.add(addTextCellButtons.get(i));
		widget.add(buttonPanel);
		widget.revalidate();
		widget.repaint();
	}

	public NotebookTemplate getTemplate() {
		NotebookTemplate notebookTemplate = new NotebookTemplate();
		notebookTemplate.setCellTemplates(new ArrayList<CellTemplate>());
		for (Cell cell : cells) {
			notebookTemplate.getCellTemplates().add(cell.getTemplate());
		}
		return notebookTemplate;
	}

	public void populate(UIPluginContext context, ProMCanceller canceller, NotebookTemplate notebookTemplate,
			XLog log) {
		int i = 0;
		for (CellTemplate cellTemplate : notebookTemplate.getCellTemplates()) {
			addCell(i, cellTemplate.createCell(context, canceller, this, log));
			i++;
		}
	}
}
