package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CountDTO;
import dto.PagedHumanBeingList;
import dto.dtoList.HumanBeingDTOList;
import mapper.HumanBeingMapper;
import models.HumanBeing;
import models.WeaponType;
import org.hibernate.Session;
import org.hibernate.query.Query;
import util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/additional")
public class AdditionalTasksServlet extends HttpServlet {
    private Session session;
    private EntityManager em;
    private Gson gson;
    private HumanBeingMapper humanBeingMapper;

    @Override
    public void init() throws ServletException {
        session = HibernateUtil.getSessionFactory().openSession();
        em = session.getEntityManagerFactory().createEntityManager();
        gson = new GsonBuilder().setPrettyPrinting().create();
        humanBeingMapper = new HumanBeingMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cors(response);
        String weaponTypeCount = request.getParameter("weaponTypeCount");
        String weaponTypeArray = request.getParameter("weaponTypeArray");
        String uniqueImpactSpeed = request.getParameter("uniqueImpactSpeed");

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        CriteriaQuery<HumanBeing> criteriaQuery = criteriaBuilder.createQuery(HumanBeing.class);
        Root<HumanBeing> from = criteriaQuery.from(HumanBeing.class);
        countQuery.select(criteriaBuilder.count(countQuery.from(HumanBeing.class)));
        em.createQuery(countQuery);

        if (weaponTypeCount != null) {
            countQuery.where(criteriaBuilder.greaterThan(from.get("weaponType"), WeaponType.valueOf(weaponTypeCount)));
            Long countResult = em.createQuery(countQuery).getSingleResult();
            CountDTO countDTO = new CountDTO();
            countDTO.setCount(countResult);
            response.getWriter().write(gson.toJson(countDTO));
            return;
        }
        if (weaponTypeArray != null) {
            criteriaQuery.where(criteriaBuilder.greaterThan(from.get("weaponType"), WeaponType.valueOf(weaponTypeArray)));
            List<HumanBeing> humanBeingList = em.createQuery(criteriaQuery).getResultList();
            PagedHumanBeingList pagedHumanBeingList = new PagedHumanBeingList();
            pagedHumanBeingList.setHumanBeingList(humanBeingList);
            pagedHumanBeingList.setCount(Long.parseLong(String.valueOf(humanBeingList.size())));
            HumanBeingDTOList dto = new HumanBeingDTOList((humanBeingMapper.mapHumanBeingListToHumanBeingDTOList(pagedHumanBeingList.getHumanBeingList())), pagedHumanBeingList.getCount());
            response.getWriter().write(gson.toJson(dto));
            return;
        }
        if (uniqueImpactSpeed != null){
            criteriaQuery.select(from.get("impactSpeed")).distinct(true);
            List<HumanBeing> humanBeingList = em.createQuery(criteriaQuery).getResultList();
            response.getWriter().write(gson.toJson(humanBeingList));
        }

    }

    protected void cors(HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, HEAD, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.addHeader("Access-Control-Allow-Credentials", "true");
    }
}