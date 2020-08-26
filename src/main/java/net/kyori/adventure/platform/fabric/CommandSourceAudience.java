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

package net.kyori.adventure.platform.fabric;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import org.checkerframework.checker.nullness.qual.NonNull;

import static java.util.Objects.requireNonNull;

/**
 * Audience implementation that can wrap a CommandOutput
 */
public final class CommandSourceAudience implements Audience {
  private final CommandSource output;

  CommandSourceAudience(final CommandSource output) {
    this.output = output;
  }

  public static Audience of(final CommandSource output) {
    if(output instanceof Audience) {
      return (Audience) output;
    } else {
      return new CommandSourceAudience(requireNonNull(output, "output"));
    }
  }

  @Override
  public void sendMessage(final Component text) {
    this.output.sendMessage(FabricAudienceProvider.adapt(text), Util.NIL_UUID);
  }

  @Override
  public void sendActionBar(final @NonNull Component message) {
    this.sendMessage(message);
  }
}
