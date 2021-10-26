package repository;


import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    List<T> findByCriteria(CriteriaQuery<T> criteriaQuery);

    Optional<T> findById(Integer id);

    T update(T coordinates);

    void save(T coordinates);

    void deleteById(Integer id);

    EntityManager createEntityManager();

    Session openSession();
}