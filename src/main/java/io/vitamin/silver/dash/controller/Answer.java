package io.vitamin.silver.dash.controller;

public class Answer {

    private final String contentType;
    private final HttpStatus status;
    private final String body;

    public Answer(HttpStatus status, String body){
        this(status, body, Params.JSON_CONTENT);
    }

    public Answer(HttpStatus status) {
        this(status, "", Params.JSON_CONTENT);
    }

    public Answer(HttpStatus status, String body, String contentType) {
        this.contentType = contentType;
        this.status = status;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Answer answer = (Answer) o;

        if (status != answer.status) return false;
        if (body != null ? !body.equals(answer.body) : answer.body != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = status.intValue();
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Answer(code=" + status + ", body=" + body + ", contentType="+ contentType +")";
    }

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return status.intValue();
    }

    public String getContentType() {
        return contentType;
    }

    public static Answer ok(String body) {
        return new Answer(HttpStatus.OK, body);
    }
}
