package org.jpa4guice.transaction;

import javax.persistence.EntityManagerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;


class EntityManagerFactoryProvider implements Provider<EntityManagerFactory> {
    private final EntityManagerFactoryHolder emFactoryHolder;

    @Inject
    public EntityManagerFactoryProvider(EntityManagerFactoryHolder sessionFactoryHolder) {
        this.emFactoryHolder = sessionFactoryHolder;
    }

    public EntityManagerFactory get() {
        return this.emFactoryHolder.getEntityManagerFactory();
    }
}