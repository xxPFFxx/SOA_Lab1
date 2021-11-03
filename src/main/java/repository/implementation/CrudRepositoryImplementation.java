package repository.implementation;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import repository.CrudRepository;
import util.HibernateUtil;
import util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public class CrudRepositoryImplementation<T> implements CrudRepository<T> {
    private final SessionFactory sessionFactory;
    private final Class<T> tClass;
    private Session session;
    private EntityManager em;

    public CrudRepositoryImplementation(Class<T> tClass) {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        this.tClass = tClass;
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.em = session.getEntityManagerFactory().createEntityManager();
    }

    @Override
    public List<T> findByCriteria(CriteriaQuery<T> criteriaQuery) {
        EntityManager em = sessionFactory.createEntityManager();
        return em.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public Optional<T> findById(Integer id) {
        EntityManager em = sessionFactory.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(tClass);
        Root<T> root = query.from(tClass);
        query.select(root);
        query.where(cb.equal(root.get("id"), id));
        return Optional.ofNullable(em.createQuery(query).getSingleResult());
    }

    public List<T> findAll() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(tClass);
        Root<T> from = criteriaQuery.from(tClass);
        CriteriaQuery<T> select = criteriaQuery.select(from);
        TypedQuery<T> typedQuery = em.createQuery(select);
        return typedQuery.getResultList();
    }

    @Override
    public T update(T entry) {
        EntityManager em = sessionFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(entry);
        em.flush();
        em.getTransaction().commit();
        return entry;
    }

    @Override
    public void save(T entry) {
        EntityManager em = sessionFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(entry);
        em.getTransaction().commit();
    }

    @Override
    public void deleteById(Integer id) {
        EntityManager em = sessionFactory.createEntityManager();
        em.getTransaction().begin();
        em.remove(this.findById(id));
        em.getTransaction().commit();
    }

    @Override
    public EntityManager createEntityManager() {
        return sessionFactory.createEntityManager();
    }

    @Override
    public Session openSession() {
        return sessionFactory.openSession();
    }

}