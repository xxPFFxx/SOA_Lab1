package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dto.HumanBeingDTO;
import dto.PagedHumanBeingList;
import dto.dtoList.HumanBeingDTOList;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import mapper.HumanBeingMapper;
import models.HumanBeing;
import repository.implementation.CrudRepositoryImplementation;
import validation.EntityValidator;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HumanBeingService {

    private CrudRepositoryImplementation<HumanBeing> repository;
    private EntityValidator entityValidator;
    private HumanBeingMapper humanBeingMapper;
    private Gson gson;

    public HumanBeingService(){
        repository = new CrudRepositoryImplementation<>(HumanBeing.class);
        entityValidator = new EntityValidator();
        humanBeingMapper = new HumanBeingMapper();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void updateHumanBeing(String requestBody, String pathInfo, HttpServletResponse resp) throws IOException {
        try {
            HumanBeingDTO humanBeingDTO = gson.fromJson(requestBody, HumanBeingDTO.class);
            HumanBeing humanBeingToUpdate = humanBeingMapper.mapHumanBeingDTOToHumanBeing(humanBeingDTO);
            entityValidator.validateHumanBeing(humanBeingToUpdate);
            entityValidator.validateCoordinates(humanBeingToUpdate.getCoordinates());
            entityValidator.validateCar(humanBeingToUpdate.getCar());

            String id = null;
            if (pathInfo != null)
                id = pathInfo.substring(1);

            if (id != null) {
                try {
                    long intId = Long.parseLong(id);
                    repository.findById((int) intId);
                    humanBeingToUpdate.setId(Long.parseLong(id));
                    repository.update(humanBeingToUpdate);
                    // Почему-то ошибки нормально в PUT не обрабатываются, пришлось так сделать
                } catch (NumberFormatException e) {
//                    throw new BadRequestException("Bad format of id: " + id + ", should be natural number (1,2,...)");
                    resp.setStatus(400);
                    resp.getWriter().println("Bad format of id: " + id + ", should be natural number (1,2,...)");
                } catch (NoResultException e) {
//                    throw new NotFoundException("No HumanBeing with id " + id);
                    resp.setStatus(400);
                    resp.getWriter().println("No HumanBeing with id " + id);
                }
            }
        }catch (JsonSyntaxException | NotFoundException | IOException e){
//            throw new NotFoundException("Bad syntax of JSON body");
            resp.setStatus(400);
            resp.getWriter().println("Bad syntax of JSON body");
        }
        catch (BadRequestException e){
            resp.setStatus(400);
            resp.getWriter().println(e.getMessage());
        }

    }

    public void saveHumanBeing(String requestBody){
        try {
        HumanBeingDTO humanBeingDTO = gson.fromJson(requestBody, HumanBeingDTO.class);
        HumanBeing humanBeingToPersist = humanBeingMapper.mapHumanBeingDTOToHumanBeing(humanBeingDTO);
        entityValidator.validateHumanBeing(humanBeingToPersist);
        entityValidator.validateCoordinates(humanBeingToPersist.getCoordinates());
        entityValidator.validateCar(humanBeingToPersist.getCar());
        repository.save(humanBeingToPersist);
    }catch (JsonSyntaxException | NotFoundException e){
        throw new BadRequestException("Bad syntax of JSON body");
    }
    }

    public void deleteHumanBeing(String pathInfo, HttpServletResponse resp) throws IOException {
        String humanBeingId = null;
        if (pathInfo != null)
            humanBeingId = pathInfo.substring(1);
        String finalHumanBeingId = humanBeingId;
        try {
            HumanBeing humanBeing = (repository.findById(Integer.parseInt(humanBeingId))).orElseThrow(() -> new NotFoundException("humanBeing with id = " + finalHumanBeingId + " does not exist"));
            repository.deleteById(Integer.parseInt(humanBeingId));
            //                  Почему-то ошибки нормально в DELETE не обрабатываются, пришлось так сделать
        } catch (NumberFormatException e) {
//                throw new BadRequestException("Bad format of id: " + humanBeingId + ", should be natural number (1,2,...)");
            resp.setStatus(400);
            resp.getWriter().println("Bad format of id: " + humanBeingId + ", should be natural number (1,2,...)");

        } catch (NoResultException e) {
//                throw new NotFoundException("No HumanBeing with id " + humanBeingId);
            resp.setStatus(404);
            resp.getWriter().println("No HumanBeing with id " + humanBeingId);
        }
    }

    public void getHumanBeing(String pathInfo, HttpServletResponse response, String perPage, String curPage, String sortBy, String filterBy) throws IOException {
        String id = null;
        if (pathInfo != null)
            id = pathInfo.substring(1);

        if (id != null) {
            String finalId = id;
            try {
                int intId = Integer.parseInt(id);
                if (intId <= 0) throw new BadRequestException("Bad format of id: " + id + ", should be natural number (1,2,...)");
                HumanBeing humanBeing = (repository.findById(intId)).orElseThrow(() -> new NotFoundException("humanBeing with id = " + finalId + " does not exist"));
                HumanBeingDTO humanBeingDTO = humanBeingMapper.mapHumanBeingToHumanBeingDTO(humanBeing);
                response.getWriter().write(gson.toJson(humanBeingDTO));
                return;
            }catch (NumberFormatException e){
                throw new BadRequestException("Bad format of id: " + id + ", should be natural number (1,2,...)");
            }catch (NoResultException e){
                throw new NotFoundException("No HumanBeing with id " + id);
            }
        }
        PagedHumanBeingList pagedHumanBeingList = repository.findAll(perPage, curPage, sortBy, filterBy);
        HumanBeingDTOList dto = new HumanBeingDTOList((humanBeingMapper.mapHumanBeingListToHumanBeingDTOList(pagedHumanBeingList.getHumanBeingList())), pagedHumanBeingList.getCount());
        response.getWriter().write(gson.toJson(dto));
        repository.clearEntityManager();
    }
}
