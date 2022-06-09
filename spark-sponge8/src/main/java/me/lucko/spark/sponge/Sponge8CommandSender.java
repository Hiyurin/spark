/*
 * This file is part of spark.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lucko.spark.sponge;

import me.lucko.spark.common.command.sender.AbstractCommandSender;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Identifiable;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Sponge8CommandSender extends AbstractCommandSender<Subject> {
    private final CommandCause cause;
    private final Audience audience;

    public Sponge8CommandSender(CommandCause cause) {
        super(cause);
        this.cause = cause;
        this.audience = cause.audience();
    }

    public <T extends Subject & Audience> Sponge8CommandSender(T cause) {
        super(cause);
        this.cause = null;
        this.audience = cause;
    }

    @Override
    public String getName() {
        return super.delegate.friendlyIdentifier().orElse(super.delegate.identifier());
    }

    @Override
    public UUID getUniqueId() {
        if (this.cause != null) {
            Identifiable identifiable = this.cause.first(Identifiable.class).orElse(null);
            if (identifiable != null) {
                return identifiable.uniqueId();
            }
        }

        try {
            return UUID.fromString(super.delegate.identifier());
        } catch (Exception e) {
            return UUID.nameUUIDFromBytes(super.delegate.identifier().getBytes(UTF_8));
        }
    }

    @Override
    public void sendMessage(Component message) {
        this.audience.sendMessage(Identity.nil(), message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return super.delegate.hasPermission(permission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sponge8CommandSender that = (Sponge8CommandSender) o;
        return this.getUniqueId().equals(that.getUniqueId());
    }

    @Override
    public int hashCode() {
        return getUniqueId().hashCode();
    }
}
