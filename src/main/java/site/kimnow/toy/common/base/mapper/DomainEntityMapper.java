package site.kimnow.toy.common.base.mapper;

public interface DomainEntityMapper<DOMAIN, ENTITY> {
    DOMAIN toDomain(ENTITY entity);
    ENTITY toEntity(DOMAIN domain);
}
