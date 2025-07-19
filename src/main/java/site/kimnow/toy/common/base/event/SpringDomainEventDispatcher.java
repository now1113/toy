package site.kimnow.toy.common.base.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpringDomainEventDispatcher implements DomainEventDispatcher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void dispatch(List<DomainEvent> events) {
        events.forEach(publisher::publishEvent);
    }
}
