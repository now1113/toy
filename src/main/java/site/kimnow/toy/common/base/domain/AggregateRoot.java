package site.kimnow.toy.common.base.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;
import site.kimnow.toy.common.base.event.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AggregateRoot<T extends DomainEntity<T, TID>, TID> extends DomainEntity<T, TID> {

    @Transient
    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        Assert.notNull(event, "Domain event must not be null");
        this.domainEvents.add(event);
    }

    // 도메인 이벤트 리스트 읽기
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    // 도메인 이벤트 리스트 비우기
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
