package ee.anu.server;

public enum Status {

    OK(200, "OK"),
    BadRequest(400, "Bad Request"),
    NotFound(404, "Not Found"),
    InternalError(500, "Internal Server Error");

    private int code;
    private String message;

    Status(int code, String message) {

        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
