package site.kimnow.toy.common.base.jpa;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import site.kimnow.toy.common.base.domain.AggregateRoot;
import site.kimnow.toy.common.base.domain.Repository;
import site.kimnow.toy.common.base.mapper.DomainEntityMapper;

@AllArgsConstructor
public abstract class BaseRepository<
        DOMAIN extends AggregateRoot<DOMAIN, ID>,
        ID,
        ENTITY,
        ENTITY_ID,
        JPA_REPO extends JpaRepository<ENTITY, ENTITY_ID>
        > implements Repository<DOMAIN, ID> {

    protected final JPA_REPO jpaRepository;
    protected final DomainEntityMapper<DOMAIN, ENTITY> mapper;

    @Override
    public void save(DOMAIN root) {
        ENTITY entity = mapper.toEntity(root);
        jpaRepository.save(entity);
    }

    @Override
    public DOMAIN find(ID id) {
        ENTITY_ID entityId = convertId(id);
        return jpaRepository.findById(entityId)
                .map(mapper::toDomain)
                .orElse(null);
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
