package site.kimnow.toy.common.base.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class LongTypeIdentifier extends ValueObject<LongTypeIdentifier> {
    private Long id;

    @Override
    protected Object[] getEqualityFields() {
        return new Object[] { id };
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " : " + getId();
    }

    public Long longValue() {
        return id;
    }

}
