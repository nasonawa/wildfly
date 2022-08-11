/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2020, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.clustering.marshalling.protostream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.PrivilegedAction;

import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.SerializationContextInitializer;
import org.wildfly.security.manager.WildFlySecurityManager;

/**
 * @author Paul Ferraro
 */
public abstract class AbstractSerializationContextInitializer implements SerializationContextInitializer, PrivilegedAction<FileDescriptorSource> {

    private final String resourceName;
    private final ClassLoader loader;

    protected AbstractSerializationContextInitializer() {
        this(null);
    }

    protected AbstractSerializationContextInitializer(String resourceName) {
        this(resourceName, null);
    }

    protected AbstractSerializationContextInitializer(String resourceName, ClassLoader loader) {
        this.resourceName = (resourceName == null) ? this.getClass().getPackage().getName() + ".proto" : resourceName;
        this.loader = (loader == null) ? WildFlySecurityManager.getClassLoaderPrivileged(this.getClass()) : loader;
    }

    @Deprecated
    @Override
    public final String getProtoFileName() {
        return null;
    }

    @Deprecated
    @Override
    public final String getProtoFile() {
        return null;
    }

    @Override
    public void registerSchema(SerializationContext context) {
        context.registerProtoFiles(WildFlySecurityManager.doUnchecked(this));
    }

    @Override
    public FileDescriptorSource run() {
        try {
            return FileDescriptorSource.fromResources(this.loader, this.resourceName);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String toString() {
        return this.resourceName;
    }
}
