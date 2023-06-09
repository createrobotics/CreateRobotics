package com.workert.robotics.base.roboscript.ingame;
import java.util.Map;

public interface VariableDataExternalSavingProvider {
	void saveVariableExternally(Map.Entry<String, Object> variableMap);

	Map<String, Object> getExternallySavedVariables();
}
