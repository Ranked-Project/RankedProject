package net.rankedproject.spigot.npc.model.impl;

import net.rankedproject.spigot.npc.model.NpcModel;

/**
 * Represents an NPC model based on a custom entity.
 * <p>
 * This interface extends {@link NpcModel} and adds support for resource pack-based entity models.
 * The {@link #getModelId()} corresponds to the unique ID of the model within the resource pack,
 * which is used to spawn the correct entity appearance in-game.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * EntityNpcModel entityModel = ...;
 * int modelId = entityModel.getModelId();
 * // Use modelId to spawn entity with model from the resource pack
 * }</pre>
 */
public interface EntityNpcModel extends NpcModel {

    /**
     * Returns the unique ID of the entity model in the resource pack.
     *
     * @return the model ID used to identify the custom entity
     */
    int getModelId();
}
