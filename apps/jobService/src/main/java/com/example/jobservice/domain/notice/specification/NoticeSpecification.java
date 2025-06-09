package com.example.jobservice.domain.notice.specification;

import com.example.jobservice.domain.notice.entity.Notice;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class NoticeSpecification {

    public static Specification<Notice> searchByConditions(
            List<String> regionList,
            List<String> categoryList,
            List<String> historyList,
            List<String> eduList,
            List<String> typeList,
            String keyword
    ){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (regionList != null && !regionList.contains("all")) {
                Predicate regionPredicate = cb.or(regionList.stream()
                        .map(region -> cb.like(root.get("workRgnLst"), "%" + region + "%"))
                        .toArray(Predicate[]::new));
                predicates.add(regionPredicate);
            }

            if (categoryList != null && !categoryList.contains("all")) {
                Predicate categoryPredicate = cb.or(categoryList.stream()
                        .map(category -> cb.like(root.get("ncsCdLst"), "%" + category + "%"))
                        .toArray(Predicate[]::new));
                predicates.add(categoryPredicate);
            }

            if (historyList != null && !historyList.contains("all")) {
                Predicate historyPredicate = cb.or(historyList.stream()
                        .map(h -> cb.like(root.get("recrutSe"), "%" + h + "%"))
                        .toArray(Predicate[]::new));
                predicates.add(historyPredicate);
            }

            if (eduList != null && !eduList.contains("all")) {
                Predicate eduPredicate = cb.or(eduList.stream()
                        .map(e -> cb.like(root.get("acbgCondLst"), "%" + e + "%"))
                        .toArray(Predicate[]::new));
                predicates.add(eduPredicate);
            }

            if (typeList != null && !typeList.contains("all")) {
                Predicate typePredicate = cb.or(typeList.stream()
                        .map(t -> cb.like(root.get("hireTypeLst"), "%" + t + "%"))
                        .toArray(Predicate[]::new));
                predicates.add(typePredicate);
            }

            if (keyword != null && !keyword.isBlank()) {
                Predicate keywordPredicate = cb.or(
                        cb.like(root.get("recrutPbancTtl"), "%" + keyword + "%"),
                        cb.like(root.get("instNm"), "%" + keyword + "%")
                );
                predicates.add(keywordPredicate);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
