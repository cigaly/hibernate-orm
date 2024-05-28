/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.results.graph.entity.internal;

import java.util.function.BiConsumer;

import org.hibernate.FetchNotFoundException;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.engine.spi.EntityHolder;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.log.LoggingHelper;
import org.hibernate.metamodel.mapping.AttributeMapping;
import org.hibernate.metamodel.mapping.ModelPart;
import org.hibernate.metamodel.mapping.internal.ToOneAttributeMapping;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.spi.NavigablePath;
import org.hibernate.sql.results.graph.AssemblerCreationState;
import org.hibernate.sql.results.graph.DomainResult;
import org.hibernate.sql.results.graph.DomainResultAssembler;
import org.hibernate.sql.results.graph.Initializer;
import org.hibernate.sql.results.graph.InitializerData;
import org.hibernate.sql.results.graph.InitializerParent;
import org.hibernate.sql.results.graph.entity.EntityInitializer;
import org.hibernate.sql.results.graph.internal.AbstractInitializer;
import org.hibernate.sql.results.jdbc.spi.RowProcessingState;

import org.checkerframework.checker.nullness.qual.Nullable;

import static org.hibernate.internal.log.LoggingHelper.toLoggableString;
import static org.hibernate.proxy.HibernateProxy.extractLazyInitializer;

/**
 * @author Andrea Boriero
 */
