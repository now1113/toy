package site.kimnow.toy.common.base.jpa;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import site.kimnow.toy.common.base.domain.AggregateRoot;
import site.kimnow.toy.common.base.domain.DomainRepository;
import site.kimnow.toy.common.base.event.DomainEventDispatcher;
import site.kimnow.toy.common.base.mapper.DomainEntityMapper;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public abstract class BaseJpaRepository<
        DOMAIN extends AggregateRoot<DOMAIN, ID>,
        ID,
        ENTITY,
        ENTITY_ID,
        JPA_REPO extends JpaRepository<ENTITY, ENTITY_ID>
        > implements DomainRepository<DOMAIN, ID> {

    protected final JPA_REPO jpaRepository;
    protected final DomainEntityMapper<DOMAIN, ENTITY> mapper;
    protected final DomainEventDispatcher domainEventDispatcher;

    @Override
    public DOMAIN save(DOMAIN root) {
        jpaRepository.save(mapper.toEntity(root));

        domainEventDispatcher.dispatch(root.getDomainEvents());
        root.clearDomainEvents();

        return root;
    }

    @Override
    public Optional<DOMAIN> find(ID id) {
        ENTITY_ID entityId = convertId(id);
        return jpaRepository.findById(entityId)
                .map(mapper::toDomain);
    }

    @Override
    public void removeById(ID id) {
        ENTITY_ID entityId = convertId(id);
        jpaRepository.deleteById(entityId);
    }

    @Override
    public void remove(DOMAIN root) {
        ENTITY entity = mapper.toEntity(root);
        jpaRepository.delete(entity);
    }

    protected abstract ENTITY_ID convertId(ID id);
}
