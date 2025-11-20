package net.rankedproject.spigot.npc.model.impl;

import net.rankedproject.spigot.npc.model.NpcModel;

/**
 * Represents a player-like NPC model containing skin data.
 * <p>
 * Extends {@link NpcModel} to provide additional properties specific
 * to player-based NPCs, such as Minecraft skin texture and signature.
 * </p>
 *
 * <p>Implementations of this interface are used for NPCs that visually
 * mimic a Minecraft player.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * PlayerNpcModel npcModel = ...;
 * String texture = npcModel.getTexture();
 * String signature = npcModel.getSignature();
 * }</pre>
 */
public interface PlayerNpcModel extends NpcModel {

    /**
     * Returns the Base64-encoded texture string for this player NPC.
     *
     * @return the texture string representing the NPC skin
     */
    String getTexture();

    /**
     * Returns the signature of the texture property, used for verification
     * by Minecraft clients.
     *
     * @return the signature corresponding to the texture
     */
    String getSignature();
}
