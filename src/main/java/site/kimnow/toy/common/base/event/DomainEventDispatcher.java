package site.kimnow.toy.common.base.event;

import java.util.List;

public interface DomainEventDispatcher {
    void dispatch(List<DomainEvent> events);
}
