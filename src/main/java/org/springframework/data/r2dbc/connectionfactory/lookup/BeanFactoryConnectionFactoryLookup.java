/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.r2dbc.connectionfactory.lookup;

import io.r2dbc.spi.ConnectionFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link ConnectionFactoryLookup} implementation based on a Spring {@link BeanFactory}.
 * <p>
 * Will lookup Spring managed beans identified by bean name, expecting them to be of type {@link ConnectionFactory}.
 *
 * @author Mark Paluch
 * @see BeanFactory
 * @deprecated since 1.2 in favor of Spring R2DBC. Use
 *             {@link org.springframework.r2dbc.connection.lookup.BeanFactoryConnectionFactoryLookup} instead.
 */
@Deprecated
public class BeanFactoryConnectionFactoryLookup implements ConnectionFactoryLookup, BeanFactoryAware {

	@Nullable private BeanFactory beanFactory;

	/**
	 * Creates a new {@link BeanFactoryConnectionFactoryLookup} instance.
	 * <p>
	 * The {@link BeanFactory} to access must be set via {@code setBeanFactory}.
	 *
	 * @see #setBeanFactory
	 */
	public BeanFactoryConnectionFactoryLookup() {}

	/**
	 * Create a new instance of the {@link BeanFactoryConnectionFactoryLookup} class.
	 * <p>
	 * Use of this constructor is redundant if this object is being created by a Spring IoC container, as the supplied
	 * {@link BeanFactory} will be replaced by the {@link BeanFactory} that creates it (see the {@link BeanFactoryAware}
	 * contract). So only use this constructor if you are using this class outside the context of a Spring IoC container.
	 *
	 * @param beanFactory the bean factory to be used to lookup {@link ConnectionFactory ConnectionFactories}.
	 */
	public BeanFactoryConnectionFactoryLookup(BeanFactory beanFactory) {

		Assert.notNull(beanFactory, "BeanFactory must not be null!");

		this.beanFactory = beanFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.r2dbc.connectionfactory.lookup.ConnectionFactoryLookup#getConnectionFactory(java.lang.String)
	 */
	@Override
	public ConnectionFactory getConnectionFactory(String connectionFactoryName)
			throws ConnectionFactoryLookupFailureException {

		Assert.state(this.beanFactory != null, "BeanFactory must not be null!");

		try {
			return this.beanFactory.getBean(connectionFactoryName, ConnectionFactory.class);
		} catch (BeansException ex) {
			throw new ConnectionFactoryLookupFailureException(
					String.format("Failed to look up ConnectionFactory bean with name '%s'", connectionFactoryName), ex);
		}
	}

}
