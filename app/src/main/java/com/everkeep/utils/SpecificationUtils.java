package com.everkeep.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.List;

@UtilityClass
public class SpecificationUtils {

    public static <T> Specification<T> findByAttributeEquals(String value, String attribute) {
        return (root, query, builder) -> builder.equal(root.get(attribute), value);
    }

    public static <T> Specification<T> findByAttributesLike(String value, List<String> attributes) {
        return (root, query, builder) -> builder.or(root.getModel().getDeclaredSingularAttributes().stream()
                .filter(attribute -> attributes.contains(attribute.getName()))
                .map(attribute -> builder.like(root.get(attribute.getName()), "%" + value + "%"))
                .toArray(Predicate[]::new)
        );
    }
}
