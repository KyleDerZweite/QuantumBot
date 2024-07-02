package de.quantum.core.module;

public class ModuleManager {

    private static volatile ModuleManager INSTANCE = null;

    private static final long ALLOWED_PERMISSION_OFFSET = 590435596631761L;

    private ModuleManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + ModuleManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
    }

    public static ModuleManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ModuleManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ModuleManager();
                }
            }
        }
        return INSTANCE;
    }
}