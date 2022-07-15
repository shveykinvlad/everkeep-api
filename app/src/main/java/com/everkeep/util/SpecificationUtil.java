package com.everkeep.util;

import javax.persistence.criteria.Predicate;
import java.util.List;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class SpecificationUtil {

    public <T> Specification<T> isEqualToAttribute(String value, String attribute) {
        return (root, query, builder) -> builder.equal(root.get(attribute), value);
    }

    public <T> Specification<T> isContainedInAttributes(String value, List<String> attributes) {
        return (root, query, builder) -> builder.or(root.getModel().getDeclaredSingularAttributes().stream()
                .filter(attribute -> attributes.contains(attribute.getName()))
                .map(attribute -> builder.like(root.get(attribute.getName()), "%" + value + "%"))
                .toArray(Predicate[]::new)
        );
    }
}
