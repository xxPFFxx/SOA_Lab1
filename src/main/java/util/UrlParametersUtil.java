package util;

import javax.servlet.http.HttpServletRequest;

public class UrlParametersUtil {
    public static String getField(HttpServletRequest request, String fieldName) {
        String fieldValue = request.getParameter(fieldName);
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            fieldValue = null;
        }
        return fieldValue;
    }

    public static Integer parseInteger(String param) throws NumberFormatException {
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse Integer from " + param);
        }
    }

}