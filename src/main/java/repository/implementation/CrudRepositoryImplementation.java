package repository.implementation;

import dto.PagedHumanBeingList;
import dto.dtoList.HumanBeingDTOList;
import exceptions.EntityIsNotValidException;
import models.HumanBeing;
import models.Mood;
import models.WeaponType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import repository.CrudRepository;
import util.HibernateUtil;
import util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static util.UrlParametersUtil.parseInteger;

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

    public PagedHumanBeingList findAll(String perPage, String curPage, String sortBy, String filterBy) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<HumanBeing> criteriaQuery = criteriaBuilder.createQuery(HumanBeing.class);
        Root<HumanBeing> from = criteriaQuery.from(HumanBeing.class);
        CriteriaQuery<HumanBeing> select = criteriaQuery.select(from);

        List<Order> orderList = getOrderList(sortBy, criteriaBuilder, from);
        if (!orderList.isEmpty())
            criteriaQuery.orderBy(orderList);

        ArrayList<Predicate> predicates = getPredicatesList(filterBy, criteriaBuilder, from);
        if (!predicates.isEmpty())
            select.where(predicates.toArray(new Predicate[]{}));

        PagedHumanBeingList pagedHumanBeingList = new PagedHumanBeingList();

        pagedHumanBeingList.setCount(getOverallCount(criteriaBuilder, predicates));
        pagedHumanBeingList.setHumanBeingList(findAll(perPage, curPage, select));

        return pagedHumanBeingList;

    }

    private List<HumanBeing> findAll(String perPage, String curPage, CriteriaQuery<HumanBeing> select) {
        if (perPage != null && curPage != null) {
            int pageNumber = parseInteger(curPage);
            int pageSize = parseInteger(perPage);
            TypedQuery<HumanBeing> typedQuery = em.createQuery(select);
            typedQuery.setFirstResult((pageNumber - 1) * pageSize);
            typedQuery.setMaxResults(pageSize);
            return typedQuery.getResultList();
        } else
            return findAll(select);
    }

    private List<HumanBeing> findAll(CriteriaQuery<HumanBeing> select) {
        TypedQuery<HumanBeing> typedQuery = em.createQuery(select);
        return typedQuery.getResultList();
    }

    private Long getOverallCount(CriteriaBuilder criteriaBuilder, ArrayList<Predicate> predicates) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        countQuery.select(criteriaBuilder.count(countQuery.from(HumanBeing.class)));
        em.createQuery(countQuery);
        if (predicates.size() > 0)
            countQuery.where(predicates.toArray(new Predicate[]{}));
        return em.createQuery(countQuery).getSingleResult();

    }

    private List<Order> getOrderList(String sortBy, CriteriaBuilder criteriaBuilder, Root<HumanBeing> from) {
        List<Order> orderList = new ArrayList();
        System.out.println(sortBy);
        if (sortBy != null) {
            List<String> criteria = new ArrayList<>(Arrays.asList(sortBy.split(";")));
            for (String criterion : criteria) {

                switch (criterion) {
                    case ("id"):
                        orderList.add(criteriaBuilder.asc(from.get("id")));
                        break;
                    case ("name"):
                        orderList.add(criteriaBuilder.asc(from.get("name")));
                        break;
                    case ("realHero"):
                        orderList.add(criteriaBuilder.asc(from.get("realHero")));
                        break;
                    case ("hasToothpick"):
                        orderList.add(criteriaBuilder.asc(from.get("hasToothpick")));
                        break;
                    case ("impactSpeed"):
                        orderList.add(criteriaBuilder.asc(from.get("impactSpeed")));
                        break;
                    case ("soundtrackName"):
                        orderList.add(criteriaBuilder.asc(from.get("soundtrackName")));
                        break;
                    case ("date"):
                        orderList.add(criteriaBuilder.asc(from.get("creationDate")));
                        break;
                    case ("coordinate"):
                        orderList.add(criteriaBuilder.asc(from.get("coordinates").get("x")));
                        break;
                    case ("car"):
                        orderList.add(criteriaBuilder.asc(from.get("car").get("name")));
                        break;
                    case ("weaponType"):
                        orderList.add(criteriaBuilder.asc(from.get("weaponType")));
                        break;
                    case ("mood"):
                        orderList.add(criteriaBuilder.asc(from.get("mood")));
                        break;
                }
            }
        }
        return orderList;
    }

    private ArrayList<Predicate> getPredicatesList(String filterBy, CriteriaBuilder criteriaBuilder, Root<HumanBeing> from) {
        ArrayList<Predicate> predicates = new ArrayList<>();
        if (filterBy != null) {
            List<String> notParsedFilters = new ArrayList<>(Arrays.asList(filterBy.split(";")));
            for (String filterString : notParsedFilters) {
                List<String> filter = new ArrayList<>(Arrays.asList(filterString.split(",")));
                switch (filter.get(0)) {
                    case ("id"):
                        if (filter.size() < 3) throw new EntityIsNotValidException("number of arguments less than required");
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(from.get("id"), Integer.parseInt(filter.get(1))));
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(from.get("id"), Integer.parseInt(filter.get(2))));
                        break;
                    case ("name"):
                        if (filter.size() < 2) throw new EntityIsNotValidException("number of arguments less than required");
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(from.get("name")),
                                filter.get(1).toUpperCase() + "%"));
                        break;
                    case ("oscar"):
                        if (filter.size() < 3) throw new EntityIsNotValidException("number of arguments less than required");
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(from.get("oscarsCount"), Integer.parseInt(filter.get(1))));
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(from.get("oscarsCount"), Integer.parseInt(filter.get(2))));
                        break;
                    case ("duration"):
                        if (filter.size() < 3) throw new EntityIsNotValidException("number of arguments less than required");
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(from.get("duration"), Integer.parseInt(filter.get(1))));
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(from.get("duration"), Integer.parseInt(filter.get(2))));
                        break;
                    case ("weaponType"):
                        if (filter.size() < 2) throw new EntityIsNotValidException("number of arguments less than required");
                        predicates.add(criteriaBuilder.equal(from.get("weaponType"), WeaponType.valueOf(filter.get(1))));
                        break;
                    case ("mood"):
                        if (filter.size() < 2) throw new EntityIsNotValidException("number of arguments less than required");
                        predicates.add(criteriaBuilder.equal(from.get("mood"), Mood.valueOf(filter.get(1))));
                        break;
                    case ("date"):
                        if (filter.size() < 3) throw new EntityIsNotValidException("number of arguments less than required");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/y");
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(from.get("creationDate"), LocalDate.parse(filter.get(1), formatter)));
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(from.get("creationDate"), LocalDate.parse(filter.get(2), formatter)));
                        break;
                    case ("coordinate"):
                        if (filter.size() < 5) throw new EntityIsNotValidException("number of arguments less than required");
                        Predicate x = criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(from.get("coordinates").get("x"), Double.parseDouble(filter.get(1))),
                                criteriaBuilder.lessThanOrEqualTo(from.get("coordinates").get("x"), Double.parseDouble(filter.get(2))));
                        Predicate y = criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(from.get("coordinates").get("y"), Double.parseDouble(filter.get(3))),
                                criteriaBuilder.lessThanOrEqualTo(from.get("coordinates").get("y"), Double.parseDouble(filter.get(4))));
                        predicates.add(criteriaBuilder.and(x, y));
                        break;
                    case ("screenWriter"):
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(from.get("screenWriter").get("name")),
                                filter.get(1).toUpperCase() + "%"));
                        break;
                }
            }
        }

        return predicates;
    }

    @Override
    public T update(T entry) {
        EntityManager em = sessionFactory.createEntityManager();
        em.getTransaction().begin();
        em.merge(entry);
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