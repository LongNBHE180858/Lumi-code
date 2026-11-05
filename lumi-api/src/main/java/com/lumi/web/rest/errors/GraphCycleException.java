package com.lumi.web.rest.errors;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when a circular prerequisite relationship is detected in the Knowledge Graph.
 */
public class GraphCycleException extends BadRequestAlertException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String cyclePath;
    private final List<Long> cycleNodeIds;

    public GraphCycleException(String defaultMessage, String cyclePath, List<Long> cycleNodeIds) {
        super(ErrorConstants.DEFAULT_TYPE, defaultMessage, "knowledgeGraph", "cycleDetected");
        this.cyclePath = cyclePath;
        this.cycleNodeIds = cycleNodeIds != null ? Collections.unmodifiableList(new ArrayList<>(cycleNodeIds)) : Collections.emptyList();
        getProblemDetailWithCause().setProperty("cyclePath", cyclePath);
        getProblemDetailWithCause().setProperty("cycleNodeIds", this.cycleNodeIds);
    }

    public String getCyclePath() {
        return cyclePath;
    }

    public List<Long> getCycleNodeIds() {
        return cycleNodeIds;
    }
}
