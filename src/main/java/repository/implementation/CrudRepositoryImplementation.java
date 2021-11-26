package repository.implementation;

import dto.PagedHumanBeingList;
import exceptions.BadRequestException;
import models.HumanBeing;
import models.Mood;
import models.WeaponType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import repository.CrudRepository;
import util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
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
        return em.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public Optional<T> findById(Integer id) {
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
        try {
            if (perPage != null && curPage != null) {

                int pageNumber = parseInteger(curPage);
                int pageSize = parseInteger(perPage);
                if (pageNumber <= 0) throw new BadRequestException("Bad format of pageNumber: " + pageNumber + ", should be natural number (1,2,...)");
                if (pageSize < 0) throw new BadRequestException("Bad format of pageSize: " + pageSize + ", should be non-negative integer number (0,1,...)");
                TypedQuery<HumanBeing> typedQuery = em.createQuery(select);
                typedQuery.setFirstResult((pageNumber - 1) * pageSize);
                typedQuery.setMaxResults(pageSize);
                return typedQuery.getResultList();
            } else
                return findAll(select);
        }catch (NumberFormatException e) {
            throw new BadRequestException("Bad format of pageSize or pageNumber. They should be integer numbers (1,2,...)");
        }

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
        if (sortBy != null) {
            List<String> criteria = new ArrayList<>(Arrays.asList(sortBy.split(";")));
            for (String criterion : criteria) {
                boolean order = String.valueOf(criterion.charAt(0)).equals(" ");
                switch (criterion.substring(1)) {
                    case ("id"):
                        if (order){
                            orderList.add(criteriaBuilder.asc(from.get("id")));
                        }
                        else {
                            orderList.add(criteriaBuilder.desc(from.get("id")));
                        }
                        break;
                    case ("name"):
                        if (order){
                            orderList.add(criteriaBuilder.asc(from.get("name")));
                        }
                        else {
                            orderList.add(criteriaBuilder.desc(from.get("name")));
                        }
                        break;
                    case ("realHero"):
                        if (order){
                            orderList.add(criteriaBuilder.asc(from.get("realHero")));
                        }
                        else {
                            orderList.add(criteriaBuilder.desc(from.get("realHero")));
                        }
                        break;
                    case ("hasToothpick"):
                        if (order){
                            orderList.add(criteriaBuilder.asc(from.get("hasToothpick")));
                        }
                        else {
                            orderList.add(criteriaBuilder.desc(from.get("hasToothpick")));
                        }
                        break;
                    case ("impactSpeed"):
                        if (order){
                            orderList.add(criteriaBuilder.asc(from.get("impactSpeed")));
                        }
                        else {
                            orderList.add(criteriaBuilder.desc(from.get("impactSpeed")));
                        }
                        break;
                    case ("soundtrackName"):
                        if (order){
                            orderList.add(criteriaBuilder.asc(from.get("soundtrackName")));
                        }
                        else {
                            orderList.add(criteriaBuilder.desc(from.get("soundtrackName")));
                        }
                        break;
                    case ("creationDate"):
                        if (order){
                            orderList.add(criteriaBuilder.asc(from.get("creationDate")));
                        }
                        else {
                            orderList.add(criteriaBuilder.desc(from.get("creationDate")));
                        }
                        break;
                    case ("coordinates"):
                        if (order){
                            orderList.add(criteriaBuilder.asc(from.get("coordinates").get("x")));
                        }
                        else {
                            orderList.add(criteriaBuilder.desc(from.get("coordinates").get("x")));
                        }
                        break;
                    case ("car"):
                        if (order){
                            orderList.add(criteriaBuilder.asc(from.get("car").get("name")));
                        }
                        else {
                            orderList.add(criteriaBuilder.desc(from.get("car").get("name")));
                        }
                        break;
                    case ("weaponType"):
                        if (order){
                            orderList.add(criteriaBuilder.asc(from.get("weaponType")));
                        }
                        else {
                            orderList.add(criteriaBuilder.desc(from.get("weaponType")));
                        }
                        break;
                    case ("mood"):
                        if (order){
                            orderList.add(criteriaBuilder.asc(from.get("mood")));
                        }
                        else {
                            orderList.add(criteriaBuilder.desc(from.get("mood")));
                        }
                        break;
                }
            }
        }
        return orderList;
    }

    private ArrayList<Predicate> getPredicatesList(String filterBy, CriteriaBuilder criteriaBuilder, Root<HumanBeing> from) {
        ArrayList<Predicate> predicates = new ArrayList<>();
        if (filterBy != null && !filterBy.isEmpty()) {
            List<String> notParsedFilters = new ArrayList<>(Arrays.asList(filterBy.split(";")));
            for (String filterString : notParsedFilters) {
                List<String> filter = new ArrayList<>(Arrays.asList(filterString.split(":")));
                switch (filter.get(0)) {
                    case ("id"):
                        predicates.add(criteriaBuilder.equal(from.get("id"), Integer.parseInt(filter.get(1))));
                        break;
                    case ("name"):
                        predicates.add(criteriaBuilder.equal(from.get("name"), filter.get(1)));
                        break;
                    case ("coordinates"):
                        List<String> coordinates = new ArrayList<>(Arrays.asList(filter.get(1).split(",")));
                        try {
                            double double_x = Double.parseDouble(coordinates.get(0));
                            double double_y = Double.parseDouble(coordinates.get(1));
                            Predicate x = criteriaBuilder.equal(from.get("coordinates").get("x"), double_x);
                            Predicate y = criteriaBuilder.equal(from.get("coordinates").get("y"), double_y);
                            predicates.add(criteriaBuilder.and(x, y));
                        }catch (NumberFormatException e){
                            throw new BadRequestException("Bad format of coordinates. They both should be present and not be empty");
                        }


                        break;
                    case ("creationDate"):
                        List<String> minutesFormat = new ArrayList<>(Arrays.asList(filter.get(2).split(",")));
                        List<String> dateParameters = new ArrayList<>();
                        dateParameters.add((filter.get(1) + ":" + minutesFormat.get(0)).replace("T", " "));
                        dateParameters.add(minutesFormat.get(1));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        if (dateParameters.get(1).equals("after")){
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(from.get("creationDate"), LocalDateTime.parse(dateParameters.get(0), formatter)));
                        }
                        if (dateParameters.get(1).equals("before")){
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(from.get("creationDate"), LocalDateTime.parse(dateParameters.get(0), formatter)));
                        }
                        break;
                    case ("realHero"):
                        predicates.add(criteriaBuilder.equal(from.get("realHero"), Boolean.parseBoolean(filter.get(1))));
                        break;
                    case ("hasToothpick"):
                        predicates.add(criteriaBuilder.equal(from.get("hasToothpick"), Boolean.parseBoolean(filter.get(1))));
                        break;
                    case ("impactSpeed"):
                        predicates.add(criteriaBuilder.equal(from.get("impactSpeed"), Float.parseFloat(filter.get(1))));
                        break;
                    case ("weaponType"):
                        predicates.add(criteriaBuilder.equal(from.get("weaponType"), WeaponType.valueOf(filter.get(1))));
                        break;
                    case ("mood"):
                        predicates.add(criteriaBuilder.equal(from.get("mood"), Mood.valueOf(filter.get(1))));
                        break;
                    case ("soundtrackName"):
                        predicates.add(criteriaBuilder.equal(from.get("soundtrackName"), filter.get(1)));
                        break;
                    case ("car"):
                        predicates.add(criteriaBuilder.equal(from.get("car").get("name"), filter.get(1)));
                        break;
                }
            }
        }

        return predicates;
    }

    @Override
    public T update(T entry) {
        em.getTransaction().begin();
        try {
            em.merge(entry);
            em.flush();
            em.getTransaction().commit();
        } catch (ConstraintViolationException e){
            em.getTransaction().rollback();
        }
        return entry;
    }

    @Override
    public void save(T entry) {
        em.getTransaction().begin();
        em.persist(entry);
        em.getTransaction().commit();
    }

    @Override
    public void deleteById(Integer id) {
        em.getTransaction().begin();
        em.remove(this.findById(id).get());
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

    public void clearEntityManager() {
        em.clear();
    }

}