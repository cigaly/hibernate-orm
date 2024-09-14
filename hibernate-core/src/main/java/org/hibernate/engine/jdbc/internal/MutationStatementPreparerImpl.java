/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.jdbc.internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.AssertionFailure;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.MutationStatementPreparer;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.event.spi.EventManager;
import org.hibernate.event.spi.HibernateMonitoringEvent;
import org.hibernate.resource.jdbc.spi.JdbcEventHandler;
import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;
import org.hibernate.resource.jdbc.spi.LogicalConnectionImplementor;

/**
 * @author Steve Ebersole
 */
public class MutationStatementPreparerImpl implements MutationStatementPreparer {
	private final JdbcCoordinatorImpl jdbcCoordinator;
	private final JdbcServices jdbcServices;

	public MutationStatementPreparerImpl(JdbcCoordinatorImpl jdbcCoordinator, JdbcServices jdbcServices) {
		this.jdbcCoordinator = jdbcCoordinator;
		this.jdbcServices = jdbcServices;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, boolean isCallable) {
		return buildPreparedStatementPreparationTemplate( sql, isCallable ).prepareStatement();
	}

	private StatementPreparationTemplate buildPreparedStatementPreparationTemplate(String sql, final boolean isCallable) {
		return new StatementPreparationTemplate( sql ) {
			@Override
			protected PreparedStatement doPrepare() throws SQLException {
				//noinspection resource
				return isCallable
						? connection().prepareCall( sql )
						: connection().prepareStatement( sql );
			}
		};
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) {
		if ( autoGeneratedKeys == PreparedStatement.RETURN_GENERATED_KEYS ) {
			checkAutoGeneratedKeysSupportEnabled();
		}
		return new StatementPreparationTemplate( sql ) {
			public PreparedStatement doPrepare() throws SQLException {
				//noinspection resource
				return connection().prepareStatement( sql, autoGeneratedKeys );
			}
		}.prepareStatement();
	}

	private void checkAutoGeneratedKeysSupportEnabled() {
		if ( ! settings().isGetGeneratedKeysEnabled() ) {
			throw new AssertionFailure( "getGeneratedKeys() support is not enabled" );
		}
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) {
		checkAutoGeneratedKeysSupportEnabled();
		return new StatementPreparationTemplate( sql ) {
			public PreparedStatement doPrepare() throws SQLException {
				//noinspection resource
				return connection().prepareStatement( sql, columnNames );
			}
		}.prepareStatement();
	}

	private abstract class StatementPreparationTemplate {
		protected final String sql;

		protected StatementPreparationTemplate(String incomingSql) {
			final String inspectedSql = jdbcCoordinator.getJdbcSessionOwner()
					.getJdbcSessionContext()
					.getStatementInspector()
					.inspect( incomingSql );
			this.sql = inspectedSql == null ? incomingSql : inspectedSql;
		}

		public PreparedStatement prepareStatement() {
			try {
				final PreparedStatement preparedStatement;
				final JdbcSessionOwner jdbcSessionOwner = jdbcCoordinator.getJdbcSessionOwner();
				final JdbcEventHandler jdbcEventHandler = jdbcSessionOwner.getJdbcSessionContext().getEventHandler();
				final EventManager eventManager = jdbcSessionOwner.getEventManager();
				final HibernateMonitoringEvent jdbcPreparedStatementCreation = eventManager.beginJdbcPreparedStatementCreationEvent();
				try {
					jdbcEventHandler.jdbcPrepareStatementStart();
					preparedStatement = doPrepare();
					setStatementTimeout( preparedStatement );
				}
				finally {
					eventManager.completeJdbcPreparedStatementCreationEvent( jdbcPreparedStatementCreation, sql );
					jdbcEventHandler.jdbcPrepareStatementEnd();
				}
				postProcess( preparedStatement );
				return preparedStatement;
			}
			catch (SQLException e) {
				throw sqlExceptionHelper().convert( e, "could not prepare statement", sql );
			}
		}

		protected abstract PreparedStatement doPrepare() throws SQLException;

		public void postProcess(PreparedStatement preparedStatement) throws SQLException {
			jdbcCoordinator.getLogicalConnection().getResourceRegistry().register( preparedStatement, true );
//			logicalConnection().notifyObserversStatementPrepared();
		}

		private void setStatementTimeout(PreparedStatement preparedStatement) throws SQLException {
			final int remainingTransactionTimeOutPeriod = jdbcCoordinator.determineRemainingTransactionTimeOutPeriod();
			if ( remainingTransactionTimeOutPeriod > 0 ) {
				preparedStatement.setQueryTimeout( remainingTransactionTimeOutPeriod );
			}
		}
	}

	protected final Connection connection() {
		return logicalConnection().getPhysicalConnection();
	}

	protected final LogicalConnectionImplementor logicalConnection() {
		return jdbcCoordinator.getLogicalConnection();
	}

	protected final SqlExceptionHelper sqlExceptionHelper() {
		return jdbcServices.getSqlExceptionHelper();
	}

	protected final JdbcSessionContext settings() {
		return jdbcCoordinator.getJdbcSessionOwner().getJdbcSessionContext();
	}
}
