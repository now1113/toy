package site.kimnow.toy.common.base.domain;


public interface Repository<DOMAIN, ID> {
    void save(DOMAIN root);
    DOMAIN find(ID id);
    void removeById(ID id);
    void remove(DOMAIN root);
}