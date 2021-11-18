package validation;

import exceptions.EntityIsNotValidException;
import models.Car;
import models.Coordinates;
import models.HumanBeing;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class EntityValidator {
    private Validator validator;

    public EntityValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private String formExceptionMsg(Set<ConstraintViolation<Object>> constraintViolations) {
        String errorMessage = "";
        for (ConstraintViolation<Object> violation : constraintViolations) {
            errorMessage = errorMessage.concat(violation.getMessage() + "\n");
        }
        return errorMessage;
    }

    public void validateCar(Car car) throws EntityIsNotValidException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(car);
        if (!constraintViolations.isEmpty())
            throw new EntityIsNotValidException(formExceptionMsg(constraintViolations));
    }

    public void validateCoordinates(Coordinates coordinates) throws EntityIsNotValidException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(coordinates);
        if (!constraintViolations.isEmpty())
            throw new EntityIsNotValidException(formExceptionMsg(constraintViolations));
    }

    public void validateHumanBeing(HumanBeing humanBeing) throws EntityIsNotValidException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(humanBeing);
        if (!constraintViolations.isEmpty())
            throw new EntityIsNotValidException(formExceptionMsg(constraintViolations));
    }


}