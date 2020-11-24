/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.platform.fabric.impl;

import com.google.gson.JsonObject;
import net.kyori.adventure.platform.fabric.ComponentArgumentType;
import net.kyori.adventure.platform.fabric.impl.accessor.ComponentSerializerAccess;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ComponentArgumentTypeSerializer implements ArgumentSerializer<ComponentArgumentType> {

  private static final ResourceLocation SERIALIZER_GSON = new ResourceLocation("adventure", "gson");

  @Override
  public void serializeToNetwork(final ComponentArgumentType type, final FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(SERIALIZER_GSON);
  }

  @Override
  public ComponentArgumentType deserializeFromNetwork(final FriendlyByteBuf buffer) {
    buffer.readResourceLocation(); // TODO: Serializer type
    return ComponentArgumentType.component();
  }

  @Override
  public void serializeToJson(final ComponentArgumentType type, final JsonObject json) {
    json.add("serializer", ComponentSerializerAccess.getGSON().toJsonTree(SERIALIZER_GSON));
  }
}