public class EntitySelectFetchInitializer<Data extends EntitySelectFetchInitializer.EntitySelectFetchInitializerData>
		extends AbstractInitializer<Data> implements EntityInitializer<Data> {
	private static final String CONCRETE_NAME = EntitySelectFetchInitializer.class.getSimpleName();

	protected final InitializerParent<?> parent;
	private final NavigablePath navigablePath;
	private final boolean isPartOfKey;
	private final boolean isEnhancedForLazyLoading;

	protected final EntityPersister concreteDescriptor;
	protected final DomainResultAssembler<?> keyAssembler;
	protected final ToOneAttributeMapping toOneMapping;

	public static class EntitySelectFetchInitializerData extends InitializerData {
		// per-row state
		protected @Nullable Object entityIdentifier;

		public EntitySelectFetchInitializerData(RowProcessingState rowProcessingState) {
			super( rowProcessingState );
		}

	}

	public EntitySelectFetchInitializer(
			InitializerParent<?> parent,
			ToOneAttributeMapping toOneMapping,
			NavigablePath fetchedNavigable,
			EntityPersister concreteDescriptor,
			DomainResult<?> keyResult,
			AssemblerCreationState creationState) {
		super( creationState );
		this.parent = parent;
		this.toOneMapping = toOneMapping;
		this.navigablePath = fetchedNavigable;
		this.isPartOfKey = Initializer.isPartOfKey( fetchedNavigable, parent );
		this.concreteDescriptor = concreteDescriptor;
		this.keyAssembler = keyResult.createResultAssembler( this, creationState );
		this.isEnhancedForLazyLoading = concreteDescriptor.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading();
	}

	@Override
	protected InitializerData createInitializerData(RowProcessingState rowProcessingState) {
		return new EntitySelectFetchInitializerData( rowProcessingState );
	}

	public ModelPart getInitializedPart(){
		return toOneMapping;
	}

	@Override
	public @Nullable InitializerParent<?> getParent() {
		return parent;
	}

	@Override
	public NavigablePath getNavigablePath() {
		return navigablePath;
	}

	@Override
	public void resolveInstance(Data data) {
		if ( data.getState() != State.KEY_RESOLVED ) {
			return;
		}

		RowProcessingState rowProcessingState1 = data.getRowProcessingState();
		data.entityIdentifier = keyAssembler.assemble( rowProcessingState1 );

		if ( data.entityIdentifier == null ) {
			data.setState( State.MISSING );
			data.setInstance( null );
			return;
		}
		data.setState( State.INITIALIZED );
		initialize( data );
	}

	@Override
	public void resolveInstance(Object instance, Data data) {
		if ( instance == null ) {
			data.setState(  State.MISSING );
			data.entityIdentifier = null;
			data.setInstance( null );
		}
		else {
			final RowProcessingState rowProcessingState = data.getRowProcessingState();
			final SharedSessionContractImplementor session = rowProcessingState.getSession();
			final LazyInitializer lazyInitializer = extractLazyInitializer( data.getInstance() );
			if ( lazyInitializer == null ) {
				data.setState( State.INITIALIZED );
				data.entityIdentifier = concreteDescriptor.getIdentifier( instance, session );
			}
			else if ( lazyInitializer.isUninitialized() ) {
				data.setState( State.RESOLVED );
				data.entityIdentifier = lazyInitializer.getIdentifier();
			}
			else {
				data.setState( State.INITIALIZED );
				data.entityIdentifier = lazyInitializer.getIdentifier();
			}
			data.setInstance( instance );
			final Initializer<?> initializer = keyAssembler.getInitializer();
			if ( initializer != null ) {
				initializer.resolveInstance( data.entityIdentifier, rowProcessingState );
			}
			else if ( rowProcessingState.needsResolveState() ) {
				// Resolve the state of the identifier if result caching is enabled and this is not a query cache hit
				keyAssembler.resolveState( rowProcessingState );
			}
		}
	}

	@Override
	public void initializeInstance(Data data) {
		if ( data.getState() != State.RESOLVED ) {
			return;
		}
		data.setState( State.INITIALIZED );
		Hibernate.initialize( data.getInstance() );
	}

	protected void initialize(EntitySelectFetchInitializerData data) {
		final RowProcessingState rowProcessingState = data.getRowProcessingState();
		final SharedSessionContractImplementor session = rowProcessingState.getSession();
		final EntityKey entityKey = new EntityKey( data.entityIdentifier, concreteDescriptor );

		final PersistenceContext persistenceContext = session.getPersistenceContextInternal();
		final EntityHolder holder = persistenceContext.getEntityHolder( entityKey );
		if ( holder != null ) {
			data.setInstance( persistenceContext.proxyFor( holder, concreteDescriptor ) );
			if ( holder.getEntityInitializer() == null ) {
				if ( data.getInstance() != null && Hibernate.isInitialized( data.getInstance() ) ) {
					data.setState( State.INITIALIZED );
					return;
				}
			}
			else if ( holder.getEntityInitializer() != this ) {
				// the entity is already being loaded elsewhere
				data.setState( State.INITIALIZED );
				return;
			}
			else if ( data.getInstance() == null ) {
				// todo: maybe mark this as resolved instead?
				assert holder.getProxy() == null : "How to handle this case?";
				data.setState( State.INITIALIZED );
				return;
			}
		}
		data.setState( State.INITIALIZED );
		final String entityName = concreteDescriptor.getEntityName();

		data.setInstance( session.internalLoad(
				entityName,
				data.entityIdentifier,
				true,
				toOneMapping.isInternalLoadNullable()
		) );

		if ( data.getInstance() == null ) {
			if ( toOneMapping.getNotFoundAction() == NotFoundAction.EXCEPTION ) {
				throw new FetchNotFoundException( entityName, data.entityIdentifier );
			}
			rowProcessingState.getSession().getPersistenceContextInternal().claimEntityHolderIfPossible(
					new EntityKey( data.entityIdentifier, concreteDescriptor ),
					data.getInstance(),
					rowProcessingState.getJdbcValuesSourceProcessingState(),
					this
			);
		}

		final boolean unwrapProxy = toOneMapping.isUnwrapProxy() && isEnhancedForLazyLoading;
		final LazyInitializer lazyInitializer = HibernateProxy.extractLazyInitializer( data.getInstance() );
		if ( lazyInitializer != null ) {
			lazyInitializer.setUnwrap( unwrapProxy );
		}
	}

	@Override
	public void initializeInstanceFromParent(Object parentInstance, Data data) {
		final AttributeMapping attributeMapping = getInitializedPart().asAttributeMapping();
		final Object instance = attributeMapping != null
				? attributeMapping.getValue( parentInstance )
				: parentInstance;
		if ( instance == null ) {
			data.setState( State.MISSING );
			data.entityIdentifier = null;
			data.setInstance( null );
		}
		else {
			data.setState( State.INITIALIZED );
			// No need to initialize this
			data.entityIdentifier = null;
			data.setInstance( instance );
			Hibernate.initialize( instance );
		}
	}

	@Override
	protected void forEachSubInitializer(BiConsumer<Initializer<?>, RowProcessingState> consumer, InitializerData data) {
		final Initializer<?> initializer = keyAssembler.getInitializer();
		if ( initializer != null ) {
			consumer.accept( initializer, data.getRowProcessingState() );
		}
	}

	@Override
	public EntityPersister getEntityDescriptor() {
		return concreteDescriptor;
	}

	@Override
	public Object getEntityInstance(Data data) {
		return data.getInstance();
	}

	@Override
	public EntityPersister getConcreteDescriptor(Data data) {
		return concreteDescriptor;
	}

	@Override
	public @Nullable Object getEntityIdentifier(Data data) {
		return data.entityIdentifier;
	}

	@Override
	public boolean isPartOfKey() {
		return isPartOfKey;
	}

	@Override
	public boolean isResultInitializer() {
		return false;
	}

	@Override
	public String toString() {
		return "EntitySelectFetchInitializer(" + LoggingHelper.toLoggableString( getNavigablePath() ) + ")";
	}

}
