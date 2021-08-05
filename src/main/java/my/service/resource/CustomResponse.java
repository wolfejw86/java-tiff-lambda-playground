package my.service.resource;

public class CustomResponse<T> {
    String result;
    String message;
    T customField;

    public CustomResponse(String result, String message, T customField) {
        this.result = result;
        this.message = message;
        this.customField = customField;
    }
    //getter setter
}