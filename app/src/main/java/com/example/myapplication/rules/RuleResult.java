package com.example.myapplication.rules;
import java.util.*;
/**
 * Result of rule evaluation.
 * Used both offline and online.
 */
public class RuleResult {

    private String ruleVersion = "v2.0";

    private List<Decision> decisions = new ArrayList<>();

    // 🔑 KEY CHANGE
    private Map<ActionCategory, List<String>> actions =
            new EnumMap<>(ActionCategory.class);

    private Map<AlertType, List<String>> alerts =
            new EnumMap<>(AlertType.class);

    /* ---------------- ADD METHODS ---------------- */

    public void addDecision(Decision decision) {
        if (!decisions.contains(decision)) {
            decisions.add(decision);
        }
    }

    public void addAction(ActionCategory category, String message) {
        actions.computeIfAbsent(category, k -> new ArrayList<>())
                .add(message);
    }

    public void addAlert(AlertType type, String message) {
        alerts.computeIfAbsent(type, k -> new ArrayList<>())
                .add(message);
    }

    /* ---------------- GETTERS ---------------- */

    public List<Decision> getDecisions() {
        return decisions;
    }

    public List<String> getActions(ActionCategory category) {
        return actions.getOrDefault(category, Collections.emptyList());
    }

    public Map<ActionCategory, List<String>> getAllActions() {
        return actions;
    }

    public Map<AlertType, List<String>> getAlerts() {
        return alerts;
    }

    public String getRuleVersion() {
        return ruleVersion;
    }
}
