/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import java.lang.reflect.Method;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

/**
 * @author Steve Ebersole
 */
public class BitronixJtaPlatform extends AbstractJtaPlatform {
	public static final String TM_CLASS_NAME = "bitronix.tm.TransactionManagerServices";

	@Override
	protected TransactionManager locateTransactionManager() {
		try {
			final Method getTransactionManagerMethod =
					serviceRegistry().requireService( ClassLoaderService.class )
							.classForName( TM_CLASS_NAME )
							.getMethod( "getTransactionManager" );
			return (TransactionManager) getTransactionManagerMethod.invoke( null );
		}
		catch (Exception e) {
			throw new JtaPlatformException( "Could not locate Bitronix TransactionManager", e );
		}
	}

	@Override
	protected UserTransaction locateUserTransaction() {
		return (UserTransaction) jndiService().locate( "java:comp/UserTransaction" );
	}
}
