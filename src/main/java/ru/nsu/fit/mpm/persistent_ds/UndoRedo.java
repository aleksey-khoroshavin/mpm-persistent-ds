package ru.nsu.fit.mpm.persistent_ds;

public interface UndoRedo {
    /**
     * Откат к предыдущей версии
     */
    void undo();

    /**
     * Повтор операции изменения
     */
    void redo();
}
