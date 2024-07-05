package de.quantum.core.module;

import java.util.ArrayList;

public record ModuleRecord(String moduleName, String moduleDescription, String moduleVersion, String moduleAuthorName,
                           String moduleAuthorID, ArrayList<String> eventList, ArrayList<String> commandList) {

    // Additional constructor with default empty lists for eventList and commandList
    public ModuleRecord(String moduleName, String moduleDescription, String moduleVersion, String moduleAuthorName, String moduleAuthorID) {
        this(moduleName, moduleDescription, moduleVersion, moduleAuthorName, moduleAuthorID, new ArrayList<>(), new ArrayList<>());
    }

    public void addEvent(String eventName) {
        eventList.add(eventName);
    }

    public void addCommand(String commandName) {
        commandList.add(commandName);
    }

}
