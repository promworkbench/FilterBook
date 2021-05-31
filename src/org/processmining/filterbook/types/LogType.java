package org.processmining.filterbook.types;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;

public class LogType {

	/*
	 * Wrapper class for event logs.
	 * 
	 * The input log of a computation cell refers to a LogType. This allows us to
	 * update the log without having to change the input log.
	 */

	/**
	 * The wrapped event log. May be null.
	 */
	private XLog log;
	/**
	 * The name of the wrapped event log.
	 */
	private String name;

	public LogType(XLog log) {
		this.log = log;
		this.name = null;
	}

	public LogType(XLog log, String name) {
		this.log = log;
		this.name = name;
	}

	public String toString() {
		if (name != null) {
			return name;
		}
		String label = XConceptExtension.instance().extractName(log);
		if (label != null) {
			return label;
		}
		return "Log containing " + log.size() + " traces";
	}

	public XLog getLog() {
		return log;
	}

	public void setLog(XLog log) {
		this.log = log;
	}

	public String getName() {
		if (name != null) {
			return name;
		}
		String label = XConceptExtension.instance().extractName(log);
		if (label != null) {
			return label;
		}
		return "Log containing " + log.size() + " traces";
	}

	public void setName(String name) {
		this.name = name;
	}
}
