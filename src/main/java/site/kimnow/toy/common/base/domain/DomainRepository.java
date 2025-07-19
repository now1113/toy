package site.kimnow.toy.common.base.domain;


import java.util.Optional;

public interface DomainRepository<DOMAIN, ID> {
    DOMAIN save(DOMAIN root);
    Optional<DOMAIN> find(ID id);
    void removeById(ID id);
    void remove(DOMAIN root);
}