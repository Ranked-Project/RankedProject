package net.rankedproject.spigot.npc.model;

import net.rankedproject.spigot.npc.Npc;

/**
 * Represents the base model for all NPCs.
 * <p>
 * This interface serves as a common parent for all NPC model types,
 * providing a shared type for NPC-related operations in the system.
 * </p>
 *
 * <p>Concrete implementations define the specific characteristics and
 * behavior of different NPC types, such as player-like NPCs or custom entities.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * NpcModel npcModel = ...;
 * if (npcModel instanceof PlayerNpcModel playerModel) {
 *     String texture = playerModel.getTexture();
 * }
 * }</pre>
 */
public interface NpcModel {

}